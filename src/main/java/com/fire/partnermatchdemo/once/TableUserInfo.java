package com.fire.partnermatchdemo.once;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author: shayu
 * @date: 2022/11/20
 * @ClassName: yupao-backend01
 * @Description:    星球表格用户信息
 */
@Data
public class TableUserInfo {
    /**
     * id
     */
    @ExcelProperty("成员编号")
    private String id;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;

}