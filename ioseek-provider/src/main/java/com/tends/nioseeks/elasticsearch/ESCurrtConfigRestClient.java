package com.tends.nioseeks.elasticsearch;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
//@PropertySource(value = "classpath:elasticsearch.properties")
//@EnableElasticsearchRepositories(basePackages = "com.tends.nioseeks.elasticsearch.mapper")
public class ESCurrtConfigRestClient {
    //@Resource
    //private Environment env;
    @Value("${spring.es.nodes.connected}")
    private String[] esipAddr;
    private static final int ADDRESS_LENGTH = 2;
    private static final String HTTP_SCHEME = "http";



    //@Beanpublic
    //ElasticsearchOperations elasticsearchTemplate() {
    //    return new ElasticsearchTemplate(client());
    //}

    //spring-data-elasticsearch cluster-nodes和cluster-name配置过时
    //在高版本中已经废弃，官方建议我们使用：High Level REST Client
    @Bean
    RestHighLevelClient elasticsearchClient() {
        ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo(esipAddr)  //使用构建器提供集群地址
                //.connectedTo(env.getProperty("elasticsearch.host"))
                //.withConnectTimeout(Duration.ofSeconds(5))   //连接超时时间、默认10s
                //.withSocketTimeout(Duration.ofSeconds(3))    //套接字超时、默认5s
                //.useSsl()                                    // 启用SSL、看需求
                //.withDefaultHeaders(defHeader)               // 设置标头、看需求
                //.withBasicAuth(username, password)           //检验身份
                // ... other options
                .build();

        //HttpHeaders defHeader = new HttpHeaders();  //可以定义默认的标题、看需求
        //defHeader.setBasicAuth("uname", "upwd");
        RestHighLevelClient client = RestClients.create(configuration).rest();
        return client;
    }


    //@Bean(name = "highLevelClient")
    //public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
    //    //TODO 此处可以进行其它操作
    //    return new RestHighLevelClient(restClientBuilder);
    //}
    //
    //@Bean
    //public RestClientBuilder restClientBuilder() {
    //    HttpHost[] hosts = Arrays.stream(esipAddr)
    //            .map(this::makeHttpHost)
    //            .filter(Objects::nonNull)
    //            .toArray(HttpHost[]::new);
    //    return RestClient.builder(hosts);
    //}
    //
    //private HttpHost makeHttpHost(String s) {
    //    assert StringUtils.isNotEmpty(s);
    //    String[] address = s.split(":");
    //    if (address.length == ADDRESS_LENGTH) {
    //        String ip = address[0];
    //        int port = Integer.parseInt(address[1]);
    //        System.err.println(ip+"+"+port);
    //        return new HttpHost(ip, port, HTTP_SCHEME);
    //    } else {
    //        return null;
    //    }
    //}



}
