package com.tends.nioseeks.netty;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

@Component   //服务端初始化
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final int MAX_FRAMEL_LEN = 8192;


    @Override      //初始化channel
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new DelimiterBasedFrameDecoder(MAX_FRAMEL_LEN, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new NettyServerHandler());

        //// 定义分隔符为$$（字符串末尾分割）
        //ByteBuf delimiter = Unpooled.copiedBuffer("$$".getBytes());
        //// 添加分隔符解码器，通过分隔符来解决拆包粘包的问题
        //pipeline.addLast(new DelimiterBasedFrameDecoder(2048, delimiter));
        //// 自定义解码器，用来获取数据并做持久化处理
        //pipeline.addLast(demoderServerHandler);
    }

}
