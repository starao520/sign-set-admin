package com.aisino.modules.system.service.impl;

import com.aisino.modules.system.entity.Dict;
import com.aisino.modules.system.entity.DictDetail;
import com.aisino.modules.system.service.DictDetailService;
import com.aisino.modules.system.service.dto.DictDetailDto;
import com.aisino.modules.system.service.dto.DictDetailQueryParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import com.aisino.base.PageInfo;
import com.aisino.base.QueryHelpMybatisPlus;
import com.aisino.base.impl.BaseServiceImpl;
import com.aisino.modules.system.mapper.DictMapper;
import com.aisino.utils.ConvertUtil;
import com.aisino.modules.system.mapper.DictDetailMapper;
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
import java.util.List;

/**
* @author rxx
* @date 2020-09-24
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DictDetailServiceImpl extends BaseServiceImpl<DictDetail> implements DictDetailService {

    private final DictDetailMapper dictDetailMapper;
    private final DictMapper dictMapper;
    private final RedisUtils redisUtils;

    @Override
    public IPage<DictDetail> queryAll(DictDetailQueryParam query, Pageable pageable) {
        IPage<DictDetail> page = PageUtil.toMybatisPage(pageable);
        IPage<DictDetail> pageList = dictDetailMapper.selectPage(page, QueryHelpMybatisPlus.getPredicate(query));
        return pageList;
//        return ConvertUtil.convertPage(pageList, DictDetailDto.class);
    }

    @Override
    public List<DictDetailDto> queryAll(DictDetailQueryParam query){
        return ConvertUtil.convertList(dictDetailMapper.selectList(QueryHelpMybatisPlus.getPredicate(query)), DictDetailDto.class);
    }

    @Override
    public List<DictDetailDto> getDictByName(String dictName) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Dict::getName, dictName);
        Dict dict = dictMapper.selectOne(wrapper);
        List<DictDetailDto> ret = dictDetailMapper.getDictDetailsByDictName(dictName);
        redisUtils.set("dictDetail::dictId:"+dict.getId(), ret);
        return ret;
    }

    @Override
    public IPage<DictDetailDto> getDictByName(String dictName, Pageable pageable) {
        IPage<DictDetailDto> page = PageUtil.toMybatisPage(pageable, true);
        return dictDetailMapper.getDictDetailsByDictName(dictName, page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(DictDetailDto resources) {
        DictDetail detail = ConvertUtil.convert(resources, DictDetail.class);
        boolean ret = dictDetailMapper.updateById(detail) > 0;
        // 清理缓存
        delCaches(detail.getDictId());
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(DictDetailDto resources){
        DictDetail detail = ConvertUtil.convert(resources, DictDetail.class);
        detail.setDictId(resources.getDict().getId());
        boolean ret = dictDetailMapper.insert(detail) > 0;
        // 清理缓存
        delCaches(detail.getDictId());
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id) {
        DictDetail dictDetail = dictDetailMapper.selectById(id);
        boolean ret = dictDetailMapper.deleteById(id) > 0;
        // 清理缓存
        delCaches(dictDetail.getDictId());
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByDictId(Long id) {
        UpdateWrapper<DictDetail> wrapper1 = new UpdateWrapper<>();
        wrapper1.lambda().eq(DictDetail::getDictId, id);
        boolean ret = dictDetailMapper.delete(wrapper1) > 0;
        delCaches(id);
        return ret;
    }

    private void delCaches(Long dictId){
        redisUtils.del("dictDetail::dictId:" + dictId);
    }
}
