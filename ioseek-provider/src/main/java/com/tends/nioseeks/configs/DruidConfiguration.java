package com.tends.nioseeks.configs;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

//ConfigurationProperties(prefix = "spring.datasource")使用规则，注入的字段如果为private，则必须具有setter方法
@Component
@ConfigurationProperties(prefix = "spring.datasource.druid")
@Getter
@Setter
public class DruidConfiguration {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.datasource.url}")
    private String url;             //数据库地址
    @Value("${spring.datasource.username}")
    private String username;        //用户名
    @Value("${spring.datasource.password}")
    private String password;        //密码
    //private String type;            //避免使用Springboot默认的连接池Hikari
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName; //DB驱动器
    private int minIdle;            //最小闲置连接
    private int initialSize;        //初始化连接数量
    private int maxActive;          //最大存活连接
    private long maxWait;           //配置获取连接等待超时的时间

    //打开PSCache，并且指定每个连接PSCache的大小
    private Boolean poolPreparedStatements;
    //每个连接最多缓存多少个SQL  默认 20
    private int maxPoolPrepareStatementPerConnectionSize;
    //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    private long timeBetweenEvictionRunsMillis;
    // 配置一个连接在池中最小生存的时间，单位是毫秒
    private long minEvictableIdleTimeMillis;
    // 配置一个连接在池中最大生存的时间，单位是毫秒
    //private long maxEvictableIdleTimeMillis;
    // 检查池中的连接是否仍可用的 SQL语句,druid会连接到数据库执行该SQL
    private String validationQuery;

    // 当程序请求连接，池在分配连接时，是否先检查该连接是否有效
    private Boolean testWhileIdle;
    // 程序 申请 连接时,进行连接有效性检查
    private Boolean testOnBorrow;
    // 程序 返还 连接时,进行连接有效性检查
    private Boolean testOnReturn;
    // 配置监控统计拦截的filters，去掉后监控界面sql将无法统计
    private String filters;
    // 通过这个属性来打开mergeSql功能；慢SQL记录
    //private String connectProperties;
    // 要求程序从池中get到连接后,N秒后必须close,否则druid会强制回收不管该连接中是活动还是空闲 默认为false
    // 防止进程不会进行close而霸占连接 false,当发现程序有未正常close连接时设置为true 打开removeAbandoned功能
    //private Boolean removeAbandoned;
    // 配置removeAbandoned对性能有一些影响,建议怀疑存在泄漏之后再打开,如连接超过30分钟未关闭就会被强行回收,
    // 且日志记录连接申请时的调用堆栈,可在内置监控界面datasource.html查看ActiveConnection StackTrace属性看未关闭连接具体堆栈
    //private long removeAbandonedTimeout=1800L;  //1800秒
    // 当druid强制回收连接后，是否将stack trace 记录到日志中 默认为true 关闭abanded连接时输出错误日志
    //private Boolean logAbandoned;
    // 程序没有close连接且空闲时长超过minEvictableIdleTimeMillis,则会执行validationQuery指定的SQL,
    // 以保证该程序连接不会池kill掉,其范围不超过minIdle指定的连接个数。 默认为true
    //private Boolean keepAlive;

    //asyncInit是1.1.4中新增加的配置，如果有initialSize数量较多时，打开会加快应用启动时间
    private Boolean asyncInit;
    //要启用PSCache，必须配置大于0，当大于0时,poolPreparedStatements自动触发修改为true
    //与maxPoolPreparedStatementPerConnectionSize相同
    private int maxOpenPreparedStatements;



    @Bean
    //@Primary
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        //druidDataSource.setDbType(type);
        druidDataSource.setDriverClassName(driverClassName);

        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setMinIdle(minIdle);

        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPrepareStatementPerConnectionSize);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        //druidDataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        druidDataSource.setValidationQuery(validationQuery);

        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setAsyncInit(asyncInit);
        druidDataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);

        try {
            druidDataSource.setFilters(this.filters);
        } catch (SQLException e) {
            logger.error("!!!!!! ioseek-provider: druid configuration init fail!!!");
        }
        return druidDataSource;
    }











}
