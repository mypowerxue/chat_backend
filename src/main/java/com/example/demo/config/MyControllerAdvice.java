package com.example.demo.config;

import com.example.demo.controller.token.TokenException;
import com.example.demo.utils.ResultModel;
import com.example.demo.utils.ResultStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@ControllerAdvice  //不指定包默认加了@Controller和@RestController都能控制
public class MyControllerAdvice {

    /**
     *
     * 全局异常处理
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = TokenException.class)
    public ResultModel exceptionHandler(Exception ex) {
        return ResultModel.error(ResultStatus.TOKEN);
    }
}