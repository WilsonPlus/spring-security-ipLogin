package com.ningdd.study.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ningdd.study.PO.Role;
import com.ningdd.study.mapper.RoleMapper;
import com.ningdd.study.service.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService {
    private final RoleMapper roleMapper;

    @Autowired
    RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public Role findRoleById(Integer id) {
        Role role = roleMapper.selectByPrimaryKey(id);

        /*
         * 获取当前用户的信息 因为身份信息是与线程绑定的，所以可以在程序的任何地方使用静态方法获取用户信息。 一个典型的获取当前登录用户的姓名的例子如下所示：
         */
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        System.out.println(username);
        return role;
    }
}