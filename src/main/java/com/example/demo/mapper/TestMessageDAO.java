package com.example.demo.mapper;

import com.example.demo.mapper.bean.TestMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TestMessageDAO继承基类
 */
@Repository
public interface TestMessageDAO extends MyBatisBaseDao<TestMessage, Integer> {

    List<TestMessage> selectAll(Integer userId, Integer friendId);   //查询全部

}