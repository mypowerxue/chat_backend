package com.example.demo.mapper;

import com.example.demo.mapper.bean.TestFriend;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TestFriendDAO继承基类
 */
@Repository
public interface TestFriendDAO extends MyBatisBaseDao<TestFriend, Integer> {

    List<TestFriend> selectAll(Integer userId, Integer friendId);   //查询全部

}