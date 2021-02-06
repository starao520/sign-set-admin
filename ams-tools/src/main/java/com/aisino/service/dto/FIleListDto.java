package com.aisino.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FIleListDto
 * @Author raoxingxing
 * @Date 2021/2/4 14:27
 **/
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FIleListDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String url;

    private Integer praiseCount;

    private Integer collectCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String userName;

    private String nickName;

    private String avatarPath;

    private Integer isPraise = 0;

    private Integer isCollect = 0;
}
