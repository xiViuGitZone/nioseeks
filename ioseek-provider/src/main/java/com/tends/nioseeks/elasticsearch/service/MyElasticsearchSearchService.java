package com.tends.nioseeks.elasticsearch.service;
import com.alibaba.fastjson.JSONObject;
import com.tends.nioseeks.elasticsearch.mapper.UserMapper;
import com.tends.nioseeks.elasticsearch.pojo.SysRole;
import com.tends.nioseeks.elasticsearch.pojo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class MyElasticsearchSearchService {
    @Autowired
    private UserMapper userMapper;


    public List<SysUser> findSysUserAll() {
        List<SysUser> userList = userMapper.findSysUserAll();
        if (userList == null || userList.isEmpty()) {
            return userList;
        }

        for (SysUser user : userList) {
            Long uid = user.getId();
            user.setRoles(Collections.EMPTY_LIST);
            List<SysRole> roleList = userMapper.findUserRolesByUserId(uid);
            if (roleList != null && !roleList.isEmpty()) {
                user.setRoles(roleList);
            }

            user.setData(JSONObject.toJSONString(user));
        }
        return userList;
    }

    public Object findDataById(Long id) {
        return userMapper.findUserById(id);
    }

    public Object findDataByNmae(String name) {
        return userMapper.findByUsername(name);
    }

}
