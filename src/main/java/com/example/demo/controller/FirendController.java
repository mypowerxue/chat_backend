package com.example.demo.controller;

import com.example.demo.controller.token.VerifyToken;
import com.example.demo.mapper.bean.TestFriend;
import com.example.demo.controller.vo.TestFriendVo;
import com.example.demo.mapper.bean.TestUser;
import com.example.demo.service.TestFriendService;
import com.example.demo.service.TestUserService;
import com.example.demo.utils.ResultModel;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/10.
 */
@Api(value = "好友模块")
@RestController //返回注解
@RequestMapping("/friend")    //接口二级路径
public class FirendController {

    @Autowired
    private TestFriendService testFirendService;
    @Autowired
    private TestUserService testUserService;


    /**
     * 查找用户接口
     */
    @VerifyToken
    @ApiOperation(value = "查找用户", notes = "查找用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "account", value = "账号", required = true, paramType = "query", dataType = "integer")
    })
    @RequestMapping(value = "/getUserList", method = RequestMethod.GET)
    public Object getFriendList(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "account") String account
    ) {
        if (account == null)
            return new ResponseEntity<>(ResultModel.error("account cannot be empty"), HttpStatus.OK);
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK);

        TestUser testUser = testUserService.selectByAccount(account);
        if (testUser == null) {
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK);
        }


        ArrayList<TestFriendVo> listVo = new ArrayList<>();

        if (userId.equals(testUser.getUserId())) {
            return new ResponseEntity<>(ResultModel.error(listVo), HttpStatus.OK);
        }

        TestFriendVo testFriendVo = new TestFriendVo();
        testFriendVo.setUserId(userId);
        testFriendVo.setFriendId(testUser.getUserId());
        testFriendVo.setNickName(testUser.getNickName());
        testFriendVo.setAccount(testUser.getAccount());
        testFriendVo.setPicUrl(testUser.getPicUrl());
        //验证这两个用户是否添加过好友 如果没有添加过就是-1
        TestFriend testFriend = testFirendService.VerifyBinding(userId, testUser.getUserId());
        if (testFriend == null) {
            testFriendVo.setStatus(-1);
        } else {
            testFriendVo.setStatus(testFriend.getStatus());
        }

        listVo.add(testFriendVo);
        return new ResponseEntity<>(ResultModel.ok(listVo), HttpStatus.OK);
    }


    /**
     * 获取好友列表接口
     */
    @VerifyToken
    @ApiOperation(value = "获取好友列表", notes = "获取好友列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "type", value = "0是添加列表 1好友 2拉黑 ", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/getFriendList", method = RequestMethod.GET)
    public Object getFriendList(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "type", required = false, defaultValue = "1") Integer type
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK);
        if (type == null)
            return new ResponseEntity<>(ResultModel.error("type cannot be empty"), HttpStatus.OK);
        if (type != 0 && type != 1 && type != 2)
            return new ResponseEntity<>(ResultModel.error("type is correct"), HttpStatus.OK);

        List<TestFriendVo> friendLists = testFirendService.selectFriendAll(userId, type);
        return new ResponseEntity<>(ResultModel.ok(friendLists), HttpStatus.OK);
    }

    /**
     * 发送添加好友请求接口
     */
    @VerifyToken
    @ApiOperation(value = "发送添加好友请求", notes = "发送添加好友请求")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "friendId", value = "好友ID", required = true, paramType = "query", dataType = "integer"),
    })
    @RequestMapping(value = "/sendAddFriendRequest", method = RequestMethod.POST)
    public Object addFriend(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "friendId") Integer friendId
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK);
        if (friendId == null)
            return new ResponseEntity<>(ResultModel.error("friendId cannot be empty"), HttpStatus.OK);
        if (userId.equals(friendId))
            return new ResponseEntity<>(ResultModel.error("自己不能添加自己好友"), HttpStatus.OK);

        //验证自己用户是否被冻结
        TestUser testUser = testUserService.selectById(userId);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK);
        Integer testUserStatus = testUser.getStatus();
        if (testUserStatus == null || testUserStatus != 0)
            return new ResponseEntity<>(ResultModel.error("users are frozen"), HttpStatus.OK);

        //验证好友用户是否被冻结
        TestUser friendUser = testUserService.selectById(friendId);
        if (friendUser == null)
            return new ResponseEntity<>(ResultModel.error("friend user does not exist"), HttpStatus.OK);
        Integer friendUserStatus = testUser.getStatus();
        if (friendUserStatus == null || friendUserStatus != 0)
            return new ResponseEntity<>(ResultModel.error("friends are frozen"), HttpStatus.OK);

        //验证这两人是否绑定过关系
        TestFriend testFirend = testFirendService.VerifyBinding(userId, friendId);
        if (testFirend != null) {
            Integer status = testFirend.getStatus();
            if (status != null) {
                switch (status) {
                    case 0: //等待同意
                        return new ResponseEntity<>(ResultModel.error("do not add again"), HttpStatus.OK);
                    case 1: //好友
                        return new ResponseEntity<>(ResultModel.error("has been a good friend"), HttpStatus.OK);
                    case 2: //左拉黑
                        return new ResponseEntity<>(ResultModel.error("please remove him from the blacklist"), HttpStatus.OK);
                    case 3: //右拉黑
                        return new ResponseEntity<>(ResultModel.error("has been a good friends"), HttpStatus.OK);
                    case 4: //删除
                    case 5: //对方不同意状态
                        //重新改状态为未确认
                        testFirend.setStatus(0);
                        testFirend.setCreateTime(new Date(System.currentTimeMillis()));
                        break;
                }
            }
            //修改数据库
            boolean update = testFirendService.update(testFirend);
            if (!update)
                return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
        } else {
            //执行绑定
            testFirend = new TestFriend();
            testFirend.setUserId(userId);
            testFirend.setFriendId(friendId);
            testFirend.setCreateTime(new Date(System.currentTimeMillis()));
            testFirend.setStatus(0);

            //重新插入数据库
            boolean insert = testFirendService.insert(testFirend);
            if (!insert)
                return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
        }

        //异步发送通知给指定用户 通知发起添加好友请求
        boolean isNotifyUsers = testFirendService.notifyUserAddMessage(userId, friendId);
        if (!isNotifyUsers)
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);


        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }

    /**
     * 确认添加好友接口
     */
    @VerifyToken
    @ApiOperation(value = "确认添加好友", notes = "确认添加好友")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "friendId", value = "好友ID", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "type", value = "是否确认添加 1确认 2不同意", required = true, paramType = "query", dataType = "integer"),
    })
    @RequestMapping(value = "/confirmAddFriend", method = RequestMethod.POST)
    public Object confirmAddFriend(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "friendId") Integer friendId,
            @RequestParam(name = "type") Integer type
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK);
        if (friendId == null)
            return new ResponseEntity<>(ResultModel.error("friendId cannot be empty"), HttpStatus.OK);
        if (type == null)
            return new ResponseEntity<>(ResultModel.error("type cannot be empty"), HttpStatus.OK);
        if (type != 1 && type != 2)
            return new ResponseEntity<>(ResultModel.error("type is correct"), HttpStatus.OK);

        //验证自己用户是否被冻结
        TestUser testUser = testUserService.selectById(userId);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK);
        Integer testUserStatus = testUser.getStatus();
        if (testUserStatus == null || testUserStatus != 0)
            return new ResponseEntity<>(ResultModel.error("users are frozen"), HttpStatus.OK);

        //验证好友用户是否被冻结
        TestUser friendUser = testUserService.selectById(friendId);
        if (friendUser == null)
            return new ResponseEntity<>(ResultModel.error("friendUser does not exist"), HttpStatus.OK);
        Integer friendUserStatus = testUser.getStatus();
        if (friendUserStatus == null || friendUserStatus != 0)
            return new ResponseEntity<>(ResultModel.error("friend are frozen"), HttpStatus.OK);

        //验证这两人是否绑定过关系
        TestFriend testFirend = testFirendService.VerifyBinding(userId, friendId);
        if (testFirend == null)
            return new ResponseEntity<>(ResultModel.error("the other party did not add you as a friend"), HttpStatus.OK);
        else {
            Integer status = testFirend.getStatus();
            if (status == null || status != 0) { //是否是等待同意状态
                return new ResponseEntity<>(ResultModel.error("the other party did not add you as a friend"), HttpStatus.OK);
            }
        }

        //修改数据库
        if (type == 1) {
            //状态改变为同意
            testFirend.setStatus(1);
            testFirend.setCreateTime(new Date(System.currentTimeMillis()));
            boolean update = testFirendService.update(testFirend);
            if (!update)
                return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
            //异步发送通知给指定用户 同意
            boolean isNotifyUsers = testFirendService.notifyUserAddSuccess(userId, friendId);
            if (!isNotifyUsers)
                return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
        }
        if (type == 2) {
            //状态改变为不同意
            testFirend.setStatus(5);
            testFirend.setCreateTime(new Date(System.currentTimeMillis()));
            boolean update = testFirendService.update(testFirend);
            if (!update)
                return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
            //异步发送通知给指定用户 不同意
            boolean isNotifyUsers = testFirendService.notifyUserAddError(userId, friendId);
            if (!isNotifyUsers)
                return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
        }

        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }

    /**
     * 删除好友接口
     */
    @VerifyToken
    @ApiOperation(value = "删除好友", notes = "删除好友")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "friendId", value = "好友ID", required = true, paramType = "query", dataType = "integer"),
    })
    @RequestMapping(value = "/deleteFriends", method = RequestMethod.POST)
    public Object updateFriend(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "friendId") Integer friendId
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK);
        if (friendId == null)
            return new ResponseEntity<>(ResultModel.error("friendId cannot be empty"), HttpStatus.OK);

        //验证自己用户是否存在
        TestUser testUser = testUserService.selectById(userId);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK);

        //验证好友用户是否存在
        TestUser friendUser = testUserService.selectById(userId);
        if (friendUser == null)
            return new ResponseEntity<>(ResultModel.error("friend user does not exist"), HttpStatus.OK);

        //验证这两人是否好友
        TestFriend testFirend = testFirendService.VerifyBinding(userId, friendId);
        if (testFirend == null)
            return new ResponseEntity<>(ResultModel.error("has been a good friends"), HttpStatus.OK);

        testFirend.setStatus(4);//删除
        boolean update = testFirendService.update(testFirend);
        if (!update) {
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }

    /**
     * 拉黑好友接口
     */
    @VerifyToken
    @ApiOperation(value = "拉黑好友", notes = "拉黑好友")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "friendId", value = "好友ID", required = true, paramType = "query", dataType = "integer"),
    })
    @RequestMapping(value = "/darkFriends", method = RequestMethod.POST)
    public Object darkFriends(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "friendId") Integer friendId
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK);
        if (friendId == null)
            return new ResponseEntity<>(ResultModel.error("friendId cannot be empty"), HttpStatus.OK);

        //验证自己用户是否存在
        TestUser testUser = testUserService.selectById(userId);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK);

        //验证好友用户是否存在
        TestUser friendUser = testUserService.selectById(userId);
        if (friendUser == null)
            return new ResponseEntity<>(ResultModel.error("friend user does not exist"), HttpStatus.OK);

        //验证这两人是否好友
        TestFriend testFirend = testFirendService.VerifyBinding(userId, friendId);
        if (testFirend == null)
            return new ResponseEntity<>(ResultModel.error("has been a good friends"), HttpStatus.OK);

        //判断是谁拉黑的谁
        if (userId.equals(testFirend.getUserId())) {
            testFirend.setStatus(2);    //发起者拉黑好友
        } else {
            testFirend.setStatus(3);    //好友拉黑发起者
        }
        boolean update = testFirendService.update(testFirend);
        if (!update) {
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
        }

        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }


    /**
     * 恢复拉黑好友接口
     */
    @VerifyToken
    @ApiOperation(value = "恢复拉黑好友", notes = "恢复拉黑好友")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "friendId", value = "好友ID", required = true, paramType = "query", dataType = "integer"),
    })
    @RequestMapping(value = "/resetDarkFriends", method = RequestMethod.POST)
    public Object resetDarkFriends(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "friendId") Integer friendId
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK);
        if (friendId == null)
            return new ResponseEntity<>(ResultModel.error("friendId cannot be empty"), HttpStatus.OK);

        //验证自己用户是否存在
        TestUser testUser = testUserService.selectById(userId);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK);

        //验证好友用户是否存在
        TestUser friendUser = testUserService.selectById(userId);
        if (friendUser == null)
            return new ResponseEntity<>(ResultModel.error("friend user does not exist"), HttpStatus.OK);

        //验证这两人是否好友
        TestFriend testFirend = testFirendService.VerifyBinding(userId, friendId);
        if (testFirend == null)
            return new ResponseEntity<>(ResultModel.error("has been a good friends"), HttpStatus.OK);

        //判断是谁拉黑的谁
        if (userId.equals(testFirend.getUserId()) && testFirend.getStatus() == 2) {
            //是发起者调用的 并且是发起者拉黑的 可以恢复
            testFirend.setStatus(1);
        } else if (userId.equals(testFirend.getFriendId()) && testFirend.getStatus() == 3) {
            //是好友方调用的 并且是好友拉黑的 可以恢复
            testFirend.setStatus(1);
        } else if (testFirend.getStatus() == 1) {
            return new ResponseEntity<>(ResultModel.error("has been a good friends"), HttpStatus.OK);
        } else if (testFirend.getStatus() == 4) {
            return new ResponseEntity<>(ResultModel.error("delete cannot be restored"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultModel.error("no privileges"), HttpStatus.OK);
        }

        boolean update = testFirendService.update(testFirend);
        if (!update) {
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
        }

        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }
}
