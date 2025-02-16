package com.fire.partnermatchdemo.service;

import com.fire.partnermatchdemo.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fire.partnermatchdemo.model.domain.User;
import com.fire.partnermatchdemo.model.dto.team.TeamJoinRequest;
import com.fire.partnermatchdemo.model.dto.team.TeamQueryRequest;
import com.fire.partnermatchdemo.model.dto.team.TeamQuitRequest;
import com.fire.partnermatchdemo.model.dto.team.TeamUpdateRequest;
import com.fire.partnermatchdemo.model.vo.team.TeamUserVO;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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


    /**
     * 搜索队伍
     * @param teamQueryRequest
     * @return
     */
    List<TeamUserVO> listTeams(TeamQueryRequest teamQueryRequest,boolean isAdmin);


    /**
     * 更新队伍
     * @param team
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest team, User loginUser);


    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);
}
