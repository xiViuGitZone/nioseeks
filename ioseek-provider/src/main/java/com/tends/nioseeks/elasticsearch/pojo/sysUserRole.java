package com.tends.nioseeks.elasticsearch.pojo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter     //授予身份验证对象以权限
public class sysUserRole implements Serializable {

    private Long pk;
    private Long uid;       //用户id
    private Long rid;       //角色id
    private Date created;
    private Date updated;


}
