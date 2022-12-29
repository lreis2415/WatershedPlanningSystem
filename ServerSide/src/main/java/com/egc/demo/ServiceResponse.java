package com.egc.demo;

import com.alibaba.fastjson.annotation.JSONField;

public class ServiceResponse{
    @JSONField
    private int resultCode;
    @JSONField
    private String message;

    public ServiceResponse(){}

    public ServiceResponse(int resultCode,String message){
        super();
        this.resultCode = resultCode;
        this.message = message;
    }

    public void setResultCode(int code){
        this.resultCode = code;
    }
    public int getResultCode(){
        return this.resultCode;
    }

    public void setMessage(String message){
        this.message = message;
    }
    
    public String getMessage(){
        return this.message;
    }
}