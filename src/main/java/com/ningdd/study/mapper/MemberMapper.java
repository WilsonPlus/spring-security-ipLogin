package com.ningdd.study.mapper;
import com.ningdd.study.PO.Member;

public interface MemberMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Member record);

    int insertSelective(Member record);

    Member selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Member record);

    int updateByPrimaryKey(Member record);

    // 当传入的参数有多个的时候，使用@Param注解
//    User loginSelect(@Param("username") String username, @Param("password") String password);

    public Member findByUserName(String username);

}