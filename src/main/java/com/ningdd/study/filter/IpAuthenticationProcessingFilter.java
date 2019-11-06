package com.ningdd.study.filter;

import com.ningdd.study.authentication.IpAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ningdd
 * @date 2019/6/10/ 23:03
 */
public class IpAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
//    public IpAuthenticationProcessingFilter() {
//        // 使用 /ipVerify 该端点进行ip认证
//        super(new AntPathRequestMatcher("/ipVerify"));
//    }

    public IpAuthenticationProcessingFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // 获取host信息，封装成一个authentication对象
        IpAuthenticationToken authRequest = new IpAuthenticationToken(request.getRemoteHost());
        // 交给内部的AuthenticationManager去认证，实现解耦
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
