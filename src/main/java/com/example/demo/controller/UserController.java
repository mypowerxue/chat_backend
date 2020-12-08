package com.example.demo.controller;

import com.example.demo.controller.token.*;
import com.example.demo.controller.vo.LoginInfoVo;
import com.example.demo.controller.vo.UserInfoVo;
import com.example.demo.mapper.bean.TestUser;
import com.example.demo.service.RedisService;
import com.example.demo.service.TestUserService;
import com.example.demo.utils.MatcherConst;
import com.example.demo.utils.ResultModel;
import io.swagger.annotations.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/10.
 */
@Api(value = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TestUserService testUserService;
    @Autowired
    private RedisService redisService;

    /**
     * 登录接口
     *
     * @param account  账号
     * @param password 密码
     */
    @PassToken
    @ApiOperation(value = "登录", notes = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password
    ) {
        if (account == null || account.isEmpty())
            return new ResponseEntity<>(ResultModel.error("account cannot be empty"), HttpStatus.OK); //验证账号输入是否有误
        if (password == null || password.isEmpty())
            return new ResponseEntity<>(ResultModel.error("password cannot be empty"), HttpStatus.OK); //验证密码输入是否有误

        if (!account.matches(MatcherConst.ACCOUNT_MATCHER))
            return new ResponseEntity<>(ResultModel.error("account input error"), HttpStatus.OK); //验证账号输入是否有误
        if (!password.matches(MatcherConst.PASSWORD_MATCHER))
            return new ResponseEntity<>(ResultModel.error("password input error"), HttpStatus.OK); //验证密码输入是否有误

        //查找用户
        TestUser testUser = testUserService.selectByAccount(account);

        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK); //验证账号是否存在
        if (testUser.getStatus() == null || testUser.getStatus() != 0)
            return new ResponseEntity<>(ResultModel.error("accounts frozen"), HttpStatus.OK); //验证账号是否被冻结
        if (!password.equals(testUser.getPassword()))
            return new ResponseEntity<>(ResultModel.error("incorrect password input"), HttpStatus.OK);//验证密码是否输入正确

        //登陆成功 并更新表
        testUser.setLoginTime(new Date(System.currentTimeMillis()));
        testUser.setStatus(0);
        boolean update = testUserService.update(testUser);
        if (!update) {
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);
        }

        //给用户返回Vo
        LoginInfoVo testUserVo = new LoginInfoVo();
        String userId = testUser.getUserId() + "";
        redisService.generateToken(userId);
        testUserVo.setToken(redisService.getToken(userId));
        testUserVo.setUserId(testUser.getUserId());

        return new ResponseEntity<>(ResultModel.ok(testUserVo), HttpStatus.OK);
    }

    /**
     * 注册接口
     *
     * @param account  账号
     * @param password 密码
     * @param nickName 昵称
     */
    @ApiOperation(value = "注册", notes = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "nickName", value = "昵称", paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Object register(
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "nickName", required = false) String nickName
    ) {
        if (account == null || account.isEmpty())
            return new ResponseEntity<>(ResultModel.error("account cannot be empty"), HttpStatus.OK); //验证账号不能为空
        if (password == null || password.isEmpty())
            return new ResponseEntity<>(ResultModel.error("password cannot be empty"), HttpStatus.OK); //验证密码不能为空
        if (!account.matches(MatcherConst.ACCOUNT_MATCHER))
            return new ResponseEntity<>(ResultModel.error("account input error"), HttpStatus.OK); //验证账号输入是否有误
        if (!password.matches(MatcherConst.PASSWORD_MATCHER))
            return new ResponseEntity<>(ResultModel.error("password input error"), HttpStatus.OK); //验证密码输入是否有误

        //查找数据库是否由此用户
        TestUser testUser = testUserService.selectByAccount(account);
        if (testUser != null)
            return new ResponseEntity<>(ResultModel.error("user does exist"), HttpStatus.OK); //验证账号是否存在

        //开始插入数据库
        testUser = new TestUser();
        testUser.setNickName(nickName);
        testUser.setAccount(account);
        testUser.setPassword(password);
        testUser.setStatus(0);
        testUser.setCreateTime(new Date(System.currentTimeMillis()));
        boolean insert = testUserService.insert(testUser);
        if (!insert)
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);

        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }


    /**
     * 忘记密码接口
     *
     * @param account     账号
     * @param newPassword 新密码
     */
    @ApiOperation(value = "忘记密码", notes = "忘记密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "newPassword", value = "密码", required = true, paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public Object forgetPassword(
            @RequestParam(name = "account") String account,
            @RequestParam(name = "newPassword") String newPassword
    ) {
        if (account == null || account.isEmpty())
            return new ResponseEntity<>(ResultModel.error("account cannot be empty"), HttpStatus.OK); //验证账号输入是否有误
        if (newPassword == null || newPassword.isEmpty())
            return new ResponseEntity<>(ResultModel.error("newPassword cannot be empty"), HttpStatus.OK); //验证密码输入是否有误

        if (!account.matches(MatcherConst.ACCOUNT_MATCHER))
            return new ResponseEntity<>(ResultModel.error("account input error"), HttpStatus.OK); //验证账号输入是否有误
        if (!newPassword.matches(MatcherConst.PASSWORD_MATCHER))
            return new ResponseEntity<>(ResultModel.error("newPassword input error"), HttpStatus.OK); //验证密码输入是否有误

        //查找数据库是否由此用户
        TestUser testUser = testUserService.selectByAccount(account);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK); //验证账号是否存在
        if (testUser.getStatus() == null || testUser.getStatus() != 0)
            return new ResponseEntity<>(ResultModel.error("accounts frozen"), HttpStatus.OK); //验证账号是否被冻结

        //修改密码 并更新表
        testUser.setPassword(newPassword);
        testUser.setStatus(0);
        boolean update = testUserService.update(testUser);
        if (!update)
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);

        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }

    /**
     * 修改密码接口
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public Object resetPassword(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "oldPassword") String oldPassword,
            @RequestParam(name = "newPassword") String newPassword
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK); //验证旧密码输入是否有误
        if (oldPassword == null || oldPassword.isEmpty())
            return new ResponseEntity<>(ResultModel.error("oldPassword cannot be empty"), HttpStatus.OK); //验证旧密码输入是否有误
        if (newPassword == null || newPassword.isEmpty())
            return new ResponseEntity<>(ResultModel.error("newPassword cannot be empty"), HttpStatus.OK); //验证新密码输入是否有误

        if (!oldPassword.matches(MatcherConst.PASSWORD_MATCHER))
            return new ResponseEntity<>(ResultModel.error("oldPassword input error"), HttpStatus.OK); //验证旧密码入是否有误
        if (!newPassword.matches(MatcherConst.PASSWORD_MATCHER))
            return new ResponseEntity<>(ResultModel.error("newPassword input error"), HttpStatus.OK); //验证新密码输入是否有误

        if (oldPassword.equals(newPassword))
            return new ResponseEntity<>(ResultModel.error("password duplication"), HttpStatus.OK); //验证新旧密码是否输入相同

        //查找用户
        TestUser testUser = testUserService.selectById(userId);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK); //验证账号是否存在
        if (!oldPassword.equals(testUser.getPassword()))
            return new ResponseEntity<>(ResultModel.error("incorrect oldPassword input"), HttpStatus.OK);//验证旧密码是否输入正确

        testUser.setPassword(newPassword);
        testUser.setStatus(0);
        boolean update = testUserService.update(testUser);
        if (update)
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);

        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }


    /**
     * 获取用户信息接口
     */
    @VerifyToken
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
    })
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public Object getUserInfo(
            @RequestParam(name = "userId") Integer userId
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK); //验证userId是否为空

        //查找数据库是否由此用户
        TestUser testUser = testUserService.selectById(userId);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK); //验证账号是否存在
        if (testUser.getStatus() == null || testUser.getStatus() != 0)
            return new ResponseEntity<>(ResultModel.error("accounts frozen"), HttpStatus.OK); //验证账号是否被冻结

        //给用户返回Vo
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setAccount(testUser.getAccount());
        userInfoVo.setNickName(testUser.getNickName());
        userInfoVo.setPicUrl(testUser.getPicUrl());
        userInfoVo.setSex(testUser.getSex());
        userInfoVo.setAge(testUser.getAge());
        userInfoVo.setBirthday(testUser.getBirthday());

        return new ResponseEntity<>(ResultModel.ok(userInfoVo), HttpStatus.OK);
    }

    /**
     * 修改用户信息接口
     */
    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "nikeName", value = "昵称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sex", value = "性别 男0/女1", paramType = "query", dataType = "Integer", defaultValue = "0"),
            @ApiImplicitParam(name = "age", value = "年龄", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "birthday", value = "生日", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    public Object updateUserInfo(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "nikeName", required = false) String nikeName,
            @RequestParam(name = "sex", required = false) Integer sex,
            @RequestParam(name = "age", required = false) Integer age,
            @RequestParam(name = "birthday", required = false) String birthday
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK); //验证userId是否为空

        //查找数据库是否由此用户
        TestUser testUser = testUserService.selectById(userId);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK); //验证账号是否存在

        boolean isUpdate = false;

        if (nikeName != null && !nikeName.isEmpty()) {
            testUser.setNickName(nikeName);
            isUpdate = true;
        }
        if (sex != null) {
            testUser.setSex(sex);
            isUpdate = true;
        }
        if (age != null) {
            testUser.setAge(age);
            isUpdate = true;
        }
        if (birthday != null && !birthday.isEmpty()) {
            testUser.setBirthday(birthday);
            isUpdate = true;
        }

        if (!isUpdate)
            return new ResponseEntity<>(ResultModel.error("no data to be updated"), HttpStatus.OK);

        boolean update = testUserService.update(testUser);
        if (!update)
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);

        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }


    /**
     * 上传头像接口
     */
    @ApiOperation(value = "上传头像", notes = "上传头像")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
    })
    @RequestMapping(value = "/upLoadIcon", method = RequestMethod.POST)
    @ResponseBody
    public Object upLoadIcon(
            @RequestParam(name = "userId") Integer userId,
            @ApiParam(value = "上传头像", required = true)
            @RequestParam(name = "file") MultipartFile file
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK); //验证userId是否为空
        if (file == null || file.isEmpty())
            return new ResponseEntity<>(ResultModel.error("file cannot be empty"), HttpStatus.OK); //验证File是否为空
        try {
            if (ImageIO.read(file.getInputStream()) == null)
                return new ResponseEntity<>(ResultModel.error("file incorrect format"), HttpStatus.OK); //验证File是否是图片
        } catch (IOException e) {
            return new ResponseEntity<>(ResultModel.error(e.getMessage()), HttpStatus.OK); //验证File是否是图片
        }

        //查找数据库是否由此用户
        TestUser testUser = testUserService.selectById(userId);
        if (testUser == null)
            return new ResponseEntity<>(ResultModel.error("user does not exist"), HttpStatus.OK); //验证账号是否存在

        String fileName = "F:/project/Test/iconFiles/" + file.getOriginalFilename();

        //保存图片
        try {
            File dest = new File(fileName);
            if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                dest.getParentFile().mkdir();
            }
            file.transferTo(dest); //保存文件
        } catch (IllegalStateException | IOException e) {
            return new ResponseEntity<>(ResultModel.error(e.getMessage()), HttpStatus.OK); //验证账号是否存在
        }

        testUser.setPicUrl(fileName);
        boolean update = testUserService.update(testUser);
        if (!update)
            return new ResponseEntity<>(ResultModel.error("account anomalies"), HttpStatus.OK);

        return new ResponseEntity<>(ResultModel.ok(), HttpStatus.OK);
    }

}
