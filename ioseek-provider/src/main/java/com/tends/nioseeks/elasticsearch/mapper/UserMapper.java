package com.tends.nioseeks.elasticsearch.mapper;
import com.tends.nioseeks.elasticsearch.pojo.SysRole;
import com.tends.nioseeks.elasticsearch.pojo.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {

    List<SysUser> findSysUserAll();

    SysUser findUserById(@Param("userId") Long userId);

    SysUser findByUsername(@Param("username") String name);

    List<SysRole> findUserRolesByUserId(@Param("uid") Long userId);
}
