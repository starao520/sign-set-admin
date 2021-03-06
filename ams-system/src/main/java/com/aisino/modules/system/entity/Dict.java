package com.aisino.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;
import com.aisino.base.BaseDataEntity;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @author rxx
* @date 2020-09-24
*/
@Getter
@Setter
@TableName("sys_dict")
public class Dict extends BaseDataEntity implements Serializable {

    @ApiModelProperty(value = "ID")
    @TableId(value = "dict_id", type= IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "字典名称")
    @NotBlank
    private String name;

    @ApiModelProperty(value = "描述")
    private String description;

    public void copyFrom(Dict source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
