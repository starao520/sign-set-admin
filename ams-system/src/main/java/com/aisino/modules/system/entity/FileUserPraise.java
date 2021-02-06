package com.aisino.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName FileUserPraise
 * @Author raoxingxing
 * @Date 2021/2/3 14:38
 **/
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("file_user_praise")
public class FileUserPraise implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("文件ID")
    private Long fileId;
}
