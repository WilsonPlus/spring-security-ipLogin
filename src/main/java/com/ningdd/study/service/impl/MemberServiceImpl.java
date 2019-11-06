package com.ningdd.study.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ningdd.study.PO.Member;
import com.ningdd.study.PO.Role;
import com.ningdd.study.mapper.MemberMapper;
import com.ningdd.study.mapper.RoleMapper;
import com.ningdd.study.service.IMemberService;

/**
 * @author ningdd
 * @date 2019/6/5/ 22:43
 */
@Service
//    使用默认实现类JdbcDaoImpl
public class MemberServiceImpl implements IMemberService {
    @Autowired(required = true)
    private MemberMapper memberMapper;
    @Autowired(required = true)
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(new BCryptPasswordEncoder().encode(username));
        if(StringUtils.isEmpty(username)) {
            throw new BadCredentialsException("用户名不能为空");
        }

        Member member = memberMapper.findByUserName(username);
        if (member == null) {
            throw new BadCredentialsException("用户名不存在");
        }
        return new User(username, member.getPassword(), getAuthorities(username));
    }

    private ArrayList<? extends GrantedAuthority> getAuthorities(String username) {
        List<Role> roles = roleMapper.selectByUsername(username);
        ArrayList<SimpleGrantedAuthority> list = new ArrayList<>();
        for (Role item : roles) {
            list.add(new SimpleGrantedAuthority(item.getRoleName()));
        }
        return list;
    }
}