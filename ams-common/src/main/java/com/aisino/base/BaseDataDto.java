package com.aisino.base;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 浅析VO、DTO、DO、PO的概念、区别和用处
 * https://www.cnblogs.com/qixuejia/p/4390086.html
 *
 *
 * @author rxx
 * @date 2020-09-22
 */
@Getter
@Setter
public abstract class BaseDataDto extends BaseDto{

    private String createBy;

    private String updateBy;

    private Date createTime;

    private Date updateTime;

}
