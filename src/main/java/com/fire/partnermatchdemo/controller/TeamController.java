package com.fire.partnermatchdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fire.partnermatchdemo.common.BaseResponse;
import com.fire.partnermatchdemo.common.ErrorCode;
import com.fire.partnermatchdemo.common.ResultUtils;
import com.fire.partnermatchdemo.exception.BusinessException;
import com.fire.partnermatchdemo.model.domain.Team;
import com.fire.partnermatchdemo.model.domain.User;
import com.fire.partnermatchdemo.model.dto.team.TeamAddRequest;
import com.fire.partnermatchdemo.model.dto.team.TeamQueryRequest;
import com.fire.partnermatchdemo.service.TeamService;
import com.fire.partnermatchdemo.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 队伍接口
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);

        Team team = new Team();

        BeanUtils.copyProperties(teamAddRequest, team);


        long teamId = teamService.addTeam(team, loginUser);


        return ResultUtils.success(teamId);


    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean res = teamService.removeById(id);

        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");

        }

        return ResultUtils.success(true);


    }


    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team) {

        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean res = teamService.updateById(team);

        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }

        return ResultUtils.success(true);

    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(@RequestParam("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Team team = teamService.getById(id);

        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        return ResultUtils.success(team);

    }

    @GetMapping("/list")
    public BaseResponse<List<Team>> getTeamList(TeamQueryRequest teamQueryRequest) {

        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();

        BeanUtils.copyProperties(teamQueryRequest, team);

        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        List<Team> teamList = teamService.list(queryWrapper);

        return ResultUtils.success(teamList);


    }


    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamListByPage(TeamQueryRequest teamQueryRequest) {

        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();

        BeanUtils.copyProperties(teamQueryRequest, team);


        Page<Team> page = new Page<>(teamQueryRequest.getPageNum(), teamQueryRequest.getPageSize());

        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Page<Team> res = teamService.page(page, queryWrapper);


        return ResultUtils.success(res);


    }


}
