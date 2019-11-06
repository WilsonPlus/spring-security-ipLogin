package com.ningdd.study.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * '@Configuration' 表明该类是Spring的一个配置类，该类中会包含应用上下文创建bean的具体细节<br/>
 * '@Bean' 告诉Spring该方法会返回一个要注册成为应用上下文中的bean的对象
 *
 * MapperScannerConfigurer 和 PropertyPlaceholderConfigurer之类的Bean必须要标记为static方法，
 * 以示优先加载。否则会给出警告。
 * 
 * @author ningdd
 */
@Configuration
@MapperScan("com.ningdd.study.mapper")
@PropertySource("classpath:db.properties")
public class PersistenceConfig {
    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Value("${jdbc.databaseUrl}")
    private String databaseUrl;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.publicKey}")
    private String publicKey;

    @Value("${jdbc.filters}")
    private String filters;

    @Value("${jdbc.connectionProperties}")
    private String connectionProperties;
    
    /*
    MapperScannerConfigurer 是 BeanFactoryPostProcessor 的一个实现，
 * 如果配置类中出现 BeanFactoryPostProcessor ，会破坏默认的 post-processing 。
 * 最简单的解决方式是使用 @MapperScan 注解代替 MapperScannerConfigurer 的 bean 配置。
    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.ningdd.study.mapper");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }
    */
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        try {
            dataSource.setFilters("stat,config");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.setConnectionProperties("config.decrypt=true;config.decrypt.key=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIQoQw5B7VUpmFCd21EhvnDj0Lt21BQiwUXavFmdgR0QD65hjGIp+oZrkjbMwbkyAGEcS3i58TtdCPMBlcBuw10CAwEAAQ==");
        dataSource.setDriverClassName(driverClassName);
        dataSource.setMaxActive(10);
        dataSource.setMinIdle(5);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        transactionManager.setRollbackOnCommitFailure(true);
        return transactionManager;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(this.dataSource());

        // 这里可以通过mybatis-config.xml 来设置 typeAliasPackage和mapper。
        // factoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        //<mappers>
        // 这个和@MapperScan冲突吗？这个设置有问题。
        // Resource[] mapperLocations = new Resource[] { new ClassPathResource("com.expert.dao") };
        // factoryBean.setMapperLocations(mapperLocations);
        factoryBean.setMapperLocations(resolver.getResources("classpath*:mappers/*Mapper.xml"));

        // factoryBean.setTypeAliasesPackage("com.ningdd.study.PO");
        // sqlSessionFactoryBean.setCache(cache);
        SqlSessionFactory sqlSessionFactory = factoryBean.getObject();

        // 可以直接指定配置实例，而无需MyBatis XML配置文件。
        // 开启驼峰映射
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
        sqlSessionFactory.getConfiguration().setCacheEnabled(true);
        sqlSessionFactory.getConfiguration().setLazyLoadingEnabled(true);
        sqlSessionFactory.getConfiguration().setAggressiveLazyLoading(false);
        // Class<Object> logImpl = sqlSessionFactory.getConfiguration()
        // .getTypeAliasRegistry().resolveAlias("SLF4J");
        
        // logImpl
        sqlSessionFactory.getConfiguration().setLogImpl(Slf4jImpl.class);
        sqlSessionFactory.getConfiguration().setLogPrefix("###SPRING_MVC###MYBATIS###");
        sqlSessionFactory.getConfiguration().setDefaultExecutorType(ExecutorType.REUSE);
        sqlSessionFactory.getConfiguration().setUseGeneratedKeys(true);
        return sqlSessionFactory;
    }
    
//  @Bean(name = "sqlSessionFactory")
//  public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
//      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//      
//      SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
//      factoryBean.setDataSource(getDataSource());
//      factoryBean.setMapperLocations(resolver.getResources("classpath*:mappers/*Mapper.xml"));
//      return factoryBean.getObject();
//  }
    

    @Bean
//    @Scope("prototype")
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(this.sqlSessionFactory());
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

//    @Bean
//    public Jaxb2Marshaller jaxb2Marshaller() {
//        Jaxb2Marshaller bean = new Jaxb2Marshaller();
//        bean.setPackagesToScan(new String("myproject.model"));
//        return bean;
//    }
}