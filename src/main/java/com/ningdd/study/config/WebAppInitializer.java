package com.ningdd.study.config;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.mock.web.MockFilterConfig;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

/**
 * 注解配置DispatcherServlet初始化程序，此类的作用可以代替web.xml进行初始化
 * 
 * @author ningdd
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private static final String CHARACTER_ENCODING = "UTF-8";

    /**
     * 获取spring的配置类
     * 对应ContextLoaderListener，是Spring根容器
     * 
     * 配置root上下文,如Jpa数据源等等的配置
     * 
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { PersistenceConfig.class, WebSecurityConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebMvcConfig.class };
    }

    @Override
    /**
     * 将DispatcherServlet映射到 "/" 指定开始被servlet处理的url,配置从/开始
     */
    protected String[] getServletMappings() {
        /*
         1.映射到 /   : '/' 可以访问，'/a' 可以访问，'/a/b' 可以访问，'/a/b/c' 可以访问；controller[可以访问]，静态资源uri能匹配

         2.映射到 /*  : '/' 访问不到，'/a' 访问不到，'/a/b' 访问不到，'/a/b/c' 访问不到；controller[可以访问]，静态资源uri能匹配

         3.映射到 /** : '/' 可以访问，'/a' 访问不到，'/a/b' 访问不到，'/a/b/c' 访问不到；controller[访问不到]，静态资源uri无法匹配
        
        */
        return new String[] { "/" };
    }

    /**
     * 方法getServletFilters返回一个过滤器数组，而不将它们映射到URL。 因此，它们在每次请求时都会触发。
     * 如果您不想在web.xml中编写url映射， 则可以使用HandlerInterceptor而不是Filter 。
     */
    @Override
    protected javax.servlet.Filter[] getServletFilters() {
        // 配置 Druid 监控
        final WebStatFilter webStatFilter = new WebStatFilter();
        webStatFilter.setProfileEnable(true);
        MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,*.jsp,/druid/*,/download/*");
        config.addInitParameter("sessionStatMaxCount", "2000");
        config.addInitParameter("sessionStatEnable", "true");
        // 修改为你user信息保存在session中的sessionName
        config.addInitParameter("principalSessionName", "user");

        try {
            webStatFilter.init(config);
        } catch (ServletException e) {
            e.printStackTrace();
        }

        // CharacterEncodingFilter，只能处理post请求的乱码，
        // spring只是利用request.setCharacterEncoding(this.encoding);帮助我们处理了POST方式的乱码问题，碰到GET方式的提交，还是会出现乱码。
        return new Filter[] { new CharacterEncodingFilter(CHARACTER_ENCODING, true), webStatFilter };
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        // 添加filter
//        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("CharacterEncodingFilter", 
//        CharacterEncodingFilter.class);
//        encodingFilter.setInitParameter("encoding", CHARACTER_ENCODING);
//        encodingFilter.setInitParameter("forceEncoding", "true");
//        // 开启异步支持
//        encodingFilter.setAsyncSupported(true);

//        EnumSet<DispatcherType> dispatcherTypes = EnumSet.allOf(DispatcherType.class);
//        dispatcherTypes.add(DispatcherType.REQUEST);
//        dispatcherTypes.add(DispatcherType.FORWARD);
//
//        // 添加Url模式映射
//        encodingFilter.addMappingForUrlPatterns(dispatcherTypes, true, "/*");
        
        // 设置Init-Para
        setInitPara(servletContext);

        // 添加servlet
        addServlet(servletContext, StatViewServlet.class);
        
        // 添加监听器
        servletContext.addListener(new RequestContextListener());
    }

    /**
     * 
     * 
     * @param servletContext
     */
    private void addServlet(ServletContext servletContext, Class<? extends Servlet> servletClass) {
        Dynamic statViewServlet = servletContext.addServlet("statViewServlet", servletClass);
        statViewServlet.setInitParameter("resetEnable", "true");
        statViewServlet.setInitParameter("loginUsername", "druid");
        statViewServlet.setInitParameter("loginPassword", "druid");

        statViewServlet.addMapping("/druid/*");
    }

    /**
     * 
     * @param servletContext
     */
    private void setInitPara(ServletContext servletContext) {
        servletContext.setInitParameter("spring.liveBeansView.mbeanDomain", "dev");
        servletContext.setInitParameter("spring.profiles.default", "dev");
        servletContext.setInitParameter("spring.profiles.active", "dev");
    }
}