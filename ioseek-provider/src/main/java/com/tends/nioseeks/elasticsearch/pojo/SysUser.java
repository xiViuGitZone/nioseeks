package com.tends.nioseeks.elasticsearch.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter      //提供用户核心信息
public class SysUser implements Serializable {
    //@TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String username;
    @JSONField(serialize = false)
    private String password;
    private String data;
    private String email;
    private Integer status;
    //@Transient
    //@TableField(exist = false)
    private List<SysRole> roles = new ArrayList<SysRole>();


}
