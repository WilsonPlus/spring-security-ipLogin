package com.ningdd.study.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ningdd
 */
@Getter
@Setter
public class IpAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 3977319930176100344L;
    private String ip;

    /**
     * 注意这个构造方法是认证时使用的，其默认值就是false
     */
    public IpAuthenticationToken(String ip) {
        super(null);
        this.ip = ip;
        super.setAuthenticated(false);
    }

    /**
     * 注意这个构造方法是认证成功后使用的
     */
    public IpAuthenticationToken(String ip, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.ip = ip;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.ip;
    }

}