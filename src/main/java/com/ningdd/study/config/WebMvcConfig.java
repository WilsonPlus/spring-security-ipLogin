package com.ningdd.study.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.ningdd.study")
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    /*
     * 因为<url-pattern>/</url-pattern>在Tomcat服务器是默认的servlet；
     * 通过查看Tomcat web.xml可得知，
     */
    // 静态资源过滤，只有当despacherServlet配置成 ‘/’ 的时候，此配置才有点用处，不然就是多余的配置，
    // 相当于：<mvc:resources location="static/js/" mapping="/js/**" />
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/page/**").addResourceLocations("static/page/");
        registry.addResourceHandler("/js/**").addResourceLocations("static/js/");
        registry.addResourceHandler("/css/**").addResourceLocations("static/js/");
        registry.addResourceHandler("/imgs/**").addResourceLocations("static/imgs/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
        registry.jsp("/WEB-INF/templates/", ".jsp");
    }
    
    // 路径映射
    // !!!addViewController("/uri.xxx")，后缀不要使用.jsp！！！不然找不到
    // servlet容器有内置的 "*.jsp" 匹配器，
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addViewController("/").setStatusCode(HttpStatus.OK).setViewName("hello");
        registry.addViewController("/index.html").setStatusCode(HttpStatus.OK).setViewName("index");
        
        registry.addViewController("/login.html").setStatusCode(HttpStatus.OK).setViewName("login");
        registry.addViewController("/hello.html").setStatusCode(HttpStatus.OK).setViewName("hello");
        registry.addViewController("/success.html").setStatusCode(HttpStatus.OK).setViewName("success");
        registry.addViewController("/failure.html").setStatusCode(HttpStatus.OK).setViewName("failure");
        
        registry.addViewController("/ipHello.html").setStatusCode(HttpStatus.OK).setViewName("ipHello");
        registry.addViewController("/ipLogin.html").setStatusCode(HttpStatus.OK).setViewName("ipLogin");
        
        registry.addViewController("/a").setStatusCode(HttpStatus.OK).setViewName("ipHello");
        registry.addViewController("/a/b").setStatusCode(HttpStatus.OK).setViewName("ipHello");
        registry.addViewController("/a/b/c").setStatusCode(HttpStatus.OK).setViewName("ipHello");
    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        Charset charset = Charset.forName("UTF-8");
        List<MediaType> supportedMediaTypes = new ArrayList<>(16);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

//        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        StringHttpMessageConverter converter1 = new StringHttpMessageConverter();
        converter1.setDefaultCharset(charset);
        converter1.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(converter1);

        MappingJackson2HttpMessageConverter converter2 = new MappingJackson2HttpMessageConverter();
        converter2.setDefaultCharset(charset);
        converter2.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(converter2);
    }

    // 配置Jakarta Commons FileUpload multipart 解析器
    /*
    2.1 配置multipart 解析器
    DispatcherServlet并没有实现任何解析multipart请求数据的功能，
    而是将其委托给了Spring中的MultipartResolver策略接口的实例。
    从Spring3.1开始，Spring内置了两个MultipartResolver实现类供我们选择：
    
    CommonsMultipartResolver：使用Jakarta Commons FileUpload（即Apache的一个项目）解析multipart请求。
    StandardServletMultipartResolver：依赖于Servlet3.0对multipart请求的支持（始于Spring3.1）。
    
    链接：https://www.jianshu.com/p/fffc1954d104
     */
//    @Bean
//    public MultipartResolver multipartResolver() throws IOException{
//      CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//      multipartResolver.setUploadTempDir(new FileSystemResource("/tmp/uploads"));
//      multipartResolver.setMaxUploadSize(2097152);
//      multipartResolver.setMaxInMemorySize(0);
//      return multipartResolver ;
//    }    
    
    
        // 内容协商视图解析器
//    @Bean
//    public ViewResolver viewResolver() {
//        ContentNegotiatingViewResolver bean = new ContentNegotiatingViewResolver();
//        List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();
//
//        // 基于url的视图解析器
////        UrlBasedViewResolver tilesResolver = new UrlBasedViewResolver();
////        tilesResolver.setViewClass(TilesView.class);
////        tilesResolver.setOrder(0);
////        viewResolvers.add(tilesResolver);
//
//        // 内部资源视图解析器，xml配置中一般常用的就是这个
//        InternalResourceViewResolver defaultResolver = new InternalResourceViewResolver();
//        defaultResolver.setPrefix("/WEB-INF/templates/");
//        defaultResolver.setSuffix(".jsp");
//        defaultResolver.setOrder(1);
//        viewResolvers.add(defaultResolver);
//
//        bean.setViewResolvers(viewResolvers);
//        return bean;
//    }
    
    
    /*
    =================================================================================
    // Thymeleaf视图解析器
    @Bean
    public ThymeleafViewResolver viewResolver(SpringTemplateEngine springTemplateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(springTemplateEngine);
        viewResolver.setCharacterEncoding("utf-8");
        return viewResolver;
    }

    // 模版引擎
    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver iTemplateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(iTemplateResolver);
        return templateEngine;
    }

    // Thymeleaf3.0之后模版解析器
    @Bean
    public ITemplateResolver iTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("utf-8");

        templateResolver.setCacheable(false);
        return templateResolver;
    }
    

    // Thymeleaf3.0之前模版解析器
    @Bean
    public TemplateResolver templateResolver() {
        TemplateResolver resolver = new ServletContextTemplateResolver();
        resolver.setPrefix("/WEB-INF/VIEWS/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCacheable(false);
        return resolver;
    }


    // 配置静态资源的处理，自己不处理此类请求。
    // 要求DispatcherServlet将对静态资源的请求转发到Servlet容器中默认的Servlet上
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    */
}