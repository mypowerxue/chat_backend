package com.example.demo.service;

import com.example.demo.mapper.bean.TestMessage;

import java.util.List;

public interface TestMessageService {

    boolean insertMessage(TestMessage testMessage); //插入

    boolean updateMessage(TestMessage testMessage); //修改

    List<TestMessage> selectMessage(Integer userId, Integer status); //查找聊天记录

}
