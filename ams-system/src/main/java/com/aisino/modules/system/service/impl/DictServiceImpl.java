package com.aisino.modules.system.service.impl;

import com.aisino.modules.system.entity.Dict;
import com.aisino.modules.system.service.DictDetailService;
import com.aisino.modules.system.service.DictService;
import com.aisino.modules.system.service.dto.DictDtoBase;
import com.aisino.modules.system.service.dto.DictQueryParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import com.aisino.base.QueryHelpMybatisPlus;
import com.aisino.base.impl.BaseServiceImpl;
import com.aisino.utils.ConvertUtil;
import com.aisino.utils.FileUtil;
import com.aisino.modules.system.mapper.DictMapper;
import com.aisino.utils.PageUtil;
import com.aisino.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
* @author rxx
* @date 2020-09-24
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DictServiceImpl extends BaseServiceImpl<Dict> implements DictService {

    public static final String CACHE_KEY = "${changeClassName}";
    private final RedisUtils redisUtils;
    private final DictMapper dictMapper;
    private final DictDetailService detailService;

    @Override
    public IPage<Dict> queryAll(DictQueryParam query, Pageable pageable) {
        IPage<Dict> page = PageUtil.toMybatisPage(pageable);
        IPage<Dict> pageList = dictMapper.selectPage(page, QueryHelpMybatisPlus.getPredicate(query));
        return pageList;
    }

    @Override
    public List<DictDtoBase> queryAll(DictQueryParam query){
        return ConvertUtil.convertList(dictMapper.selectList(QueryHelpMybatisPlus.getPredicate(query)), DictDtoBase.class);
    }

    @Override
    public Dict getById(Long id) {
        return dictMapper.selectById(id);
    }

    @Override
    public DictDtoBase findById(Long id) {
        return ConvertUtil.convert(getById(id), DictDtoBase.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Dict resources) {
        return dictMapper.insert(resources) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Dict resources){
        return dictMapper.updateById(resources) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Set<Long> ids){
        List<Dict> dicts = dictMapper.selectBatchIds(ids);
        boolean ret = dictMapper.deleteBatchIds(ids) > 0;
        for (Dict dict : dicts) {
            detailService.removeByDictId(dict.getId());
            delCaches(dict);
        }
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id){
        Set<Long> ids = new HashSet<>(1);
        ids.add(id);
        return removeByIds(ids);
    }
    @Override
    public void download(List<DictDtoBase> all, HttpServletResponse response) throws IOException {
      List<Map<String, Object>> list = new ArrayList<>();
      for (DictDtoBase dict : all) {
        Map<String,Object> map = new LinkedHashMap<>();
              map.put("字典名称", dict.getName());
              map.put("描述", dict.getDescription());
              map.put("创建者", dict.getCreateBy());
              map.put("更新者", dict.getUpdateBy());
              map.put("创建日期", dict.getCreateTime());
              map.put("更新时间", dict.getUpdateTime());
        list.add(map);
      }
      FileUtil.downloadExcel(list, response);
    }

    private void delCaches(Dict dict){
        redisUtils.del("dictDetail::name:" + dict.getName());
        redisUtils.del("dictDetail::dictId:" + dict.getId());
        redisUtils.del("dict::id:" + dict.getId());
    }
}