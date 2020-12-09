package com.tends.nioseeks.mapper;

import com.tends.nioseeks.pojo.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface SqlserverTestMapper {

    @Select("select top 10 * from sys_user where username=#{fileName}")
    public ArrayList<SysUser> findFileDataByName(@Param("fileName") String fileName);

    @Select("select top 20 * from sys_user ")
    public ArrayList<SysUser> findAll();
}
