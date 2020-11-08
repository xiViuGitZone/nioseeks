package com.tends.nioseeks.netty;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * netty服务端启动加载配置
 * 添加初始化时候加载netty配置文件，实现了ApplicationListener接口，这样springboot容器启动完毕就可以加载netty的相关配置信息
 */
@Component
public class NettybootServerInitConfig implements ApplicationListener<ContextRefreshedEvent>{

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(event.getApplicationContext().getParent() == null){
            WssServer.getInstance().start();
        }
    }

}
