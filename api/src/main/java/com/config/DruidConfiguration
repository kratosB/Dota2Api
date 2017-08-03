//package com.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.alibaba.druid.support.http.WebStatFilter;
//import java.sql.SQLException;
//import javax.sql.DataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Conditional;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//@Configuration
//@ConditionalOnClass({
//        JpaRepository.class
//})
//@Conditional({
//        MysqlCondition.class
//})
//public class DruidConfiguration {
//
//    private static final Logger log = LoggerFactory.getLogger(DruidConfiguration.class);
//
//    public DruidConfiguration() {
//    }
//
//    @Bean
//    public ServletRegistrationBean druidServlet() {
//        return new ServletRegistrationBean(new StatViewServlet(), new String[] {
//                "/druid/*"
//        });
//    }
//
//    @Bean
//    public DataSource druidDataSource(@Value("${spring.datasource.driver-class-name}") String driver, @Value("${spring.datasource.url}") String url,
//            @Value("${spring.datasource.username}") String username, @Value("${spring.datasource.password}") String password) {
//        DruidDataSource druidDataSource = new DruidDataSource();
//        if (url.contains("mysql")) {
//            druidDataSource.setDbType("mysql");
//        }
//
//        druidDataSource.setDriverClassName(driver);
//        druidDataSource.setUrl(url);
//        druidDataSource.setUsername(username);
//        druidDataSource.setPassword(password);
//        druidDataSource.setMaxWait(60000L);
//        druidDataSource.setTimeBetweenEvictionRunsMillis(60000L);
//        druidDataSource.setMinEvictableIdleTimeMillis(300000L);
//        druidDataSource.setValidationQuery("select 1");
//        druidDataSource.setTestOnBorrow(true);
//        druidDataSource.setTestOnReturn(true);
//        druidDataSource.setTestWhileIdle(true);
//
//        try {
//            druidDataSource.setFilters("stat, wall");
//        } catch (SQLException var7) {
//            var7.printStackTrace();
//        }
//
//        return druidDataSource;
//    }
//
//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter(new WebStatFilter());
//        filterRegistrationBean.addUrlPatterns(new String[] {
//                "/*"
//        });
//        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//        return filterRegistrationBean;
//    }
//}
