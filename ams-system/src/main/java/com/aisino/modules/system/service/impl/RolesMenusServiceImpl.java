package com.aisino.modules.system.service.impl;

import com.aisino.modules.system.entity.RolesMenus;
import com.aisino.modules.system.service.RolesMenusService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.AllArgsConstructor;
import com.aisino.base.impl.BaseServiceImpl;
import com.aisino.modules.system.mapper.RolesMenusMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jinjin on 2020-09-25.
 */
@AllArgsConstructor
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RolesMenusServiceImpl extends BaseServiceImpl<RolesMenus> implements RolesMenusService {

    private final RolesMenusMapper rolesMenusMapper;

    @Override
    public List<Long> queryMenuIdByRoleId(Long id) {
        QueryWrapper<RolesMenus> query = new QueryWrapper<>();
        query.lambda().eq(RolesMenus::getRoleId, id);
        return rolesMenusMapper.selectList(query).stream().map(RolesMenus::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> queryRoleIdByMenuId(Long id) {
        QueryWrapper<RolesMenus> query = new QueryWrapper<>();
        query.lambda().eq(RolesMenus::getMenuId, id);
        return rolesMenusMapper.selectList(query).stream().map(RolesMenus::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeByRoleId(Long id) {
        UpdateWrapper<RolesMenus> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(RolesMenus::getRoleId, id);
        return rolesMenusMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeByMenuId(Long id) {
        UpdateWrapper<RolesMenus> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(RolesMenus::getMenuId, id);
        return rolesMenusMapper.delete(wrapper);
    }

}
