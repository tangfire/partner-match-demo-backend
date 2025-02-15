package com.fire.partnermatchdemo.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 */
@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = 6799299692899374463L;

    private String userAccount;

    private String userPassword;
}
