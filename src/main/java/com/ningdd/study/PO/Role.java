package com.ningdd.study.PO;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author ningdd
 * @date 2019/6/6/ 11:30
 */
@Data
public class Role {
    private Integer id;
    private String roleName;
    private String roleDesc;
    private Date createTime;
    private Date updateTime;

    List<Permission> permissions;
    List<Member> users;

    public Role(Integer id, String roleName, String roleDesc, Date createTime, Date updateTime) {
        this.id = id;
        this.roleName = roleName;
        this.roleDesc = roleDesc;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
