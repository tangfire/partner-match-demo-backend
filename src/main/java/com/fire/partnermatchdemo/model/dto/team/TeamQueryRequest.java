package com.fire.partnermatchdemo.model.dto.team;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fire.partnermatchdemo.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 队伍查询封装类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQueryRequest extends PageRequest {

    /**
     * id
     */
    private Long id;


    /**
     * 搜索关键词(同时对队伍名称和描述搜索)
     */
    private String searchText;



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
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;


}
