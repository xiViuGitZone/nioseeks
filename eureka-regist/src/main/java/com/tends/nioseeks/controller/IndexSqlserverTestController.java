package com.tends.nioseeks.controller;

import com.tends.nioseeks.mapper.SqlserverTestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class IndexSqlserverTestController {
    @Autowired
    private SqlserverTestMapper sqlserverTestMapper;


    @RequestMapping("/index/name")
    public Object getInfo(String name) {
        return sqlserverTestMapper.findFileDataByName(name);
    }


    @RequestMapping("/index/all")
    public Object getAllInfo(String name) {
        return sqlserverTestMapper.findAll();
    }






}
