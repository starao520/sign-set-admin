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
public class LogQueryParam{

    @Query(blurry = "username,description,address,requestIp,method,params")
    private String blurry;

    /** 精确 */
    @Query
    private String logType;

    /** BETWEEN */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Query(type = Query.Type.BETWEEN)
    private List<Date> createTime;
}
