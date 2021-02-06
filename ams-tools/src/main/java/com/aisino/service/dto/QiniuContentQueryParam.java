package com.aisino.service.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Date;
import com.aisino.annotation.Query;
import org.springframework.format.annotation.DateTimeFormat;

/**
* @author rxx
* @date 2020-09-27
*/
@Getter
@Setter
public class QiniuContentQueryParam{

    @Query(type = Query.Type.INNER_LIKE)
    private String key;

    /** BETWEEN */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Query(type = Query.Type.BETWEEN)
    private List<Date> createTime;
}
