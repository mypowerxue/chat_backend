package com.example.demo.service.imp;

import com.example.demo.controller.socket.WebSocketServer;
import com.example.demo.controller.vo.TestMessageVo;
import com.example.demo.mapper.bean.TestFriend;
import com.example.demo.controller.vo.TestFriendVo;
import com.example.demo.mapper.bean.TestMessage;
import com.example.demo.mapper.bean.TestUser;
import com.example.demo.mapper.TestFriendDAO;
import com.example.demo.mapper.TestUserDAO;
import com.example.demo.service.TestFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TestFriendServiceImpl implements TestFriendService {

    @Autowired
    TestFriendDAO testFriendDAO;
    @Autowired
    TestUserDAO testUserDAO;

    @Override
    public boolean insert(TestFriend TestFriend) {
        int insert = testFriendDAO.insert(TestFriend);
        return insert != 0;
    }

    @Override
    public boolean update(TestFriend TestFriend) {
        int insert = testFriendDAO.updateByPrimaryKey(TestFriend);
        return insert != 0;
    }

    @Override
    public List<TestFriendVo> selectFriendAll(Integer userId, Integer type) {
        List<TestFriend> list = testFriendDAO.selectAll(userId, userId);
        List<TestFriendVo> friendLists = new ArrayList<>();
        for (TestFriend testFriend : list) {
            Integer status = testFriend.getStatus();
            Integer selectId = null;

            TestFriendVo TestFriendVo = new TestFriendVo();
            switch (type) {
                case 0: //查询申请列表
                    if (status == 0 || status == 1 || status == 5) {
                        selectId = testFriend.getFriendId();
                        if (selectId.equals(userId)) {
                            selectId = testFriend.getUserId();
                        }
                        if (!userId.equals(testFriend.getUserId()) && !userId.equals(testFriend.getFriendId())) {
                            selectId = null;
                        }else {
                            TestFriendVo.setUserId(testFriend.getUserId());
                            TestFriendVo.setFriendId(testFriend.getFriendId());
                        }
                    }
                    break;
                case 1: //查询好友列表
                    if (status == 1 || status == 3) {
                        selectId = testFriend.getFriendId();
                        if (selectId.equals(userId)) {
                            selectId = testFriend.getUserId();
                        }
                        TestFriendVo.setUserId(userId);
                        TestFriendVo.setFriendId(selectId);
                    }
                    break;
                case 2: //查询黑名单
                    if (status == 2) {
                        selectId = testFriend.getFriendId();
                        if (selectId.equals(userId)) {
                            selectId = testFriend.getUserId();
                        }
                    }
                    break;
                default:
                    return null;
            }

            if (selectId != null) {
                TestUser testUser = testUserDAO.selectByPrimaryKey(selectId);
                TestFriendVo.setAccount(testUser.getAccount());
                TestFriendVo.setStatus(status);
                TestFriendVo.setNickName(testUser.getNickName());
                TestFriendVo.setPicUrl(testUser.getPicUrl());
                friendLists.add(TestFriendVo);
            }
        }
        return friendLists;
    }

    @Override
    public TestFriend VerifyBinding(Integer userId, Integer friendId) {
        List<TestFriend> list = testFriendDAO.selectAll(userId, friendId);
        for (int i = 0; i < list.size(); i++) {
            TestFriend testFriend = list.get(i);
            if (
                    (!testFriend.getUserId().equals(userId) && !testFriend.getFriendId().equals(friendId)) ||
                            (!testFriend.getUserId().equals(friendId) && !testFriend.getFriendId().equals(userId))
            ) {
                //这俩人是好友
            }else {
                list.remove(i);
                i--;
            }
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public boolean notifyUserAddMessage(Integer userId, Integer friendId) {
        WebSocketServer server = WebSocketServer.clients.get(userId);
        if (server != null) {
            TestMessageVo testMessage = new TestMessageVo();
            testMessage.setSendType(3);
            testMessage.setSendId(userId);
            testMessage.setReceiveId(friendId);
            testMessage.setMessageType(1);
            testMessage.setTime(System.currentTimeMillis());
            testMessage.setMessage("做个朋友吧");
            server.sendMessage(testMessage);
            return true;
        }
        return false;
    }

    @Override
    public boolean notifyUserAddSuccess(Integer userId, Integer friendId) {
        WebSocketServer server = WebSocketServer.clients.get(userId);
        if (server != null) {
            TestMessageVo testMessage = new TestMessageVo();
            testMessage.setSendType(3);
            testMessage.setSendId(userId);
            testMessage.setReceiveId(friendId);
            testMessage.setMessageType(2);
            testMessage.setTime(System.currentTimeMillis());
            testMessage.setMessage("我们已经是好友啦");
            server.sendMessage(testMessage);
            return true;
        }
        return false;
    }

    @Override
    public boolean notifyUserAddError(Integer userId, Integer friendId) {
        WebSocketServer server = WebSocketServer.clients.get(userId);
        if (server != null) {
            TestMessageVo testMessage = new TestMessageVo();
            testMessage.setSendType(3);
            testMessage.setSendId(userId);
            testMessage.setReceiveId(friendId);
            testMessage.setMessageType(3);
            testMessage.setTime(System.currentTimeMillis());
            testMessage.setMessage("对方拒绝添加好友");
            server.sendMessage(testMessage);
            return true;
        }
        return false;
    }

}
