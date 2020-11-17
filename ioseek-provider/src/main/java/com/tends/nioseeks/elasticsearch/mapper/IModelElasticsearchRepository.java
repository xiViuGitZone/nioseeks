package com.tends.nioseeks.elasticsearch.mapper;

import com.tends.nioseeks.elasticsearch.pojo.ESTestItemPojo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

////接口要位于com.tends.nioseeks.elasticsearch.mapper路径下（和EnableElasticsearchRepositories注解对应）
public interface IModelElasticsearchRepository extends ElasticsearchRepository<ESTestItemPojo, Integer> {

    //用@Query这种注解来制定查询/修改语句
    @Query("{ \"match\" : { \"content\" : \"?0\" }}")
    List<ESTestItemPojo> findByText(String text);

    //默认的注释
    @Query("{\n" +
            "     \"term\" : {\n" +
            "       \"content\": \"?0\"\n" +
            "     }\n" +
            " }")
    Page<ESTestItemPojo> findByContent(String content, Pageable pageable);

    @Query("{\n" +
            "     \"match\" : {\n" +
            "       \"firstCode\": \"?0\"\n" +
            "     }\n" +
            " }")
    Page findByFirstCode(String k, Pageable pageable);


    //List<ESTestItemPojo> findByName(String name);   //根据name查询
    //
    //List<ESTestItemPojo> findByNameAndInfo(String name,String info);  //根据name和info查询
    //
    //
    //List<ESTestItemPojo> findAll();
    //
    //List<ESTestItemPojo> findAllByTitleContains(String title);
    //
    //ESTestItemPojo findByTitleContains(String title);
    //
    //ESTestItemPojo findByTitle(String title);

}
