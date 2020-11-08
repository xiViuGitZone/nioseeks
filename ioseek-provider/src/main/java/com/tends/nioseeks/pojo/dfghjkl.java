//package com.tends.nioseeks.pojo;
//
//import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.nio.channels.SocketChannel;
//import java.util.concurrent.TimeUnit;
//
//@Component
//@Slf4j     //Netty 客户端
//public class NettyClient  {
//    private EventLoopGroup group = new NioEventLoopGroup();
//    @Value("${netty.port}")
//    private int port;
//    @Value("${netty.host}")
//    private String host;
//    private SocketChannel socketChannel;
//
//    public void sendMsg(MessageBase.Message message) {
//        socketChannel.writeAndFlush(message);
//    }
//
//    @PostConstruct
//    public void start()  {
//        Bootstrap bootstrap = new Bootstrap();
//        bootstrap.group(group)
//                .channel(NioSocketChannel.class)
//                .remoteAddress(host, port)
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                .option(ChannelOption.TCP_NODELAY, true)
//                .handler(new ClientHandlerInitilizer());
//        ChannelFuture future = bootstrap.connect();
//        //客户端断线重连逻辑
//        future.addListener((ChannelFutureListener) future1 -> {
//            if (future1.isSuccess()) {
//                log.info("连接Netty服务端成功");
//            } else {
//                log.info("连接失败，进行断线重连");
//                future1.channel().eventLoop().schedule(() -> start(), 20, TimeUnit.SECONDS);
//            }
//        });
//        socketChannel = (SocketChannel) future.channel();
//    }
//}
