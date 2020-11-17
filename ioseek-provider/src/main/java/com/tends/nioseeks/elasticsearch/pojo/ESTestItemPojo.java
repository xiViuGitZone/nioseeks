package com.tends.nioseeks.elasticsearch.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
//@Document:说明当前实体为文档主要使用俩个属性indexName(唯一索引名必须小写,建议取类名)、type(高版本已弃用)对应在索引库中的类型
//shards：分片数量，默认5、 replicas：副本数量，默认1
//@Field:字段注解，配置字段属性,主要属性:
// type:声明字段属性、枚举：FieldType，可以是text、long、short、date、integer、object等
// text：存储数据时候，会自动分词，并生成索引
// keyword：存储数据时候，不会分词建立索引
// Numerical：数值类型，分两类
//      基本数据类型：long、interger、short、byte、double、float、half_float
//      浮点数高精度类型：scaled_float、需指定一个精度因子,如10或100、es把真实值乘这个因子后存储,取出时再还原
// Date：日期类型。elasticsearch能对日期格式化为字符串存储，建议存储为毫秒值long，节省空间
// index：是否索引，默认是true
// store：是否存储，默认是false
// searchAnalyzer:指定搜索分词器、analyer:指定分词器
//      ik分词器参数： ik_smart最少切分、 ik_max_word最细粒度划分
 */
//@Accessors(chain = true)  //lombok注解、自动解释字段
//Accessor 的中文含义是存取器，@Accessors用于配置getter和setter方法的生成结果，下面介绍三个属性
//  fluent 的中文含义是流畅的，设置为true，则getter和setter方法的方法名都是基础属性名，且setter方法返回当前对象
//  chain 的中文含义是链式的，设置为true，则setter方法返回当前对象。
//  prefix 的中文含义是前缀，用于生成getter和setter方法的字段名会忽视指定前缀（遵守驼峰命名）
//    Long pId;  public Long getId(){略}  public void setId(Long id){略}
//除了如下方式、springBoot还提供@Setting及@Mapping注解方式,对字段设置要求较高的建议用它,能在配置文件写原生DDL语句,麻烦些但较清晰
//提供搜索用的实体应与业务实体区分开，如果有相同实体，为避免混淆应写两份。通常全局搜索接口可单独部署服务器
@Document(indexName = "mytest_esitem")
public class ESTestItemPojo {
    @Id   //标记一个字段作为id主键
    private Integer id;
    @Field(type = FieldType.Keyword)   //设置为keyword、就不能进行分词
    private String firstCode;
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Auto)   //自动检测类型
    private Object data;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFirstCode() {
        return firstCode;
    }

    public void setFirstCode(String firstCode) {
        this.firstCode = firstCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}


