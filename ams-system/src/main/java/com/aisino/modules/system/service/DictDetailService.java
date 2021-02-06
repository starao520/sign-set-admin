package com.aisino.modules.system.service;

import com.aisino.modules.system.entity.DictDetail;
import com.aisino.base.BaseService;
import com.aisino.base.PageInfo;
import com.aisino.modules.system.service.dto.DictDetailDto;
import com.aisino.modules.system.service.dto.DictDetailQueryParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
* @author rxx
* @date 2020-09-24
*/
public interface DictDetailService  extends BaseService<DictDetail> {

    /**
    * 查询数据分页
    * @param query 条件
    * @param pageable 分页参数
    * @return map[totalElements, content]
    */
    IPage<DictDetail> queryAll(DictDetailQueryParam query, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param query 条件参数
    * @return List<DictDetailDto>
    */
    List<DictDetailDto> queryAll(DictDetailQueryParam query);

    /**
     * 根据字典名称获取字典详情
     * @param name 字典名称
     * @return /
     */
    List<DictDetailDto> getDictByName(String name);

    IPage<DictDetailDto> getDictByName(String dictName, Pageable pageable);

    boolean removeById(Long id);
    boolean removeByDictId(Long id);

    boolean updateById(DictDetailDto resources);

    boolean save(DictDetailDto resources);

}
