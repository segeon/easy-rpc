package com.segeon.easyrpc.etcd;

import com.segeon.easyrpc.core.domain.entity.Endpoint;
import com.segeon.easyrpc.core.domain.entity.ReferenceConfig;
import com.segeon.easyrpc.core.domain.entity.RemotingService;
import com.segeon.easyrpc.core.domain.entity.ServiceKey;
import com.segeon.easyrpc.core.domain.entity.impl.AbstractServiceRegistry;
import com.segeon.easyrpc.core.domain.entity.impl.SimpleEndpoint;
import com.segeon.easyrpc.core.domain.entity.impl.SimpleServiceKey;
import com.segeon.easyrpc.core.domain.exception.RPCClientException;
import com.segeon.easyrpc.core.domain.exception.RegistrationException;
import com.segeon.easyrpc.core.utils.HessianUtil;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Slf4j
public class EtcdRegistry extends AbstractServiceRegistry {
    public static int SUGGEST_LEASE_SECONDS = 10;

    private Client client;
    private volatile boolean leased = false;
    private volatile Long leaseSeconds;
    private volatile Long leaseId;
    private Endpoint endpoint;
    private ByteSequence endpointByteSequence;
    private ConcurrentMap<ServiceKey, Long> serviceKey2WatchId;

    @Override
    public void register(RemotingService remotingService) {
        if (!leased) {
            applyLease();
        }
        PutOption option = PutOption.newBuilder().withLeaseId(leaseId).build();
        try {
            client.getKVClient().put(buildEtcdKey(remotingService, endpoint), endpointByteSequence, option).get();
        } catch (InterruptedException|ExecutionException e) {
            throw new RegistrationException("注册服务异常: " + remotingService, e);
        }
    }

    private synchronized void applyLease() {
        LeaseGrantResponse leaseGrantResponse = null;
        try {
            leaseGrantResponse = client.getLeaseClient().grant(SUGGEST_LEASE_SECONDS).get();
            leaseId = leaseGrantResponse.getID();
            leaseSeconds = leaseGrantResponse.getTTL();
            client.getLeaseClient().keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
                @Override
                public void onNext(LeaseKeepAliveResponse leaseKeepAliveResponse) {
                    log.debug("LeaseKeepAliveResponse: ttl={}", leaseKeepAliveResponse.getTTL());
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error("keepAlive error. 尝试重新续约！", throwable);
                    applyLease();
                }

                @Override
                public void onCompleted() {
                    log.debug("LeaseKeepAliveResponse completed!");
                }
            });
        } catch (InterruptedException|ExecutionException e) {
            throw new RegistrationException("从Etcd服务器申请租约异常！", e);
        }
    }

    private void revokeLease() {
        client.getLeaseClient().revoke(leaseId);
    }

    @Override
    public void unRegister(RemotingService remotingService) {
        try {
            client.getKVClient().delete(buildEtcdKey(remotingService, endpoint)).get();
        } catch (InterruptedException|ExecutionException e) {
            throw new RegistrationException("取消注册服务异常: " + remotingService);
        }
    }

    @Override
    protected void doInit() {
        this.client = Client.builder().endpoints("http://" + config.getRegistryConfig().getIp() + ":" + config.getRegistryConfig().getPort()).build();
        this.endpoint = new SimpleEndpoint(config.getLocalIp(), config.getPort());
        try {
            this.endpointByteSequence = ByteSequence.from(HessianUtil.serialize(endpoint).array());
        } catch (IOException e) {
            throw new RegistrationException("序列化Endpoint异常: " + endpoint, e);
        }
        this.serviceKey2WatchId = new ConcurrentHashMap<>();
    }

    @Override
    protected void doSubscribe(ServiceKey remotingService) {
        ByteSequence prefix = buildEtcdKeyPrefix(remotingService);
        WatchOption watchOption = WatchOption.newBuilder().withPrefix(prefix).build();
        this.client.getWatchClient().watch(prefix, watchOption, new Watch.Listener() {
            @Override
            public void onNext(WatchResponse response) {
                ArrayList<Endpoint> endpoints = new ArrayList<>(64);
                try {
                    for (WatchEvent event : response.getEvents()) {
                        if (event.getKeyValue() != null && event.getKeyValue().getValue() != null) {
                            endpoints.add(parseValue(event.getKeyValue().getValue()));
                        }
                    }
                    EtcdRegistry.this.onEndpointsChange(remotingService, endpoints);
                } catch (Exception e) {
                    log.error("读取watch消息异常: {}", response, e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("watch异常! ServiceKey={}", remotingService, throwable);
            }

            @Override
            public void onCompleted() {
                log.info("watch结束。ServiceKey={}", remotingService);
            }
        });
    }

    @Override
    protected void doUnsubscribe(ServiceKey config) {

    }

    @Override
    public String registryType() {
        return "etcd";
    }

    @Override
    public List<Endpoint> list(ReferenceConfig remotingService) {
        SimpleServiceKey serviceKey = SimpleServiceKey.fromReferenceConfig(remotingService);
        try {
            ByteSequence key = ByteSequence.from(buildEtcdKeyPrefix(serviceKey).getBytes());
            GetOption option = GetOption.newBuilder().withPrefix(key).build();
            GetResponse response = client.getKVClient().get(key, option).get();
            ArrayList<Endpoint> list = new ArrayList<Endpoint>((int)response.getCount());
            for (KeyValue keyValue : response.getKvs()) {
                SimpleEndpoint endpoint = parseValue(keyValue.getValue());
                list.add(endpoint);
            }
            return list;
        } catch (InterruptedException|ExecutionException e) {
            throw new RPCClientException("从etcd获取provider列表异常: " + serviceKey.toString(), e);
        } catch (IOException e) {
            throw new RPCClientException("从etcd反序列化provider列表异常: " + serviceKey.toString(), e);
        }
    }

    private ByteSequence buildEtcdKey(ServiceKey serviceKey, Endpoint endpoint) {
        StringBuilder builder = new StringBuilder(128);
        builder.append("easyrpc:svc:").append(serviceKey.interfaceType().getCanonicalName())
                .append(":").append(serviceKey.group()).append(":").append(serviceKey.version())
                .append(":").append(endpoint.ip()).append(":").append(endpoint.port());
        return ByteSequence.from(builder.toString(), StandardCharsets.UTF_8);
    }

    private ByteSequence buildEtcdKeyPrefix(ServiceKey serviceKey) {
        StringBuilder builder = new StringBuilder(128);
        builder.append("easyrpc:svc:").append(serviceKey.interfaceType().getCanonicalName())
                .append(":").append(serviceKey.group()).append(":").append(serviceKey.version());
        return ByteSequence.from(builder.toString(), StandardCharsets.UTF_8);
    }

    private SimpleEndpoint parseValue(ByteSequence byteSequence) throws IOException {
        return (SimpleEndpoint) HessianUtil.deserialize(byteSequence.getBytes());
    }

    @Override
    public void close() {
        if (leaseId != null) {
            revokeLease();
            leaseId = null;
        }
    }
}
