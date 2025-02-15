package com.fire.partnermatchdemo.service;

import com.fire.partnermatchdemo.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fire.partnermatchdemo.model.domain.User;

/**
* @author Admin
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-02-15 17:56:42
*/
public interface TeamService extends IService<Team> {


    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);


}
