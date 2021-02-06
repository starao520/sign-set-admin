package com.aisino.modules.system.service;

import com.aisino.modules.system.entity.UsersRoles;
import com.aisino.base.BaseService;

import java.util.List;

/**
* @author rxx
* @date 2020-09-25
*/
public interface UsersRolesService extends BaseService<UsersRoles> {
    List<Long> queryUserIdByRoleId(Long id);
    List<Long> queryRoleIdByUserId(Long id);
    int removeByRoleId(Long id);
    int removeByUserId(Long id);


}
