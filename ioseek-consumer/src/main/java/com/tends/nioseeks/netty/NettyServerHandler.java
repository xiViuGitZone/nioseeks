package com.tends.nioseeks.netty;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.InetSocketAddress;
import java.util.Map;

@Component
@Slf4j       //服务端处理器
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {


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








}
