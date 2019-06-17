# 一个简单的RPC框架实现: easyrpc
本框架在概念和设计思想上借鉴了Dubbo，尽量用最简单的方式实现了一个可用的RPC框架，其目的主要用于个人学习。

## 功能特点
+ 基于netty4的网络通信
+ 使用hessian进行序列化/反序列化
+ 基于etcd的服务注册、发现
+ 服务支持分组和版本号，支持直连服务提供者

## 使用方式
### 注册中心
首先启动etcd，要求使用v3.3.4以上版本

### 服务提供者
#### 注册一个ApplicationConfig bean


    @Configuration
    public class Config {
    
        @Bean
        public ApplicationConfig applicationConfig() {
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName("annotation-provider");
            applicationConfig.setPort(1124);
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setSchema("etcd");
            registryConfig.setIp("localhost");
            registryConfig.setPort(2379);
            applicationConfig.setRegistryConfig(registryConfig);
            applicationConfig.init();
            return applicationConfig;
        }
    }

#### 在想要提供远程服务的Bean上面增加`@RpcService`注解


    @Service
    @RpcService(interfaceType = HelloService.class)
    public class HelloServiceImpl implements HelloService {
    
        @Override
        public HelloResponse say(HelloRequest r) {
            log.info("received: {}", r);
            HelloResponse helloResponse = new HelloResponse();
            helloResponse.setReply("ok");
            helloResponse.setReplyTime(System.currentTimeMillis());
            //throw new RuntimeException("fake error");
            return helloResponse;
        }
    }

### 服务消费者
#### 使用`@RpcReference`注解注入想要引用的远程服务

    @Service
    @Slf4j
    public class DemoService {
        @RpcReference
        private HelloService helloService;
    
        public String sayHello(String msg) {
            HelloRequest helloRequest = new HelloRequest();
            helloRequest.setFrom("thh");
            helloRequest.setData(msg);
            helloRequest.setTime(System.currentTimeMillis());
            HelloResponse response = helloService.say(helloRequest);
            log.info("response: {}", response.getReply());
            return response.getReply();
        }
    }
