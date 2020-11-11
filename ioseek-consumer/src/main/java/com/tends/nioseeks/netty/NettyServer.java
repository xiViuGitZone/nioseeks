package com.tends.nioseeks.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j       //Netty 服务端
public class NettyServer {
    @Value("${netty.port}")
    private Integer nettyPort;
    //初始化用于Acceptor的主"线程池" 以及 用于I/O工作的从"线程池"
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();


    //启动服务器方法
    public void run() {
        //初始化ServerBootstrap实例，它是netty服务端应用开发的入口(netty引导程序,简化开发)
        //ServerBootstrap中定义了服务端React的"从线程池"对应的相关配置，都是以child开头的属性
        //而用于"主线程池"channel的属性都定义在AbstractBootstrap中
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap.group(bossGroup, workerGroup);  //通过group()，初始化的主从"线程池"
            //使用指定的端口设置套接字地址
            //serverBootstrap.localAddress(new InetSocketAddress(nettyPort));
            serverBootstrap.channel(NioServerSocketChannel.class); //指定channel类型，服务端是NioServerSocketChannel
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO)); //设置ServerSocketChannel的处理器
            //设置子通道也就是SocketChannel的处理器， 其内部是具体业务开发的逻辑
            serverBootstrap.childHandler(new NettyServerInitializer());

            // 服务端TCP内核模块维护2个队列,三次握手后accept从队列2中取出完成三次握手的连接,队列1和队列2的长度之和是backlog
            // 服务端处理客户端连接请求是顺序处理的,backlog对程序支持的连接数无影响,它影响的只是还没有被accept 取出的连接
            // 标识服务器请求处理线程全满时,临时存放已完成三次握手的请求的队列最大长度,如未设置或值小于1,Java用默认值50
            //serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024); //配置ServerSocketChannel的选项
            // 开启Nagle算法要求高实时性发送数据（true）,要减少网络交互发送次数等累积一定大小再发送（false默认）,即TCP延迟传输
            serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);//配置子通道也就是SocketChannel的选项
            // 是否启用心跳保活机制,在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）
            // 一般两个小时内无数据通信时(上层无任何数据传输的情况),TCP会自动发送一个活动探测数据报文(机制被激活)
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //// 定义接收或者传输的系统缓冲区buf的大小
            //serverBootstrap.childOption(ChannelOption.SO_RCVBUF, 1024);
            //serverBootstrap.childOption(ChannelOption.SO_SNDBUF, 1024);   
            //// Netty4使用对象池，重用缓冲区
            //serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            //serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            serverBootstrap.option(ChannelOption.SO_REUSEADDR, true); //允许重复使用端口

            // 绑定并侦听指定端口,开始接收进来的连接 
            ChannelFuture channelFuture = serverBootstrap.bind(nettyPort).sync();
            log.info("netty服务启动: [nettyPort:" + nettyPort + "]");
            channelFuture.channel().closeFuture().sync(); // 等待服务器socket关闭

        } catch (Exception e) {
            log.error("netty服务启动异常: 【{}】", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
