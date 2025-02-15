package com.fire.partnermatchdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fire.partnermatchdemo.constant.UserConstant;
import com.fire.partnermatchdemo.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import static com.fire.partnermatchdemo.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Admin
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-02-05 21:32:58
*/
public interface UserService extends IService<User> {



    /**
     * 用户注释
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);


    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user,User loginUser);


    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);


    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);



}
