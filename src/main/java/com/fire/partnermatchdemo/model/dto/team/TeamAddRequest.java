package com.fire.partnermatchdemo.model.dto.team;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamAddRequest implements Serializable {


    private static final long serialVersionUID = 8384250494583254754L;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 描述
     */
    private String description;



    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;




}
