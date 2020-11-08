package com.tends.nioseeks.netty;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.time.LocalDateTime;

//聊天的ehandler  TextWebSocketFrame  用于为websockt处理文本的对象
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

    //用于记录和管理所有客户端的channel
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //@Override
    protected void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg)
            throws Exception {
        //客户端传递过来的消息
        String content = msg.text();
        System.out.println("接收到了客户端的消息是:" + content);

        //将客户端发送过来的消息刷到所有的channel中
        for(Channel channel : clients){
            //channel.writeAndFlush(msg);
            channel.writeAndFlush(
                    new TextWebSocketFrame("[服务器接收到了客户端的消息:]" + LocalDateTime.now()+",消息为:" + content));
        }

//		clients.writeAndFlush(
//				new TextWebSocketFrame("[服务器接收到了客户端的消息:]" + LocalDateTime.now()+",消息为:" + content));

    }

    //客户端创建的时候触发，当客户端连接上服务端之后，就可以获取该channel，然后放到channelGroup中进行统一管理
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    //客户端销毁的时候触发，
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当handlerRemoved 被触发时候，channelGroup会自动移除对应的channel
        //clients.remove(ctx.channel());
        System.out.println("客户端断开，当前被移除的channel的短ID是：" +ctx.channel().id().asShortText());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {

    }
}
