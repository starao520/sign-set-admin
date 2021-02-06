package com.aisino.modules.system.mapper;

import com.aisino.modules.system.entity.DictDetail;
import com.aisino.modules.system.service.dto.DictDetailDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aisino.base.CommonMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author rxx
* @date 2020-09-24
*/
@Repository
public interface DictDetailMapper extends CommonMapper<DictDetail> {

    /**
     * 根据字典名称查询字典详情
     * @param dictName
     * @return
     */
    List<DictDetailDto> getDictDetailsByDictName(@Param("dictName") String dictName);

    /**
     * 根据字典名称分页查询字典详情
     * @param dictName
     * @param page
     * @return
     */
    IPage<DictDetailDto> getDictDetailsByDictName(@Param("dictName") String dictName, IPage<DictDetailDto> page);
}
