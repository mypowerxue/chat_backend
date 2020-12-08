package com.example.demo.utils;

import com.example.demo.controller.vo.ResultVo;

public class ResultModel {

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回结果描述
     */
    private String message;
    /**
     * 返回数据
     */
    private Object data;


    public static ResultModel ok(Object data) {
        return new ResultModel(ResultStatus.SUCCESS, data);
    }

    public static ResultModel ok() {
        ResultVo resultVo = new ResultVo();
        resultVo.setResult(true);
        return new ResultModel(ResultStatus.SUCCESS, resultVo);
    }

    public static ResultModel error(ResultStatus error) {
        return new ResultModel(error);
    }

    public static ResultModel error(String errorMessage) {
        ResultStatus error1 = ResultStatus.ERROR;
        error1.setMessage(errorMessage);
        error1.setCode(500);
        return new ResultModel(error1, null);
    }

    public static ResultModel error(Object error) {
        return new ResultModel(ResultStatus.ERROR, error);
    }

    private ResultModel(ResultStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = null;
    }

    private ResultModel(ResultStatus status, Object data) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
