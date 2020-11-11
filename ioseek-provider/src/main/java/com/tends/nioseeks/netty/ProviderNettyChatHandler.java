package com.tends.nioseeks.netty;
import com.alibaba.fastjson.JSON;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//@ChannelHandler.Sharable   //聊天消息handler  TextWebSocketFrame用于为websockt处理文本的对象
//handler已是IOC管理的Bean可自由使用依赖注入等Spring快捷功能、因是单例存在,所有链接用同一个hander，所以尽量不要保存任何实例变量
public class ProviderNettyChatHandler extends SimpleChannelInboundHandler<WebSocketFrame>{
    private Logger log = LoggerFactory.getLogger(getClass());
    public static Map<String, Object> resMap = new HashMap<String, Object>();//保存response的map
    private static Map checkMap = new HashMap<>();
    //通道组池、用于记录和管理所有客户端的channel
    //每个chennel对应着唯一的长ID或者端ID，通过这层关系，将chennel和ID进行绑定；
    //针对每一个发送消息的客户端，在read0这个方法中截取消息，并将消息转发到指定的chennel中
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SocketChannel sc;
    private String path;


    public ProviderNettyChatHandler() { }
    public ProviderNettyChatHandler(SocketChannel socketChannel, String path) {
        this.sc = socketChannel;
        this.path = path;
    }

    @Override    //断开连接时触发Unregistered这个SelectionKey操作
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (this.sc != null) {
            log.info("-----------------------来自: {} 的连接主动断开", sc.remoteAddress());
            this.sc = null;
        }
        ctx.fireChannelUnregistered();  //做netty关闭通道的一些处理,并把连入的客户端置空
    }
    @Override    //处理异常信息
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //因为前面加Handler的顺序在ReadTimeoutHandler后面,所以ReadTimeoutHandler抛出的异常可以在这里被处理
        //如果是ReadTimeoutException,则记录超时断开的日志,否则打印出具体异常,关闭通道,并记录异常断开的日志
        if (cause.getClass() == io.netty.handler.timeout.ReadTimeoutException.class) {
            log.warn(">>>>>>>>>>>>>> 来自: {} 的连接超时断开", sc.remoteAddress());
        } else {
            log.info(">>>>>>>>>>>>>> 来自: {} 的连接异常,  异常信息：【{}】", sc.remoteAddress(), cause);
            ctx.close();
        }
        this.sc = null;
    }
    @Override    //信息获取完毕后操作,执行一下刷新
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //有时客户端发送信息后不会清空channel,这样就没有结束标识,read的SelectionKey不会触发
        ctx.flush();  //执行一下刷新
    }


    @Override    //读取客户端发送的信息
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ////可能是客户端发送的多条连接信息,所以要按照业务的逻辑切分开分别处理,处理后可通过传入的ctx写回复
        //ByteBuf msgByteBuf = (ByteBuf) msg;
        //byte[] msgBytes = new byte[msgByteBuf.readableBytes()];
        //msgByteBuf.readBytes(msgBytes); //msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
        //msgByteBuf.release();           //释放资源
        //List<byte[]> list = getMsgList(msgBytes);//返的msgByteBuf可能是多条信息拼起来的,将其拆开处理
        //list.forEach(v -> handler(v, ctx));     // 真正处理信息的方法

        try {
            if (null != msg && msg instanceof FullHttpRequest) {
                FullHttpRequest request = (FullHttpRequest) msg;
                String uri = request.uri();

                Map paramMap = getReqUrlParams(uri);
                log.info("接收到的参数是："+ JSON.toJSONString(paramMap));
                String newUri = "";

                if(uri.contains("?")){  //如果url包含参数，需要处理
                    newUri = uri.substring(0, uri.indexOf("?"));
                    log.info("url包含参数处理后： "+newUri);
                    request.setUri(newUri);
                }
                if (StringUtils.hasText(newUri) && (!StringUtils.hasText(path) || !newUri.equals(path))) {
                    // 访问的路径不是 websocket的端点地址，响应404
                    ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.NOT_FOUND)).addListener(ChannelFutureListener.CLOSE);
                    log.warn("-----------------------访问的路径不是websocket的端点地址[path]： 【{}】", uri);
                    return ;
                }
            //}else if(msg instanceof TextWebSocketFrame) {
            //} else {
            //    channelRead0(ctx, (TextWebSocketFrame)msg);
            }
        } catch (Exception e) {
            log.error("-----------------------读取channelRead客户端发送的信息异常：日志Log 【{}】", e);
        }
        super.channelRead(ctx, msg);
    }
    //封装ws的请求参数表
    private Map getReqUrlParams(String url){
        Map<String,String> map = new HashMap<String,String>();
        url = url.replace("?",";");
        if (!url.contains(";"))  return map;
        
        if (url.split(";").length > 0){
            String[] arr = url.split(";")[1].split("&");
            for (String s : arr){
                String key = s.split("=")[0];
                String value = String.valueOf(s.split("=")[1]);
                map.put(key, value);
            }
        }
        return map;
    }


    @Override //对应每一个连接的客户端channel，通过这个chennel获取客户端推送来的消息，并发送消息响应客户端
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame socketMsg) throws Exception {
        if (socketMsg instanceof TextWebSocketFrame) {
            String content = ((TextWebSocketFrame)socketMsg).text();//客户端传递过来的消息
            log.info("接收到了客户端的消息是:" + content);

            String msgInfo = "[服务器接收到了客户端的消息:] " + LocalDateTime.now() +", 消息为: " + content;
            //使用channel的短ID作为标识 ，每个ID对应着当前的channel，可以认为是唯一的，将channel和这个端ID进行绑定
            String shortedId = ctx.channel().id().asShortText();
            for(Channel channel : clients){ //响应客户端,遍历消息ACK到所有channel
                if (resMap.get(shortedId) == null || !resMap.get(shortedId).equals(channel)) {
                    channel.writeAndFlush(new TextWebSocketFrame(msgInfo)); // 响应客户端
                    resMap.put(shortedId, channel);
                }
            }
            resMap.clear();
        } else {  // 不接受文本以外的数据帧类型
            ctx.channel().writeAndFlush(WebSocketCloseStatus.INVALID_MESSAGE_TYPE).addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    @Override //客户端创建就触发当客户端连上服务端后，就可获取该channel放到channelGroup中统一管理
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //每一个和服务端建立连接的客户端，都会触发handlerAdded方法
        Channel channel = ctx.channel();
        clients.add(channel);
        String shortedId = channel.id().asShortText().trim();
        log.info("接收到了一个客户端的连接请求啦，当前的连接用户的channel的短ID是:" + shortedId);
        if (checkMap.get(shortedId) == null || !checkMap.get(shortedId).equals(channel)) {
            checkMap.put(shortedId, channel);
        }
    }
    @Override //客户端销毁时触发，当handlerRemoved被触发channelGroup会自动移除对应的channel
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        resMap.remove(channel);
        checkMap.remove(channel);
        log.info("客户端断开，当前被移除的channel的短ID是：" + channel.id().asShortText());
        clients.remove(channel);
    }
    
    @Override   // 链接断开-与客户端断开连接
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("链接断开：{}", ctx.channel().remoteAddress());
    }
    @Override   // 链接创建-与客户端建立连接
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("链接创建：{}", ctx.channel().remoteAddress());
    }




    
    
}
