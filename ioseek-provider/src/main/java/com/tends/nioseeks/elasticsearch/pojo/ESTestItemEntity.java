package com.tends.nioseeks.elasticsearch.pojo;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class ESTestItemEntity implements Serializable {
    private Integer id;
    private String firstCode;
    private String title;
    private String content;
    private Object data;


    public ESTestItemEntity(ESTestItemPojo itemPojo) {
        this.id = itemPojo.getId();
        this.firstCode = itemPojo.getFirstCode();
        this.title = itemPojo.getTitle();
        this.content = itemPojo.getContent();
        this.data = itemPojo.getData();
    }



}
