package com.tends.nioseeks.configs;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

//@WebServlet(urlPatterns = "/druid/*",
//        initParams = {
//                @WebInitParam(name = "allow", value = "127.0.0.1"),// IP白名单 (没有配置或者为空，则允许所有访问)
//                //@WebInitParam(name = "deny", value = "192.168.16.111"),// IP黑名单 (存在共同时，deny优先于allow)
//                @WebInitParam(name = "loginUsername", value = "admin"),// 用户名
//                @WebInitParam(name = "loginPassword", value = "admin"),// 密码
//                @WebInitParam(name = "resetEnable", value = "false")// 禁用HTML页面上的“Reset All”功能
//        })      继承 StatViewServlet
//@WebFilter(filterName = "DruidFilter", urlPatterns = "/*", initParams = {
//        @WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
//})      继承 WebStatFilter
//对于基于注解的Filter和Servlet需要在SpringBoot的启动类上加上自动扫描注解@ServletComponentScan
@Component
@ConfigurationProperties(prefix = "dbmonitor.druid")
@Getter
@Setter
public class DruidMonitorManage {

    private String allowd;
    //private String denyd;        // IP黑名单(存在共同时，deny优先于allow)
    private String loginUser;
    private String loginPwd;
    private String resetEnable="false";   // 禁用HTML页面上的“Reset All”功能


    @Bean
    public ServletRegistrationBean druidStatViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        servletRegistrationBean.addInitParameter("allow", this.allowd);  //IP白名单 (没有配置或者为空，则允许所有访问)
//        servletRegistrationBean.addInitParameter("deny", this.deny);         // IP黑名单(存在共同时，deny优先于allow)
        servletRegistrationBean.addInitParameter("loginUsername", this.loginUser);
        servletRegistrationBean.addInitParameter("loginPassword", this.loginPwd);
        servletRegistrationBean.addInitParameter("resetEnable", this.resetEnable); //禁用HTML页面上的“Reset All”功能
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        // 忽略资源
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
