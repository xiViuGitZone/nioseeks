package com.tends.nioseeks.commons;
import com.tends.nioseeks.commons.msgpack.ResultVo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

//自定义编码器
public class NettyMsgEncoder extends MessageToByteEncoder<ResultVo> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ResultVo msg, ByteBuf out) throws Exception {

        MessagePack messagePack = new MessagePack();
        byte[] write = messagePack.write(msg);
        out.writeInt(write.length);
        out.writeBytes(write);
    }

}
