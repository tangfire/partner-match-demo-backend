package com.fire.partnermatchdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fire.partnermatchdemo.common.ErrorCode;
import com.fire.partnermatchdemo.exception.BusinessException;
import com.fire.partnermatchdemo.model.domain.Team;
import com.fire.partnermatchdemo.model.domain.User;
import com.fire.partnermatchdemo.model.domain.UserTeam;
import com.fire.partnermatchdemo.model.enums.TeamStatusEnum;
import com.fire.partnermatchdemo.service.TeamService;
import com.fire.partnermatchdemo.mapper.TeamMapper;
import com.fire.partnermatchdemo.service.UserTeamService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * @author Admin
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2025-02-15 17:56:42
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {


    @Resource
    private UserTeamService userTeamService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 1. 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 是否登录,未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        final long userId = loginUser.getId();

        // 3. 校验信息
        Integer maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }

        String description = team.getDescription();

        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }

        // status 是否公开(int) 不传默认为0 (公开)
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getTeamStatusEnum(status);
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }

        String password = team.getPassword();

        if (TeamStatusEnum.SECRET.equals(teamStatusEnum) && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不正确");
        }

        Date expireTime = team.getExpireTime();

        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间 > 当前师姐");
        }

        // todo 有bug,用户疯狂点击的时候,可能创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("userId", userId);

        long hasTeamNum = this.count(queryWrapper);

        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多创建5个队伍");
        }

        team.setId(null);
        team.setUserId(userId);
        boolean res = this.save(team);
        Long teamId = team.getId();

        if(!res || teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }


        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());

        res = userTeamService.save(userTeam);

        if (!res){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }

        return teamId;


    }
}




