package com.example.demo.service;

import com.example.demo.mapper.bean.TestFriend;
import com.example.demo.controller.vo.TestFriendVo;

import java.util.List;

public interface TestFriendService {

    boolean insert(TestFriend testUser); //插入

    boolean update(TestFriend testUser); //更新

    List<TestFriendVo> selectFriendAll(Integer userId, Integer status); //查询所有用户

    TestFriend VerifyBinding(Integer userId, Integer friendId); //查询指定ID

    boolean notifyUserAddMessage(Integer userId, Integer friendId);  //通知用户添加好友请求信息

    boolean notifyUserAddSuccess(Integer userId, Integer friendId);  //通知用户添加好友成功

    boolean notifyUserAddError(Integer userId, Integer friendId);  //通知用户添加好友被拒绝

}
