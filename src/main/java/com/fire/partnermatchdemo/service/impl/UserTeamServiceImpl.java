package com.fire.partnermatchdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fire.partnermatchdemo.model.domain.UserTeam;
import com.fire.partnermatchdemo.service.UserTeamService;
import com.fire.partnermatchdemo.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Admin
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2025-02-15 18:02:33
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




