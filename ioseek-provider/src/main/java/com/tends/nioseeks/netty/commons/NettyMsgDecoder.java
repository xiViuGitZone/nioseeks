//package com.tends.nioseeks.commons;
//import com.tends.nioseeks.commons.msgpack.ServerMessagePojo;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.ByteToMessageDecoder;
//import org.msgpack.MessagePack;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import java.util.List;
//
////自定义解码器、类似mina中CumulativeProtocolDecoder类，ByteToMessageDecoder也可将未处理的ByteBuf保存起来下次一起处理
//public class NettyMsgDecoder extends ByteToMessageDecoder {
//    private static final Logger log = LoggerFactory.getLogger(NettyMsgDecoder.class);
//
//
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        log.info("thread name: " + Thread.currentThread().getName());
//        long start = System.currentTimeMillis();
//        if (in.readableBytes() < 4) {
//            return;
//        }
//
//        in.markReaderIndex();
//        int length = in.readInt();
//        if (length <= 0) {
//            log.info("length: " + length);
//            ctx.close();
//            return;
//        }
//
//        if (in.readableBytes() < length) {
//            log.info("return");
//            in.resetReaderIndex();
//            return;
//        }
//
//        try {
//            byte[] b = new byte[length];
//            in.readBytes(b);
//            ServerMessagePojo message = new ServerMessagePojo();
//            MessagePack msgpack = new MessagePack();
//
//            message = msgpack.read(b, ServerMessagePojo.class);
//            out.add(message);
//            log.info(" ====== decode succeed: " + message.toString());
//        } catch (Exception e) {
//            log.error("MessagePack read error!!!!!!!");
//            ctx.close();
//        }
//        long time = System.currentTimeMillis() - start;
//        log.info("decode time: " + time + " ms");
//    }
//
//
//
//}
