package com.tends.nioseeks.netty;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;

@Component
@Slf4j       //Netty 服务端
public class TestNettySimpleServer {
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
            serverBootstrap.childHandler(initServerChildNettyClient());

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


    //服务端Channel初始化
    private static ChannelInitializer<SocketChannel> initServerChildNettyClient() {
        final int MAX_FRAMEL_LEN = 8192;
        return new ChannelInitializer<SocketChannel> (){
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new DelimiterBasedFrameDecoder(MAX_FRAMEL_LEN, Delimiters.lineDelimiter()));
                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(initServerChannelHandler());

                //// 定义分隔符为$$（字符串末尾分割）
                //ByteBuf delimiter = Unpooled.copiedBuffer("$$".getBytes());
                //// 添加分隔符解码器，通过分隔符来解决拆包粘包的问题
                //pipeline.addLast(new DelimiterBasedFrameDecoder(2048, delimiter));
                //// 自定义解码器，用来获取数据并做持久化处理
                //pipeline.addLast(demoderServerHandler);
            }
        };
    }

    //服务端Channel初始化处理器Handler
    private static SimpleChannelInboundHandler<String> initServerChannelHandler() {
        return new SimpleChannelInboundHandler<String>(){
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                StringBuilder sb = new StringBuilder();
                Map<String, Object> result = null;
                try {
                    // 报文解析处理
                    result = JSON.parseObject(msg);
                    sb.append(result);
                    sb.append("解析成功\n");

                    ctx.writeAndFlush(sb);
                } catch (Exception e) {
                    log.error("报文解析失败，异常信息: 【{}】", e);
                    String errorCode = "读取失败！ 解析异常 -1\n";
                    ctx.writeAndFlush(errorCode);
                }
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
                String clientIp = insocket.getAddress().getHostAddress();
                log.info("收到客户端[ip:" + clientIp + "]连接");
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                // 当出现异常就关闭连接
                InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
                String clientIp = insocket.getAddress().getHostAddress();
                log.info("客户端[ip:" + clientIp + "]连接出现异常，服务器主动关闭连接。。。", cause);
                ctx.close();
            }
        };
    }














}
