package com.ningdd.study.PO;

import lombok.Data;

import java.util.List;

/**
 * @author ningdd
 * @date 2019/6/6/ 11:34
 */
@Data
public class Permission {
    private Integer id;
    private String url;
    private String permissionName;

    private List<Role> roles;
}
