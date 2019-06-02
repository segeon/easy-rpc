package com.segeon.easyrpc.core.domain.invocation.server;

import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.core.netty.RPCContext;

import java.util.concurrent.*;

public class InvocationExecutor {

    private ApplicationConfig config;
    private ExecutorService executorService;

    public InvocationExecutor(ApplicationConfig config) {
        this.config = config;
        this.executorService = new ThreadPoolExecutor(10, 100, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                ((InvocationRunnable) r).rejectExecution();
            }
        });
    }

    public void execute(RPCContext context) {
        executorService.submit(new InvocationRunnable(context, config));
    }
}
