package com.tends.nioseeks.commons.msgpack;
import lombok.Getter;
import lombok.Setter;
import org.msgpack.annotation.Message;

@Message
@Setter
@Getter
public class ResultVo {  //客户端接收类

    private int resultCode;
    private String resultMsg;
    private String data;


    
    public ResultVo() {
        this(1, "success");
    }

    public ResultVo(int resultCode, String resultMsg) {
        this(resultCode, resultMsg, null);
    }

    public ResultVo(int resultCode, String resultMsg, String data) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.data = data;
    }
}
