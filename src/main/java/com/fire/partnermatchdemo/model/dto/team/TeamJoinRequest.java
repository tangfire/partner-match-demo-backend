package com.fire.partnermatchdemo.model.dto.team;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamJoinRequest implements Serializable {


    private static final long serialVersionUID = -6959107611715036790L;

    /**
     * teamId
     */
    private Long teamId;


    /**
     * 密码
     */
    private String password;


}
