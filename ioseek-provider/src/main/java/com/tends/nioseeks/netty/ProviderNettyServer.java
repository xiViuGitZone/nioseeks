package com.tends.nioseeks.netty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component   //服务端基本配置
//public class ProviderNettyServer extends Thread implements ApplicationListener<ContextRefreshedEvent> {
public class ProviderNettyServer implements ApplicationRunner,ApplicationListener<ContextClosedEvent>, ApplicationContextAware {
    private Logger log = LoggerFactory.getLogger(getClass());
    // 实际是两个Reactor线程组，一个用于服务端接收客户端的连接，一个用于进行socketChannel的网络读写
    private EventLoopGroup acceptors = new NioEventLoopGroup();//只维护accept接入新连接的SelectionKey
    private EventLoopGroup workers = new NioEventLoopGroup();  //只维护客户端的SelectionKey

    private ApplicationContext applicationContext;//通过ApplicationContextAware获得ApplicationContext
    private SocketChannel sc = null;  // 保存客户端连接的通道引用
    @Value("${netty.port}")
    private Integer nettyPort;
    @Value("${netty.host}")
    private String ip;
    @Value("${netty.websocket.path}")
    private String path;
    @Value("${netty.websocket.max-frame-size}")
    private int maxFrameSize;

    ////private boolean runned = true;
    ////@PostConstruct
    ////public void init() throws InterruptedException {
    ////    this.start();
    ////    log.info("-----------------------  NettyServer启动成功！");
    ////}
    ////@PreDestroy
    ////public void exit() {
    ////    log.info("-----------------------  NettyServer 进入销毁阶段!");
    ////    acceptors.shutdownGracefully();
    ////    workers.shutdownGracefully();
    ////}
    ////
    //////单例静态内部类，单例类保证启动时候只被加载一次
    ////public static class SingletionWSServer {
    ////    static final ProviderNettyServer instance = new ProviderNettyServer();
    ////}
    ////public static ProviderNettyServer getInstance() {
    ////    return SingletionWSServer.instance;
    ////}
    //
    //@Override
    //public void run() {
    //    //netty用于启动NIO服务端的辅助启动类，降低服务端的开发复杂度
    //    ServerBootstrap serverBootstrap = new ServerBootstrap();
    //    serverBootstrap.group(acceptors, workers);  // 添加boss和worker组
    //
    //    //用于构造socketchannel工厂,设置创建的Channel为NioServerSocketChannel
    //    serverBootstrap.channel(NioServerSocketChannel.class);
    //    serverBootstrap.localAddress(this.nettyPort);  // 绑定监听端口
    //    //指定允许等待 accept 的最大连接数量,测试只要连一个客户端可不设置,java默认是50个
    //    // bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
    //    serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
    //    //传入自定义客户端Handle（处理消息） 绑定I/O事件的处理类
    //    serverBootstrap.childHandler(getMyChannelInitializer());
    //
    //    try {
    //        // 绑定端口，开始接收进来的连接
    //        //ChannelFuture f = bootstrap.bind(nettyPort).sync();
    //        ChannelFuture f = serverBootstrap.bind().sync();
    //        if (f != null && f.isSuccess()) {
    //            log.info("NettyServer-listening[Host]:【{}】 and [port]:【{}】 are ready for connections...", ip, nettyPort);
    //        }
    //        // 等待服务器socket关闭  关闭服务器通道
    //        f.channel().closeFuture().sync();
    //    } catch (Exception e) {
    //        log.error("~~~~~~~~~~~~~~~~~~~~~~~~~~~Provider-Netty 通信异常，log信息：", e);
    //    } finally {
    //        log.info("-----------------------  NettyServer 进入销毁阶段!");
    //        acceptors.shutdownGracefully();  // 释放线程池资源
    //        workers.shutdownGracefully();
    //    }
    //}
    //@Override
    //public void onApplicationEvent(ContextRefreshedEvent event) {
    //    // netty服务端启动初始化netty配置,实现了ApplicationListener接口,这样spring容器启动完就会加载netty相关配置
    //    //if(event.getApplicationContext().getParent() == null){
    //        ProviderNettyServer.getInstance().start(); //为了保证服务启动类不会受其他类干扰，直接使用单例
    //        //this.start();
    //        log.info("-----------------------  NettyServer启动成功！");
    //    //}
    //}


    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    @Override //初始化Netty服务,监听APP启动和关闭时执行Neetty-Websocket服务
    public void run(ApplicationArguments args) throws Exception {
        //EventLoopGroup acceptors = new NioEventLoopGroup();
        //EventLoopGroup workers = new NioEventLoopGroup();
        //netty用于启动NIO服务端的辅助启动类，降低服务端的开发复杂度
        //ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(acceptors, workers);  // 添加boss和worker组
            //用于构造socketchannel工厂,设置创建的Channel为NioServerSocketChannel
            serverBootstrap.channel(NioServerSocketChannel.class);
            //serverBootstrap.localAddress(new InetSocketAddress(ip, nettyPort));
            serverBootstrap.localAddress(this.nettyPort);
            //传入自定义客户端Handle（处理消息） 绑定I/O事件的处理类
            serverBootstrap.childHandler(getMyChannelInitializer());
            //serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.option(ChannelOption.TCP_NODELAY, true);

            this.start();  //启动服务
        } catch (Exception e) {
            log.error("~~~~~~~~~~~~~~~~~~~~~~~~~~~Provider-Netty 服务通信异常，启动异常log信息：", e);
        } finally {
            acceptors.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }
    private void start() throws Exception {  //websocket 服务启动
        ChannelFuture cf = serverBootstrap.bind().sync();
        log.info("websocket 服务启动，ip={},port={}", ip, nettyPort);

        System.out.println(getClass() + " 启动正在监听： " + cf.channel().localAddress());
        cf.channel().closeFuture().sync();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.warn("-----------------------  NettyServer 进入销毁阶段!");
        if (this.sc != null)   this.sc.close();
        log.warn("Netty-websocket 服务停止");
    }
    // 通过ApplicationContextAware还可获得ApplicationContext
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 配置channel初始化所有的handler
    private ChannelInitializer<SocketChannel> getMyChannelInitializer(){
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                if (sc != null) {
                    socketChannel.close();
                    return;
                }
                sc = socketChannel;
                log.info("!!!!!!!!!!! 来自" + socketChannel.remoteAddress() + "的新连接接入");

                ChannelPipeline pipeline = socketChannel.pipeline();
                // 用于支持http协议的3个Handler
                pipeline.addLast(new HttpServerCodec());    //websocket基于http协议，所以需要http编解码器
                pipeline.addLast(new ChunkedWriteHandler());//添加对于读写大数据流的支持
                pipeline.addLast(new HttpObjectAggregator(8192));//对httpMessage进行聚合
                // 注册、自定义handler
                //pipeline.addLast(new ReadTimeoutHandler(20)); //20s超时
                //pipeline.addLast(applicationContext.getBean(ProviderNettyChatHandler.class));//从IOC获取Handler
                pipeline.addLast(new ProviderNettyChatHandler(sc, path));  //自定义的客户端业务消息Handler
                //websocket服务器处理的协议，用于给指定的客户端进行连接访问的路由地址,比如处理一些握手动作(ping,pong)
                //pipeline.addLast(new WebSocketServerProtocolHandler(path));//注意ProviderNettyChatHandler的顺序
                //pipeline.addLast(new WebSocketServerCompressionHandler());  //subprotocols:"WebSocket"
                pipeline.addLast(new WebSocketServerProtocolHandler(path, null, true, maxFrameSize));

                //// 解码编码  编解码器的顺序不要错，否则会造成无法解码的情况
                //// pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
                //pipeline.addLast(new NettyMsgDecoder());
                //// pipeline.addLast(new LengthFieldPrepender(2));
                //pipeline.addLast(new NettyMsgEncoder());
                //pipeline.addLast(new IdleStateHandler(Const.READER_IDLE_TIME_SECONDS, 0, 0));
                //pipeline.addLast(new ServerHandler());
            }
        };
    }




}
