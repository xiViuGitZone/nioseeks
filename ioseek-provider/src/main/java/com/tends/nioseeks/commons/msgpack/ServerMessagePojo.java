package com.tends.nioseeks.commons.msgpack;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.msgpack.annotation.Message;

/**
 * MessagePack编解码: MessagePack是一个高效的二进制序列化框架。
 * 特点： 编解码高效，性能高、 序列化之后的码流小
 * 支持多种语言，使用很简单，java为例只需在自定义的类加上@Messgae注解
 * 序列化：
 * MessagePack messagePack = new MessagePack();
 * byte[] write = messagePack.write(msg);
 * 反序列化：
 * MessagePack msgpack = new MessagePack();
 * MessagePojo message = msgpack.read(b, MessagePojo.class);
 */
@Message
@Getter
@Setter
@ToString
public class ServerMessagePojo {  //服务端接收类

    // 用户id
    private int uid;

    // 模块id: 0-心跳包
    private int module;

    // json格式数据
    private String data;



    public ServerMessagePojo() {
        super();
    }

    public ServerMessagePojo(int uid, int module, String data) {
        this.uid = uid;
        this.module = module;
        this.data = data;
    }

    
}
