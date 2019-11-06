package com.ningdd.study.mapper;

import com.ningdd.study.PO.Role;

import java.util.List;

/**
 * @author ningdd
 * @date 2019/6/9/ 22:10
 */
public interface RoleMapper {
    Role selectByPrimaryKey(Integer id);

    List<Role> selectByUsername(String username);
}
