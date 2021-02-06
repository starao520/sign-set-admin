package com.aisino.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;

/**
* @author rxx
* @date 2020-09-27
*/
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class QiniuContentDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String bucket;

    private String name;

    private String realName;

    private String size;

    private String type;

    private String url;

    private String suffix;

    private Integer praiseCount;

    private Integer collectCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String userName;

    private String nickName;

    private String avatarPath;
}
