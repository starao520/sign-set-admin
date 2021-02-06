package com.aisino.mapper;

import com.aisino.base.CommonMapper;
import com.aisino.entity.QiniuConfig;
import org.springframework.stereotype.Repository;

/**
* @author rxx
* @date 2020-09-27
*/
@Repository
public interface QiniuConfigMapper extends CommonMapper<QiniuConfig> {

    int updateType(String type);
}
