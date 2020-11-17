package com.tends.nioseeks.elasticsearch.utils;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyElasticSearchHitUtils {

    //利用正则表达式判断字符串是否是数字
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    //获取年的开始时间戳
    public static Long getYearStartTime(String year) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy");
        Date date = df.parse(year);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        long tim = cal .getTimeInMillis();
        return tim;
    }
    //获取当年的最后时间戳
    public static Long getYearEndTime(String year) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy");
        Date date = df.parse(year);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        long tim = cal .getTimeInMillis();
        return tim;
    }
    //获取日期的开始时间戳
    public static Long getTimeStartTime(String dateTime) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(dateTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        long tim = cal .getTimeInMillis();
        return tim;
    }
    //获取日期的结束时间戳
    public static Long getTimeEndTime(String dateTime) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(dateTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        long tim = cal .getTimeInMillis();
        return tim;
    }



    //创建索引
    public static Object esCreateIndexs(String esIndex, RestHighLevelClient highLevelClient) {
        try {
            Map<String, Object> properties = new HashMap<String, Object>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", "keyword"); // 类型
            properties.put("title", map);
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("type", "text"); // 类型
            //map2.put("index",true);
            map2.put("analyzer", "ik_max_word"); // 指定分词器
            map2.put("search_analyzer", "ik_smart"); // 指定搜索分词器
            properties.put("content", map2);

            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject()
                    .startObject("mappings")
                    //.startObject("_doc")
                    .field("properties", properties)
                    //.endObject()
                    .endObject()
                    .startObject("settings")
                    .field("number_of_shards", 3)   //分片数量，默认5
                    .field("number_of_replicas", 1) //副本数量，默认1
                    .endObject()
                    .endObject();

            CreateIndexRequest request = new CreateIndexRequest(esIndex).source(builder);
            CreateIndexResponse indexResponse = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
            System.out.println(indexResponse.index());
            return indexResponse.index();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "FAIL";
    }

    //插入文档
    public static int esAddDocsInfo(String esIndex, String esText, RestHighLevelClient highLevelClient) {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            //builder.field("title", "我是一条测试文档数据");
            builder.field("title", esText.trim().substring(0,8));
            builder.field("content", esText);
            builder.endObject();

            IndexRequest request = new IndexRequest(esIndex).source(builder);
            IndexResponse index = highLevelClient.index(request, RequestOptions.DEFAULT);
            return index.status().getStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    //查询文档
    public static Object searchTextByWords(Integer pageIndex, Integer pageSize,
                                           String esIndex, String queryName, String k,
                                           RestHighLevelClient highLevelClient,String sorted,String ordered) {
        //Integer pageIndex = 1;
        //Integer pageSize = 5;
        String indexName = esIndex;
        Map<String, Object> data = new HashMap<>();
        data.put(queryName, k);

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        SearchRequest searchRequest = new SearchRequest(indexName);
        //searchRequest.types(indexName);
        queryBuilder(pageIndex, pageSize, queryName, data, searchRequest, sorted, ordered);
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> map = hit.getSourceAsMap();
                map.put("id", hit.getId());
                result.add(map);

                // 取高亮结果
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                //HighlightField highlight = highlightFields.get("title");
                HighlightField highlight = highlightFields.get(queryName);
                Text[] fragments = highlight.fragments(); // 多值的字段会有多个值
                String fragmentString = fragments[0].string();
                System.out.println("高亮：" + fragmentString);
            }
            System.out.println("pageIndex:" + pageIndex);
            System.out.println("pageSize:" + pageSize);
            System.out.println(response.getHits().getTotalHits());
            System.out.println(result.size());
            for (Map<String, Object> map : result) {
                System.out.println(map.get(queryName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void queryBuilder(Integer pageIndex, Integer pageSize, String queryName,
                              Map<String, Object> query, SearchRequest searchRequest, String sorted,String ordered) {
        if (query != null && !query.keySet().isEmpty()) {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            if (StringUtils.equals("1", sorted)) {  //要排序
                searchSourceBuilder.sort("_id",
                        StringUtils.equals("DESC",ordered)? SortOrder.DESC: SortOrder.ASC);//hit.getId()
                searchSourceBuilder.sort("title",
                        StringUtils.equals("DESC",ordered)? SortOrder.DESC: SortOrder.ASC);//"title": {"type": "keyword"}
            }
            if (pageIndex != null && pageSize != null) {
                searchSourceBuilder.size(pageSize);
                if (pageIndex <= 0) {
                    pageIndex = 0;
                }
                searchSourceBuilder.from((pageIndex - 1) * pageSize);
            }
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
            query.keySet().forEach(key -> {
                boolBuilder.must(QueryBuilders.matchQuery(key, query.get(key)));

            });
            searchSourceBuilder.query(boolBuilder);

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            HighlightBuilder.Field highlightTitle =
                    new HighlightBuilder.Field(queryName).preTags("<strong>").postTags("</strong>");
            highlightTitle.highlighterType("unified");
            highlightBuilder.field(highlightTitle);
            searchSourceBuilder.highlighter(highlightBuilder);

            searchRequest.source(searchSourceBuilder);
        }
    }


    ////使用scroll深分页、  可以把 scroll 分为初始化和遍历两步：
    ////  1、初始化时将所有符合搜索条件的搜索结果缓存起来，可理解成快照
    ////  2、遍历时从快照里取数据，就是说，在初始化后对索引插入、删除、更新数据都不会影响遍历结果
    //public static void scrollPages(RestHighLevelClient highLevelClient){
    //    //获取Client对象,设置索引名称,搜索类型(SearchType.SCAN)[5.4移除，对于java代码，直接返回index顺序，不对结果排序],搜索数量,发送请求
    //    SearchResponse searchResponse = highLevelClient
    //            .prepareSearch("blog2")
    //            .setSearchType(SearchType.DEFAULT)//执行检索的类别
    //            .setSize(10).setScroll(new TimeValue(1000)).execute()
    //            .actionGet();//注意:首次搜索并不包含数据
    //    //获取总数量
    //    long totalCount=searchResponse.getHits().getTotalHits();
    //    int page=(int)totalCount/(10);//计算总页数
    //    System.out.println("总页数： ================="+page+"=============");
    //    for (int i = 1; i <= page; i++) {
    //        System.out.println("=========================页数："+i+"==================");
    //        searchResponse = highLevelClient
    //                .prepareSearchScroll(searchResponse.getScrollId())//再次发送请求,并使用上次搜索结果的ScrollId
    //                .setScroll(new TimeValue(1000)).execute()
    //                .actionGet();
    //        SearchHits hits = searchResponse.getHits();
    //        for(SearchHit searchHit : hits){
    //            System.out.println(searchHit.getSourceAsString());// 获取字符串格式打印
    //        }
    //    }
    //}







}
