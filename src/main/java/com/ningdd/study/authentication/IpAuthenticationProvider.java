package com.ningdd.study.authentication;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author ningdd
 * @date 2019/6/10/ 23:03
 */
/*
@Deprecated
注解可以对类实现弃用
*/
public class IpAuthenticationProvider implements AuthenticationProvider {
    private final static Map<String, SimpleGrantedAuthority> IP_AUTHORITY_MAP = new ConcurrentHashMap<>();

    //维护一个ip白名单列表，每个ip对应一定的权限
    static {
        IP_AUTHORITY_MAP.put("127.0.0.1", new SimpleGrantedAuthority("ROLE_ADMIN"));
        IP_AUTHORITY_MAP.put("localhost", new SimpleGrantedAuthority("ROLE_ADMIN"));
        IP_AUTHORITY_MAP.put("192.168.43.101", new SimpleGrantedAuthority("ROLE_ADMIN"));
        IP_AUTHORITY_MAP.put("192.168.127.128", new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        IpAuthenticationToken ipAuthenticationToken = (IpAuthenticationToken) authentication;
        String ip = ipAuthenticationToken.getIp();
        SimpleGrantedAuthority simpleGrantedAuthority = IP_AUTHORITY_MAP.get(ip);
        // 不在白名单列表中
        if (simpleGrantedAuthority == null) {
            return null;
        } else {
            // 封装权限信息，并且此时身份已经被认证
            return new IpAuthenticationToken(ip, Collections.singletonList(simpleGrantedAuthority));
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        // 只支持IpAuthenticationToken该身份,返回的是两个对象的class比较结果是否一致
        return (IpAuthenticationToken.class.isAssignableFrom(authentication));
    }
}