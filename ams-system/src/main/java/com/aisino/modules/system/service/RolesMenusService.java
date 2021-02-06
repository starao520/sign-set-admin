package com.aisino.modules.system.service;

import com.aisino.modules.system.entity.RolesMenus;
import com.aisino.base.BaseService;

import java.util.List;

/**
* @author rxx
* @date 2020-09-25
*/
public interface RolesMenusService extends BaseService<RolesMenus> {
    List<Long> queryMenuIdByRoleId(Long id);
    List<Long> queryRoleIdByMenuId(Long id);
    int removeByRoleId(Long id);
    int removeByMenuId(Long id);
}
