package com.fire.partnermatchdemo.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author tangfire
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -3070720153972725543L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

}
