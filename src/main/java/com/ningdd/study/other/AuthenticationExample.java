package com.ningdd.study.other;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationExample {
    private static AuthenticationManager am = new SampleAuthenticationManager();

    public static void main(String[] args) throws Exception {
        authenticationExampleMain(args);
    }

    public static void authenticationExampleMain(String[] args) throws Exception {
        @SuppressWarnings("unused")
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("username password:");
            String name = "admin";
            String password = "admin";
            try {
                Authentication request = new UsernamePasswordAuthenticationToken(name, password);
                Authentication result = am.authenticate(request);
                SecurityContextHolder.getContext().setAuthentication(result);
                in.close();
                break;
            } catch (AuthenticationException e) {
                System.out.println("Authentication failed: " + e.getMessage());
            }
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("UserDetails：" + (UserDetails)authentication.getDetails());

        System.out.println("成功通过身份验证。");
        System.out.println("authentication包含: " + authentication);
    }
}

class SampleAuthenticationManager implements AuthenticationManager {
    static final List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();

    static {
        AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        /*
        Authentication的getCredentials()与UserDetails中的getPassword()需要被区分对待，
        前者是用户提交的密码凭证，
        后者是用户正确的密码，认证器其实就是对这两者的比对。
        */
        System.out.println(auth.getName() + ":" + auth.getCredentials());

        if (auth.getName().equals(auth.getCredentials())) {
            return new UsernamePasswordAuthenticationToken(auth.getName(),
                    auth.getCredentials(), AUTHORITIES);
        }
        throw new BadCredentialsException("Bad Credentials");
    }
}