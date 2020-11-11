package com.tends.nioseeks.netty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener      //Netty 服务监听器
@Slf4j
public class NettyServerListener implements ServletContextListener {
    @Autowired
    private NettyServer nettyServer;   //注入NettyServer


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("ServletContex初始化...");  
        //Thread thread = new Thread(new NettyServerThread());
        Thread thread = new Thread(() -> {
            nettyServer.run(); //Netty 服务启动线程
        } );
        thread.start();  // 启动netty服务
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    ////Netty 服务启动线程
    //private class NettyServerThread implements Runnable {
    //    @Override
    //    public void run() {
    //        nettyServer.run();
    //    }
    //}

}