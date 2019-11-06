package com.ningdd.study.PO;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ningdd
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Member implements Serializable {
    private static final long serialVersionUID = -3257302941210149912L;

    private Integer id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String question;

    private String answer;

    private Integer status;

    private String statusStr;

    private List<Role> roles;

    private Date createTime;

    private Date updateTime;

    public Member(Integer id, String username, String password, String email, String phone, String question, String answer, Integer status, String statusStr, Date createTime, Date updateTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.question = question;
        this.answer = answer;
        this.status = status;
        this.statusStr = statusStr;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}