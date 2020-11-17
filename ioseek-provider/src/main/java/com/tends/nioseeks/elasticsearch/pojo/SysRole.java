package com.tends.nioseeks.elasticsearch.pojo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter     //授予身份验证对象以权限
public class SysRole implements Serializable {
    private static final long serialVersionUID = 6263052755291926771L;

    private Long id;
    private String roleDesc;
    private String roleName;




}
