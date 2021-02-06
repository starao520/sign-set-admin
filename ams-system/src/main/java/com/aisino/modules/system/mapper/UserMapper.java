package com.aisino.modules.system.mapper;

import com.aisino.modules.system.entity.User;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.aisino.base.CommonMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
* @author rxx
* @date 2020-09-25
*/
@Repository
public interface UserMapper extends CommonMapper<User> {

    /**
     * 根据角色查询用户
     *
     * @param roleId /
     * @return /
     */
    List<User> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色中的部门查询
     *
     * @param roleId /
     * @return /
     */
    List<User> findByDeptRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单查询
     *
     * @param menuId 菜单ID
     * @return /
     */
    List<User> findByMenuId(@Param("menuId") Long menuId);

    /**
     * 根据部门查询
     *
     * @param deptIds /
     * @return /
     */
    int countByDepts(@Param("deptIds") Set<Long> deptIds);

    /**
     * 根据角色查询
     *
     * @return /
     */
    int countByRoles(@Param("ids") Set<Long> ids);

}
