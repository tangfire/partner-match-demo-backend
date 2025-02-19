package com.fire.partnermatchdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fire.partnermatchdemo.common.BaseResponse;
import com.fire.partnermatchdemo.common.DeleteRequest;
import com.fire.partnermatchdemo.common.ErrorCode;
import com.fire.partnermatchdemo.common.ResultUtils;
import com.fire.partnermatchdemo.constant.UserConstant;
import com.fire.partnermatchdemo.exception.BusinessException;
import com.fire.partnermatchdemo.model.domain.Team;
import com.fire.partnermatchdemo.model.domain.User;
import com.fire.partnermatchdemo.model.domain.UserTeam;
import com.fire.partnermatchdemo.model.dto.team.*;
import com.fire.partnermatchdemo.model.vo.team.TeamUserVO;
import com.fire.partnermatchdemo.service.TeamService;
import com.fire.partnermatchdemo.service.UserService;
import com.fire.partnermatchdemo.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Resource
    UserTeamService userTeamService;

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
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = deleteRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);


        boolean res = teamService.deleteTeam(id,loginUser);

        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");

        }

        return ResultUtils.success(true);


    }


//    @PostMapping("/update")
//    public BaseResponse<Boolean> updateTeam(@RequestBody Team team) {
//
//        if (team == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//
//        boolean res = teamService.updateById(team);
//
//        if (!res) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
//        }
//
//        return ResultUtils.success(true);
//
//    }


    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {

        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);

        boolean res = teamService.updateTeam(teamUpdateRequest,loginUser);

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

//    @GetMapping("/list")
//    public BaseResponse<List<Team>> getTeamList(TeamQueryRequest teamQueryRequest) {
//
//        if (teamQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Team team = new Team();
//
//        BeanUtils.copyProperties(teamQueryRequest, team);
//
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//        List<Team> teamList = teamService.list(queryWrapper);
//
//        return ResultUtils.success(teamList);
//
//
//    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> getTeamList(TeamQueryRequest teamQueryRequest,HttpServletRequest request) {

        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        boolean isAdmin = userService.isAdmin(request);


        // 1. 查询队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQueryRequest,isAdmin);

        // 队伍id列表
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());

        // 2. 判断当前用户是否已加入队伍


        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();


        try{
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("userId",loginUser.getId());
            userTeamQueryWrapper.in("teamId",teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的队伍id集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team ->{
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        }catch (Exception e){

        }

        // 3. 查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        // 加入这个队伍的用户列表
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team ->{
            team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(),new ArrayList<>()).size());
        });

        return ResultUtils.success(teamList);


    }


    // todo 查询分页
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

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = teamService.joinTeam(teamJoinRequest,loginUser);
        return ResultUtils.success(res);
    }


    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if(teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        boolean res = teamService.quitTeam(teamQuitRequest,loginUser);
        return ResultUtils.success(res);
    }


    /**
     * 获取我创建的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQueryRequest teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User logininUser = userService.getLoginUser(request);
        teamQuery.setUserId(logininUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(teamList);
    }


    /**
     *  获取我加入的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQueryRequest teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User logininUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",logininUser.getId());
        List<UserTeam> userTeamlist = userTeamService.list(queryWrapper);
        // 取出不重复的队伍 id
        //teamId userId
        //1,2
        //1,3
        //2,3
        //result
        //1=> 2,3
        //2=> 3
        Map<Long, List<UserTeam>> listMap = userTeamlist.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        if (idList.isEmpty()){
            teamQuery.setUserId(logininUser.getId());
        }

        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(teamList);
    }


}
