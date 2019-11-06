package com.ningdd.study.controller;
import com.ningdd.study.PO.Role;
import com.ningdd.study.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ningdd
 * @date 2019/6/10/ 23:03
 */
@Controller
@RequestMapping("/admin/")
public class RoleController {
    private final IRoleService roleService;

    @Autowired
    RoleController(IRoleService roleService){
        this.roleService = roleService;
    }

    @RequestMapping("getRole.action")
    @ResponseBody
    public Role get(Integer id){
        if(null == id || id.intValue() < 1) {
            return null;
        }
        return roleService.findRoleById(id);
    }
}
