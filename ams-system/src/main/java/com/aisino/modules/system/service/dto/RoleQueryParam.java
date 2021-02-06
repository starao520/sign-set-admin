package com.aisino.modules.system.service.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import com.aisino.annotation.Query;
import org.springframework.format.annotation.DateTimeFormat;

/**
* @author rxx
* @date 2020-09-25
*/
@Data
public class RoleQueryParam{

    @Query(blurry = "name,description")
    private String blurry;

    /** 精确 */
    @Query
    private Long roleId;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    /** BETWEEN */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Query(type = Query.Type.BETWEEN)
    private List<Date> createTime;
}
