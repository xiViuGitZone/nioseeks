package com.tends.nioseeks.pojo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter      //提供用户核心信息
public class SysUser implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String data;
    private String email;
    private Integer status;


}
