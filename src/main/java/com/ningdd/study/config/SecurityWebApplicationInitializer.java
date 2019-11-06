package com.ningdd.study.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    /*
     * spring或者springmvc中会自动将此类注入到父类中
     * 
     * public SecurityWebApplicationInitializer() {
     *     super(WebSecurityConfig.class);
     * }
     */
}