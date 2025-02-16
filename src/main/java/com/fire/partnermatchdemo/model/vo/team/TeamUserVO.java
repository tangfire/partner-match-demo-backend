package com.fire.partnermatchdemo.model.vo.team;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fire.partnermatchdemo.model.domain.User;
import com.fire.partnermatchdemo.model.vo.user.UserVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍和用户信息封装类(脱敏)
 */
@Data
public class TeamUserVO implements Serializable {


    private static final long serialVersionUID = 8639398369308803168L;
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

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
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;


    /**
     * 创建人用户信息
     */
    UserVO createUser;


}
