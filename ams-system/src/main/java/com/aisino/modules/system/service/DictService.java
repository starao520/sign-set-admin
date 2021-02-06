package com.aisino.modules.system.service;

import com.aisino.modules.system.entity.Dict;
import com.aisino.modules.system.service.dto.DictDtoBase;
import com.aisino.modules.system.service.dto.DictQueryParam;
import com.aisino.base.BaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.io.IOException;
import java.util.Set;

/**
* @author rxx
* @date 2020-09-24
*/
public interface DictService  extends BaseService<Dict> {

    /**
    * 查询数据分页
    * @param query 条件
    * @param pageable 分页参数
    * @return map[totalElements, content]
    */
    IPage<Dict> queryAll(DictQueryParam query, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param query 条件参数
    * @return List<DictDtoBase>
    */
    List<DictDtoBase> queryAll(DictQueryParam query);

    Dict getById(Long id);
    DictDtoBase findById(Long id);
    boolean save(Dict resources);
    boolean updateById(Dict resources);
    boolean removeById(Long id);
    boolean removeByIds(Set<Long> ids);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<DictDtoBase> all, HttpServletResponse response) throws IOException;
}
