package com.ningdd.study.config;
import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.alibaba.druid.pool.DruidDataSource;
import com.ningdd.study.authentication.IpAuthenticationProvider;
import com.ningdd.study.filter.IpAuthenticationProcessingFilter;
import com.ningdd.study.service.IMemberService;
import com.ningdd.study.service.impl.MemberServiceImpl;

/**
 * Spring Security Java配置
 * 注解方式配置 Spring Security，
 * 注意需要在 Spring 配置文件中扫描此类
 *
 * @author ningdd
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String DEF_USERS_BY_USERNAME_QUERY = "SELECT username, password, status as enabled"
            + " FROM t_sec_member" 
            + " WHERE username = ?";
    private static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = "SELECT m.username, r.role_name as authority" +
            " FROM t_sec_role AS r LEFT JOIN t_sec_member AS m"
            + " ON m.username = ?"
            + " INNER JOIN `t_sec_member_role` AS mr"
            + " ON m.id = mr.member_id"
            + " AND mr.role_id = r.id";
    
    @Resource
    private DruidDataSource dataSource;
//    @Resource
//    private IMemberService memberService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 用于影响全局安全性(配置资源，设置调试模式，通过实现自定义防火墙定义拒绝请求)的配置设置。
        web
                .ignoring()
                .antMatchers(
                        "/index.jsp",
                        "/index.html",
                        "/css/**",
                        "/js/**",
                        "/imgs/**",
                        "/help/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * authorizeRequests()  配置路径拦截，表明路径访问所对应的权限，角色，认证信息。
         * formLogin()          对应表单认证相关的配置
         * logout()             对应了注销相关的配置
         * httpBasic()          可以配置basic登录
         * sessionManagement()  session管理
         */
        http
                .authorizeRequests()
                    .antMatchers("/", "/home").permitAll()
                    .antMatchers("/index.html","index.jsp").permitAll()
                    .antMatchers("/ipLogin.html").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
                    /*
                    anyRequest：任何的请求
                    authenticated：认证
                    */
                    .anyRequest().authenticated()
                    .and()
                // 表单登录
                .formLogin()
                    .usernameParameter("username")
                    .passwordParameter("password")
                    // 登录成功重定向页面
                    .defaultSuccessUrl("/success.html")
                    // 登录失败转发页面
                    .failureUrl("/failure.html")
                    // 登录成功转发页面
//                    .successForwardUrl("/WEB-INF/templates/success.jsp")
                    // 登录失败重定向页面
//                    .failureForwardUrl("/WEB-INF/templates/failure.jsp")
                    // 登录页面
                    .loginPage("/login.html")
                    // 处理登录的请求url
                    .loginProcessingUrl("/login")
                    /*
                    // 定义登录认证失败后执行的操作
                    .failureHandler(this.authenticationFailureHandler())
                    // 定义登录认证曾工后执行的操作
                    .successHandler(this.authenticationSuccessHandler());
                    */
                    .permitAll()
                    .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/index.html")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                    .and()
                .httpBasic()
                    .disable()
                // 禁用 CSRF
                .csrf()
                    .disable()
                    
                // 登录发生异常时的处理
                .exceptionHandling()
                    .accessDeniedPage("/ipLogin.html")
                    .authenticationEntryPoint(loginUrlAuthenticationEntryPoint())
                    .and()
                
                // 请求的渠道，对指定的请求进行https的访问方式
                /*
                .requiresChannel()
                    .antMatchers("/login")
                    .requiresSecure()
                    .and()
                */
                // session管理
                .sessionManagement()
                    .sessionFixation().none().maximumSessions(1);

        // 注册Ip认证访问的过滤器， 注意放置的顺序 这很关键
        http.addFilterBefore(ipAuthenticationProcessingFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 登录认证配置
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 添加使用自定义的ip认证提供者
        auth.authenticationProvider(ipAuthenticationProvider());

//        auth.userDetailsService(memberService).passwordEncoder(this.bCryptPasswordEncoder());

        // 其默认使用的AuthenticationProvider就是DaoAuthenticationProvider
        auth.userDetailsService(this.myUserDetailsService()).passwordEncoder(this.bCryptPasswordEncoder());
    }

    @Bean(name = "userDetailsService")
    public JdbcDaoImpl getUserDetailsService() {
        JdbcDaoImpl userDetailsService = new JdbcDaoImpl();
        
        userDetailsService.setDataSource(dataSource);
        
        userDetailsService.setAuthoritiesByUsernameQuery(DEF_AUTHORITIES_BY_USERNAME_QUERY);
        
        userDetailsService.setUsersByUsernameQuery(DEF_USERS_BY_USERNAME_QUERY);
        
        return userDetailsService;
    }

    @Bean(name = "myUserDetailsService")
    public IMemberService myUserDetailsService() {
        return new MemberServiceImpl();
    }

    @Bean(name = "bCryptPasswordEncoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置封装ipAuthenticationToken的过滤器
     */
    public IpAuthenticationProcessingFilter ipAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
        IpAuthenticationProcessingFilter ipAuthenticationProcessingFilter = 
                // 使用 /ipVerify 该端点进行ip认证
                new IpAuthenticationProcessingFilter(new AntPathRequestMatcher("/ipVerify"));
        
        // 为过滤器添加认证器管理
        ipAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        
        // 重写认证失败时的跳转页面
        ipAuthenticationProcessingFilter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/html/failure.html"));
        
        return ipAuthenticationProcessingFilter;
    }
    
    /**
     * 配置登录端点
     */
    @Bean
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
        LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint =
                new LoginUrlAuthenticationEntryPoint("/ipLogin.html");
        return loginUrlAuthenticationEntryPoint;
    }

    /**
     * ip认证者配置
     */
    @Bean
    public IpAuthenticationProvider ipAuthenticationProvider() {
        return new IpAuthenticationProvider();
    }
}