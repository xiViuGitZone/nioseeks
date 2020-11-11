//package com.tends.nioseeks.pojo;
//import com.tends.nioseeks.netty.service.NettyDiscardService;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelHandler.Sharable;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
//import io.netty.handler.codec.http.websocketx.WebSocketFrame;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Sharable  //handler已是一个IOC管理的Bean可自由使用依赖注入等Spring的快捷功能。
//@Component //由于是单例存在,所有链接都使用同一个hander，所以尽量不要保存任何实例变量
//public class NettyWebsocketMsgHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
//    private final Logger log = LoggerFactory.getLogger(getClass());
//    @Autowired
//    NettyDiscardService nettyDiscardService;
//
//
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
//        if (msg instanceof TextWebSocketFrame) {
//            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
//            // 业务层处理数据
//            this.nettyDiscardService.discardMsg(textWebSocketFrame.text());
//            // 响应客户端
//            ctx.channel().writeAndFlush(new TextWebSocketFrame("我收到了你的消息：" + System.currentTimeMillis()));
//        } else {
//            // 不接受文本以外的数据帧类型
//            ctx.channel().writeAndFlush(WebSocketCloseStatus.INVALID_MESSAGE_TYPE).addListener(ChannelFutureListener.CLOSE);
//        }
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);
//        log.info("链接断开：{}", ctx.channel().remoteAddress());
//    }
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
//        log.info("链接创建：{}", ctx.channel().remoteAddress());
//    }
//}
