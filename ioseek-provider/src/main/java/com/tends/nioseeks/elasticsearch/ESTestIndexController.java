package com.tends.nioseeks.elasticsearch;

import com.alibaba.fastjson.JSONObject;
import com.tends.nioseeks.elasticsearch.mapper.IModelElasticsearchRepository;
import com.tends.nioseeks.elasticsearch.pojo.ESTestItemEntity;
import com.tends.nioseeks.elasticsearch.pojo.ESTestItemPojo;
import com.tends.nioseeks.elasticsearch.service.MyElasticsearchSearchService;
import com.tends.nioseeks.elasticsearch.utils.MyElasticSearchHitUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

@RestController
@RequestMapping("/esarticle")
public class ESTestIndexController {
    @Autowired
    private IModelElasticsearchRepository iModelElasticsearchRepository;
    //使用ElasticsearchTemplate(过时,需用ElasticsearchRestTemplate)模式,在代码任何地方都可以@注入
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private MyElasticsearchSearchService myElasticsearchSearchService;
    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;
    //@Autowired
    //@Qualifier("highLevelClient")
    //private RestHighLevelClient highLevelClient;


    private String[] names={"诸葛亮","曹操","李白","韩信","赵云","小乔","狄仁杰","李四","诸小明","王五"};
    private String[] infos={"我来自中国的一个小乡村，地处湖南省","我来自中国的一个大城市，名叫上海，人们称作魔都"
            ,"我来自东北，家住大囤里，一口大碴子话"};


    @GetMapping("/saveUser")
    public Object saveUser(){
        Long resLong = null;
        try {
            //添加索引mapping    索引会自动创建但mapping自只用默认的这会导致分词器不生效 所以这里我们手动导入mapping
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(ESTestItemPojo.class);
            if (indexOps.exists()) {
                String redisUserKey = "es:test:userpojo:items:listkey";
                resLong = redisTemplate.opsForList().leftPushAll(redisUserKey, "[]");
                if (resLong == null) {
                    return -1;
                }
               return 0;
            }
            indexOps.putMapping(indexOps.createMapping(ESTestItemPojo.class));
            //elasticsearchRestTemplate.putMapping(ESTestItemPojo.class);
            Random random = new Random();
            List<ESTestItemEntity> users = new ArrayList<ESTestItemEntity>();
            List<IndexQuery> queryList = new ArrayList<IndexQuery>();
            for (int i=0; i<20; i++){
                ESTestItemPojo userPojo = new ESTestItemPojo();
                userPojo.setId(i);
                userPojo.setTitle("第"+i + "号用户， 用户编码： "+ random.nextInt(40)+i);
                userPojo.setFirstCode(names[random.nextInt(9)]);
                userPojo.setContent(infos[random.nextInt(2)]);
                userPojo.setData(JSONObject.toJSONString(userPojo));

                ESTestItemEntity itemEntity = new ESTestItemEntity(userPojo);
                users.add(itemEntity);

                IndexQuery indexQuery = new IndexQueryBuilder()
                        .withId(userPojo.getId().toString())
                        .withObject(userPojo)
                        .build();
                //// 存入索引，返回文档ID
                ////String documentId = elasticsearchTemplate.index(indexQuery);
                //String documentId = elasticsearchRestTemplate.index(indexQuery, IndexCoordinates.of("mytest_esitem"));
                //elasticsearchRepository.save(userPojo);   //保存到es中
                queryList.add(indexQuery);
            }
            //elasticsearchRestTemplate.indexOps(IndexCoordinates.of(new String[]{"indexName"})).exists();
            List<String> esitems = elasticsearchRestTemplate.bulkIndex(queryList, IndexCoordinates.of("mytest_esitem"));
            System.out.println("文档ID: "+ esitems);

            String redisUserKey = "es_test_userpojo_items_listkey";
            resLong = redisTemplate.opsForList().leftPushAll(redisUserKey, JSONObject.toJSONString(users));
            if (resLong == null) {
                return -1;
            }
        } catch (Exception e) {
            elasticsearchRestTemplate.indexOps(ESTestItemPojo.class).delete();
            e.printStackTrace();
        }
        return resLong;
    }

