package com.fire.partnermatchdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fire.partnermatchdemo.common.BaseResponse;
import com.fire.partnermatchdemo.common.ErrorCode;
import com.fire.partnermatchdemo.common.ResultUtils;
import com.fire.partnermatchdemo.constant.UserConstant;
import com.fire.partnermatchdemo.exception.BusinessException;
import com.fire.partnermatchdemo.model.domain.User;
import com.fire.partnermatchdemo.model.dto.user.UserLoginRequest;
import com.fire.partnermatchdemo.model.dto.user.UserRegisterRequest;
import com.fire.partnermatchdemo.model.vo.user.UserVO;
import com.fire.partnermatchdemo.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.fire.partnermatchdemo.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @author tangfire
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {


    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }

        long result =  userService.userRegister(userAccount, userPassword, checkPassword);

//        return new BaseResponse<>(0,result,"ok");
        return ResultUtils.success(result);
    }

    /**
     * 登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }

        User user = userService.userLogin(userAccount, userPassword, request);

//        return new BaseResponse<>(0,user,"ok");
        return ResultUtils.success(user);
    }


    /**
     * 查询用户
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {

        if (!userService.isAdmin(request)){
//            return ResultUtils.error(ErrorCode.NO_AUTH);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }

        List<User> userList = userService.list(queryWrapper);
        List<User> list =  userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());

        return ResultUtils.success(list);
    }

    /**
     * 删除用户
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request) {

        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);

        }

        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }

        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }


    // todo 推荐多个,未实现
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(int pageSize,long pageNum, HttpServletRequest request) {


        User loginUser = userService.getLoginUser(request);

        String redisKey = String.format("partnermatch:user:recommend:%s",loginUser);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        Page<User> userPage = (Page<User>)valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }



        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        List<User> userList = userService.list(queryWrapper);


        /**
         * 使用分页功能需要开启拦截器,MybatisPlusConfig
         * 引入依赖
         * mybatis-plus-jsqlparser-4.9
         */
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);

        // 写缓存
        // 这里不抛异常,redis写失败,也可以把数据库查询出来的值交给前端
        try {
            valueOperations.set(redisKey,userPage,100, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set key error",e);
        }

//        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(userPage);


    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);

        }

        long userId = currentUser.getId();
        // todo 校验用户是否合法
        User user = userService.getById(userId);

        User safetyUser =  userService.getSafetyUser(user);

        return ResultUtils.success(safetyUser);




    }


    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }


    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request) {
        if (user == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        User loginUser = userService.getLoginUser(request);


        int result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);
    }


    /**
     * 获取最匹配的用户
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request){
        if (num <= 0 || num > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = userService.getLoginUser(request);

        return ResultUtils.success(userService.matchUsers(num,user));

    }




}
