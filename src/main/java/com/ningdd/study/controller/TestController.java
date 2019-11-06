package com.ningdd.study.controller;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ningdd
 * @date 2019/6/11/ 19:33
 */
@Controller
@RequestMapping("/test/")
public class TestController {
    /*
    不返回任何值的时候需一下任一注解，
    1.@ResponseStatus(value = HttpStatus.OK)
    2.@ResponseBody
    不然返回的就是 prefix前缀 + 请求的地址 + suffix后缀组成。
    例：请求这个接口，
    前缀是：/WEB-INF/jsps，后缀配置的是.jsp，再加上requestMapping + 处理类方法名，组合组合一起，即：
    /WEB-INF/jspstest/get.jsp
   
    */
    @RequestMapping("get.action")
    @ResponseStatus(value = HttpStatus.OK)
    public void get() {
        // 获取一个认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        @SuppressWarnings("unused")
        Object credentials = authentication.getCredentials();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
            // 认证成功之后会进行擦出密码操作，别想着拿密码了
            System.out.println(((UserDetails) principal).getPassword());
        } else {
            username = principal.toString();
        }
        System.out.println("我肯定是空的啦。。。"+username);

        /**
         getAuthorities()，权限信息列表，默认是GrantedAuthority接口的一些实现类，通常是代表权限信息的一系列字符串。
         getCredentials()，密码信息，用户输入的密码字符串，在认证过后通常会被移除，用于保障安全。
         getDetails()，细节信息，web应用中的实现接口通常为 WebAuthenticationDetails，它记录了访问者的ip地址和sessionId的值。
         getPrincipal()，敲黑板！！！最重要的身份信息，大部分情况下返回的是UserDetails接口的实现类，也是框架中的常用接口之一。UserDetails接口将会在下面的小节重点介绍。
         */
    }
    
    @RequestMapping("newget.action")    
    @ResponseStatus(value = HttpStatus.OK)
    public void newGet(String parm) {
        System.out.println(parm);
    }
}
