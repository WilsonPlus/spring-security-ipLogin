package com.ningdd.study.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author ningdd
 * @date 2019/6/5/ 22:56
 *
 * UserDetailsService只负责从特定的地方（通常是数据库）加载用户信息，仅此而已
 */
public interface IMemberService extends UserDetailsService {
}