    @GetMapping("/getDataById")
    public Object getDataById(Long id){
        return myElasticsearchSearchService.findDataById(id);
    }
    @GetMapping("/getDataByName")
    public Object getDataByName(String name){
        //return myElasticsearchSearchService.findDataByNmae(name);
        return iModelElasticsearchRepository.findByContent(name, PageRequest.of(1,10));
    }
    @GetMapping("/getByfirstCode")
    public Object findByTitle(@RequestParam(name = "k") String k){
        return iModelElasticsearchRepository.findByFirstCode(k, PageRequest.of(1,10));
    }
    @GetMapping("/getAllDataByPage")
    public Object getAllDataByPage(Long id){
        //本该传入page和size，这里为了方便就直接写死了
        Pageable pageable = PageRequest.of(0,10, Sort.Direction.ASC,"id");
        //Page<ESTestItemPojo> all = elasticsearchRestTemplate.queryForObject(new GetQuery(""+id), Page.class);
        Page<ESTestItemPojo> all = iModelElasticsearchRepository.findAll(pageable);
        return all.getContent();
    }

    @GetMapping("/getHightByUser")
    public Object getHightByUser(String keyStr, int pages, int size){
        ////int pageNumber = 0;     // 页码
        ////int pageSize = 10;      // 页数
        ////Page<ESTestItemPojo> page = null;
        ////String keyword = "管理员";
        ////// 构建查询
        ////NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        ////// 多索引查询
        //////searchQuery.withIndices(EsConstant.INDEX_NAME.TRAVEL);
        ////
        ////// 组合查询，boost即为权重，数值越大，权重越大
        ////QueryBuilder queryBuilder = QueryBuilders.boolQuery()
        ////        .should(QueryBuilders.multiMatchQuery(keyword, "title").boost(3))
        ////        .should(QueryBuilders.multiMatchQuery(keyword, "passCity", "description").boost(2))
        ////        .should(QueryBuilders.multiMatchQuery(keyword, "content", "keyword").boost(1));
        ////searchQuery.withQuery(queryBuilder);
        ////
        ////// 高亮设置
        ////List<String> highlightFields = new ArrayList<String>();
        ////highlightFields.add("title");
        ////highlightFields.add("passCity");
        ////highlightFields.add("description");
        ////highlightFields.add("content");
        ////highlightFields.add("keyword");
        ////HighlightBuilder.Field[] fields = new HighlightBuilder.Field[highlightFields.size()];
        ////for (int x = 0; x < highlightFields.size(); x++) {
        ////    fields[x] = new HighlightBuilder.Field(highlightFields.get(x)).preTags("<title>")
        ////            .postTags("</title>");
        ////}
        ////searchQuery.withHighlightFields(fields);
        ////// 分页设置
        ////searchQuery.withPageable(PageRequest.of(pageNumber, pageSize));
        //
        //
        //NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //HighlightBuilder highlightBuilder = new HighlightBuilder();
        //highlightBuilder.requireFieldMatch(false);
        //////对所有找到的搜索词添加同一个标签
        ////highlightBuilder.field("title");
        ////highlightBuilder.field("content");
        ////highlightBuilder.preTags("<em>");
        ////highlightBuilder.postTags("</em>");
        //
        ////对不同的关键词添加不同的标签
        //HighlightBuilder.Field titleField = new HighlightBuilder.Field("title");  //高亮标题
        //titleField.preTags("<title>"); //标题的开始标签
        //titleField.postTags("</title>");//标题的结束标签
        //HighlightBuilder.Field contentField = new HighlightBuilder.Field("content"); //高亮内容
        //contentField.preTags("<content>"); //内容的开始标签
        //contentField.postTags("</content>"); //内容的结束标签
        //highlightBuilder.field(titleField);
        //highlightBuilder.field(contentField);
        //
        ////List<String> indexList = new ArrayList<String>(1);
        ////indexList.add("schools");
        ////builder.withIndices(indexList);  //设置搜索的索引
        //builder.withQuery(QueryBuilders.multiMatchQuery(keyStr, "title", "content"));  //设置搜索的字段为title,content
        //builder.withHighlightBuilder(highlightBuilder);  //添加高亮设置
        //NativeSearchQuery query = builder.build();


        //根据一个值查询 多个字段  并高亮显示  这里的查询是取并集，即多个字段只需要有一个字段满足即可
        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("firstCode",keyStr))
                .should(QueryBuilders.matchQuery("content",keyStr));  //需要查询的字段
        //构建高亮查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withHighlightFields(new HighlightBuilder.Field("firstCode"),
                                    new HighlightBuilder.Field("content"))
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"))
                .build();
        searchQuery.setPageable(PageRequest.of(pages, size));
        //查询
        SearchHits<ESTestItemPojo> search = elasticsearchRestTemplate.search(searchQuery, ESTestItemPojo.class);
        //得到查询返回的内容
        List<SearchHit<ESTestItemPojo>> searchHits = search.getSearchHits();
        //设置一个最后需要返回的实体类集合
        List<ESTestItemPojo> users = new ArrayList<>();
        //遍历返回的内容进行处理
        for(SearchHit<ESTestItemPojo> searchHit:searchHits){
            //高亮的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            //HighlightField hTitle = searchHit.getHighlightFields().get("title");
            //searchHit.getContent().setTitle(hTitle.getFragments()[0].toString());
            //将高亮的内容填充到content中
            searchHit.getContent().setFirstCode(highlightFields.get("firstCode")==null ?
                    searchHit.getContent().getFirstCode():highlightFields.get("firstCode").get(0));
            searchHit.getContent().setContent(highlightFields.get("content")==null ?
                    searchHit.getContent().getContent():highlightFields.get("content").get(0));
            //放到实体类中
            users.add(searchHit.getContent());
        }
        return users;
    }


    @GetMapping("/queryEsArticle")
    public Object queryEsArticle(String keyword, String esType, String dateType) {
        int pageNumber = 0;
        int pageSize = 20;
        //在此处判断用户输入的keyword中是否有敏感词
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(keyword)) {
            builder.must(QueryBuilders.multiMatchQuery(keyword, "firstCode", "content"));
        }
        //if (!StringUtils.isEmpty(esType)) {
        //    builder.must(QueryBuilders.matchQuery("esType",esType));
        //}
        //dateType  0:一周,1:一个月,2:三个月
        if (!StringUtils.isEmpty(dateType)) {
            Calendar c = Calendar.getInstance();
            if("0".equals(dateType)){
                //当前时间年月日
                c.add(Calendar.DATE, - 7);
                Date time = c.getTime();
                builder.must(rangeQuery("date").gte(time.getTime()));
            }else if("1".equals(dateType)){
                c.add(Calendar.DATE, - 30);
                Date time = c.getTime();
                builder.must(rangeQuery("date").gte(time.getTime()));
            }else if("2".equals(dateType)){
                c.add(Calendar.DATE, - 90);
                Date time = c.getTime();
                builder.must(rangeQuery("date").gte(time.getTime()));
            }
        }
        // 构造分页类
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        String preTag = "<font color='#dd4b39'>";//google的色值
        String postTag = "</font>";

        //SearchQuery searchQuery = new NativeSearchQueryBuilder().
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(builder).
                withHighlightFields(
                        new HighlightBuilder.Field("firstCode").preTags(preTag).postTags(postTag),
                        new HighlightBuilder.Field("content").preTags(preTag).postTags(postTag)
                        //,new HighlightBuilder.Field("esType").preTags(preTag).postTags(postTag)
                ).build();
        searchQuery.setPageable(pageable);

        //// 不需要高亮直接return ideas
        //AggregatedPage<ESTestItemPojo> ideas = elasticsearchRestTemplate.queryForPage(searchQuery, ESTestItemPojo.class, IndexCoordinates.of("mytest_esitem"));

        //查询
        SearchHits<ESTestItemPojo> search = elasticsearchRestTemplate.search(searchQuery, ESTestItemPojo.class);
        //得到查询返回的内容
        List<SearchHit<ESTestItemPojo>> searchHits = search.getSearchHits();
        //设置一个最后需要返回的实体类集合
        List<ESTestItemPojo> users = new ArrayList<>();
        //遍历返回的内容进行处理
        for(SearchHit<ESTestItemPojo> searchHit: searchHits){
            //高亮的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            //HighlightField hTitle = searchHit.getHighlightFields().get("title");
            //searchHit.getContent().setTitle(hTitle.getFragments()[0].toString());
            //将高亮的内容填充到content中
            searchHit.getContent().setFirstCode(highlightFields.get("firstCode")==null ?
                    searchHit.getContent().getFirstCode():highlightFields.get("firstCode").get(0));
            searchHit.getContent().setContent(highlightFields.get("content")==null ?
                    searchHit.getContent().getContent():highlightFields.get("content").get(0));
            //放到实体类中
            users.add(searchHit.getContent());
        }

        return users;
    }
    //private ESTestItemPojo hitToEntity(org.elasticsearch.search.SearchHit searchHit) {
    //    ESTestItemPojo itemPojo = new ESTestItemPojo();
    //    itemPojo.setId(searchHit.getId() == null ? 0 : Integer.valueOf(searchHit.getId()));
    //    itemPojo.setTitle(String.valueOf(searchHit.getSourceAsMap().get("title")));
    //    itemPojo.setContent(String.valueOf(searchHit.getSourceAsMap().get("content")));
    //    //itemPojo.setType(String.valueOf(searchHit.getSourceAsMap().get("type")));
    //    //itemPojo.setEsType(String.valueOf(searchHit.getSourceAsMap().get("esType")));
    //    //itemPojo.setAuthor(String.valueOf(searchHit.getSourceAsMap().get("author")));
    //    //itemPojo.setUrl(String.valueOf(searchHit.getSourceAsMap().get("url")));
    //    //itemPojo.setDate(String.valueOf(searchHit.getSourceAsMap().get("date")));
    //
    //    return itemPojo;
    //}

    @GetMapping("/queryEsArticleZWGK")    //全文搜索政务公开
    public Object queryEsArticleZWGK(String title,String content,String wjType,String wjbh,String year,String dataTime) {
        int pageNumber = 0;
        int pageSize = 20;
        try {
            //在此处判断用户输入的keyword中是否有敏感词
            BoolQueryBuilder builder = QueryBuilders.boolQuery();
            if (!StringUtils.isEmpty(title)) {
                builder.must(QueryBuilders.multiMatchQuery(title, "title"));
            }
            if (!StringUtils.isEmpty(content)) {
                builder.must(QueryBuilders.multiMatchQuery(content, "content"));
            }
            if (!StringUtils.isEmpty(wjType)) {
                builder.must(QueryBuilders.multiMatchQuery(wjType, "wjType"));
            }
            if (!StringUtils.isEmpty(wjbh)) {
                builder.must(QueryBuilders.multiMatchQuery(wjbh, "wjbh"));
            }

            //年份查询
            if (!StringUtils.isEmpty(year) && MyElasticSearchHitUtils.isNumeric(year)) {
                Long startTime = MyElasticSearchHitUtils.getYearStartTime(year);
                Long endTime = MyElasticSearchHitUtils.getYearEndTime(String.valueOf(Integer.valueOf(year)+1));
                builder.must(rangeQuery("date").gte(startTime).lte(endTime));
            }
            //年月日查询
            if (!StringUtils.isEmpty(dataTime)) {
                Long startTime = MyElasticSearchHitUtils.getTimeStartTime(dataTime);
                Long endTime = MyElasticSearchHitUtils.getTimeEndTime(dataTime);
                builder.must(rangeQuery("date").gte(startTime).lte(endTime));
            }

            // 构造分页类
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            String preTag = "<font color='#dd4b39'>";//google的色值
            String postTag = "</font>";

            //SearchQuery searchQuery = new NativeSearchQueryBuilder().
            NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().
                    withQuery(builder).
                    withHighlightFields(
                            new HighlightBuilder.Field("title").preTags(preTag).postTags(postTag),
                            new HighlightBuilder.Field("content").preTags(preTag).postTags(postTag)
                            //,new HighlightBuilder.Field("esType").preTags(preTag).postTags(postTag)
                    ).build();
            searchQuery.setPageable(pageable);

            // 不需要高亮直接return ideas
            // AggregatedPage<Idea> ideas = elasticsearchTemplate.queryForPage(searchQuery, Idea.class);

            //查询
            SearchHits<ESTestItemPojo> search = elasticsearchRestTemplate.search(searchQuery, ESTestItemPojo.class);
            //AggregatedPage<SearchHit<ESTestItemPojo>> aggregatedPage = SearchHitSupport.page(search, searchQuery.getPageable());
            //AggregatedPage aggregatedPage1 = (AggregatedPage) SearchHitSupport.unwrapSearchHits(aggregatedPage);
            //得到查询返回的内容
            List<SearchHit<ESTestItemPojo>> searchHits = search.getSearchHits();
            //设置一个最后需要返回的实体类集合
            List<ESTestItemPojo> users = new ArrayList<>();
            //遍历返回的内容进行处理
            for(SearchHit<ESTestItemPojo> searchHit: searchHits){
                //高亮的内容
                Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
                //HighlightField hTitle = searchHit.getHighlightFields().get("title");
                //searchHit.getContent().setTitle(hTitle.getFragments()[0].toString());
                //将高亮的内容填充到content中
                searchHit.getContent().setFirstCode(highlightFields.get("title")==null ?
                        searchHit.getContent().getFirstCode():highlightFields.get("title").get(0));
                searchHit.getContent().setContent(highlightFields.get("content")==null ?
                        searchHit.getContent().getContent():highlightFields.get("content").get(0));
                //放到实体类中
                users.add(searchHit.getContent());
            }

            return users;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    //@GetMapping("/findEsContentByTitle")   //查询
    //public Object findByTitle(@RequestParam(name = "page", defaultValue = "0") Integer pages,
    //                          @RequestParam(name = "limit", defaultValue = "10") Integer size,
    //                          @RequestParam(name = "k") String k, String index, String queryName,
    //                          @RequestParam(name = "isSorted") String sorted,
    //                          @RequestParam(name = "sortOrder") String ordered){
    //    if (StringUtils.isEmpty(k)) {
    //      k = "美国";
    //    }
    //    if (StringUtils.isEmpty(index)) {
    //        index = "schools";
    //    }
    //    if (StringUtils.isEmpty(queryName)) {
    //        index = "content";
    //    }
    //    if (StringUtils.hasText(sorted) && "1".equals(sorted)) { //要排序
    //        if (StringUtils.isEmpty(ordered) || ("DESC".equalsIgnoreCase(ordered) && !"ASC".equalsIgnoreCase(ordered))) {
    //            ordered = "DESC";
    //        }
    //        ordered = ordered.toUpperCase();
    //    } else {
    //        sorted = "0";
    //    }
    //
    //    Object o = MyElasticSearchHitUtils.searchTextByWords(pages,size, index,queryName, k, highLevelClient, sorted, ordered);
    //    if (o == null) {
    //       return "查询异常，没有数据！";
    //    }
    //    return o;
    //}
    //
    //@GetMapping("/createEsIndex")      //创建索引
    //public Object createEsIndex(String index){
    //    if (StringUtils.isEmpty(index)) {
    //        throw new RuntimeException("===============索引不能为空！");
    //    }
    //
    //    Object o = MyElasticSearchHitUtils.esCreateIndexs(index, highLevelClient);
    //    if (o == null) {
    //       return "创建索引异常，没有数据！";
    //    }
    //    return o;
    //}
    //
    //@GetMapping("/addEsContent")      //新增索引数据
    //public Object addEsContent(String index, String texts){
    //    if (StringUtils.isEmpty(index)) {
    //        index = "test_esquery";
    //    }
    //    if (StringUtils.isEmpty(texts)) {
    //        throw new RuntimeException("===============文本不能为空！");
    //    }
    //
    //    Object o = MyElasticSearchHitUtils.esAddDocsInfo(index, texts, highLevelClient);
    //    if (o == null) {
    //       return "新增数据异常，没有数据！";
    //    }
    //    return o;
    //}







}
