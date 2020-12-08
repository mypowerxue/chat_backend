package com.example.demo.controller;

import com.example.demo.ConfigClass;
import com.example.demo.controller.token.VerifyToken;
import com.example.demo.controller.vo.FileInfoVo;
import com.example.demo.controller.vo.TestFriendVo;
import com.example.demo.mapper.bean.TestFriend;
import com.example.demo.mapper.bean.TestUser;
import com.example.demo.service.TestFriendService;
import com.example.demo.service.TestUserService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/10.
 */
@Api(value = "聊天模块")
@RestController //返回注解
@RequestMapping("/chat")    //接口二级路径
public class ChatController {

    @Autowired
    private TestFriendService testFirendService;
    @Autowired
    private TestUserService testUserService;


    /**
     * 发送图片接口
     */
    @ApiOperation(value = "发送图片", notes = "发送图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
    })
    @RequestMapping(value = "/sendImage", method = RequestMethod.POST)
    @ResponseBody
    public Object sendImage(
            @RequestParam(name = "userId") Integer userId,
            @ApiParam(value = "传输图片", required = true)
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

        String fileName = ConfigClass.IMG_ABSOLUTE_PATH + "chat/" + userId + "/" + file.getOriginalFilename();

        //保存图片
        try {
            File dest = new File(fileName);
            if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest); //保存文件
        } catch (IllegalStateException | IOException e) {
            return new ResponseEntity<>(ResultModel.error(e.getMessage()), HttpStatus.OK); //验证账号是否存在
        }

        FileInfoVo fileInfoVo = new FileInfoVo();
        fileInfoVo.setCreateTime(System.currentTimeMillis());
        fileInfoVo.setFileType("*image");
        fileInfoVo.setFileUrl(ConfigClass.IMG_PATH + "chat/" + userId + "/" + file.getOriginalFilename());
        return new ResponseEntity<>(ResultModel.ok(fileInfoVo), HttpStatus.OK);
    }


    /**
     * 发送语音接口
     */
    @ApiOperation(value = "发送语音", notes = "发送语音")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "query", dataType = "integer"),
    })
    @RequestMapping(value = "/sendMp4", method = RequestMethod.POST)
    @ResponseBody
    public Object sendMp4(
            @RequestParam(name = "userId") Integer userId,
            @ApiParam(value = "传输语音", required = true)
            @RequestParam(name = "file") MultipartFile file
    ) {
        if (userId == null)
            return new ResponseEntity<>(ResultModel.error("userId cannot be empty"), HttpStatus.OK); //验证userId是否为空
        if (file == null || file.isEmpty())
            return new ResponseEntity<>(ResultModel.error("file cannot be empty"), HttpStatus.OK); //验证File是否为空

        //查找数据库是否由此用户
        String fileName = ConfigClass.IMG_ABSOLUTE_PATH + "chat/" + userId + "/" + file.getOriginalFilename();

        //保存图片
        try {
            File dest = new File(fileName);
            if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest); //保存文件
        } catch (IllegalStateException | IOException e) {
            return new ResponseEntity<>(ResultModel.error(e.getMessage()), HttpStatus.OK); //验证账号是否存在
        }

        FileInfoVo fileInfoVo = new FileInfoVo();
        fileInfoVo.setCreateTime(System.currentTimeMillis());
        fileInfoVo.setFileType("*image");
        fileInfoVo.setFileUrl(ConfigClass.IMG_PATH + "chat/" + userId + "/" + file.getOriginalFilename());
        return new ResponseEntity<>(ResultModel.ok(fileInfoVo), HttpStatus.OK);
    }


}
