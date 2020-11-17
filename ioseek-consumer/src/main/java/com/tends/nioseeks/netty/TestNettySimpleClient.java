package com.tends.nioseeks.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

//Netty 客户端
public class TestNettySimpleClient {
    private Logger log = LoggerFactory.getLogger(getClass());
    private EventLoopGroup workGroup  = new NioEventLoopGroup(); //初始化用于连接及I/O工作的"主线程池"
    private String host;
    private int port;


    public TestNettySimpleClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //连接方法
    public void connect(String users, String msgs) {
        Bootstrap bootstrap = new Bootstrap(); //实例化netty客户端应用开发的入口

        try {
            bootstrap.group(workGroup)  //设置初始化的"线程池"
                    //.remoteAddress(host, port)  //设置服务端IP、端口
                    .channel(NioSocketChannel.class); //指定channel类型，客户端是NioSocketChannel
            // 是否开启Nagle算法,若要求高实时性发送数据（true）若要减少网络交互发送次数等累积一定大小后再发送,（false默认）
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 是否启用心跳保活机制,在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）
            // 并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(initChildNettyClient()); //设置SocketChannel的处理器，实际业务处理

            //连接指定的服务地址
            Channel channel = bootstrap.connect(host, port).sync().channel();
            ////客户端断线重连逻辑
            //ChannelFuture future = bootstrap.connect();
            //future.addListener((ChannelFutureListener) future1 -> {
            //    if (future1.isSuccess()) {
            //        log.info("连接Netty服务端成功");
            //    } else {
            //        log.info("连接失败，进行断线重连");
            //        future1.channel().eventLoop().schedule(() -> connect(users, msgs), 20, TimeUnit.SECONDS);
            //    }
            //});
            //SocketChannel sc = (SocketChannel) future.channel();

            String msg = "{\"name\":\""+ users +"\",\"age\":" + msgs +"}\n";
            // 发送json字符串
            //String msg = "{\"name\":\"admin\",\"age\":27}\n";
            channel.writeAndFlush(msg);
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("客户端连接服务端异常。。。。。。", e);
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    //客户端Channel初始化
    private static ChannelInitializer<SocketChannel> initChildNettyClient() {
        final int MAX_FRAMEL_LEN = 8192;
        return new ChannelInitializer<SocketChannel> (){
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new DelimiterBasedFrameDecoder(MAX_FRAMEL_LEN, Delimiters.lineDelimiter()));
                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(initChannelHandler());
            }
        };
    }
    //客户端Channel初始化处理器Handler
    private static SimpleChannelInboundHandler initChannelHandler() {
        return new SimpleChannelInboundHandler(){
            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                System.out.println("收到服务端消息: " + o);
            }
        };
    }



    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 7777;
        TestNettySimpleClient nettyClient = new TestNettySimpleClient(host, port);

        Scanner scn = new Scanner(System.in);
        String strs = scn.nextLine();
        String[] ssarr = strs.split(",");
        nettyClient.connect(ssarr[0], ssarr[1]);
        //nettyClient.connect();
    }

}