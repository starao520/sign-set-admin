package com.aisino.modules.system.mapper;

import com.aisino.modules.system.entity.Role;
import com.aisino.base.CommonMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
* @author rxx
* @date 2020-09-25
*/
@Repository
public interface RoleMapper extends CommonMapper<Role> {

    /**
     * 根据用户ID查询
     * @param userId
     * @return
     */
    Set<Role> selectLink(@Param("userId") Long userId);

    /**
     * 根据用户ID查询
     *
     * @param id 用户ID
     * @return /
     */
//    @Select("SELECT r.* FROM sys_role r, sys_users_roles u WHERE " + "r.role_id = u.role_id AND u.user_id = #{id}")
//    Set<Role> findByUserId(@Param("id") Long id);

    /**
     * 根据部门查询
     *
     * @param deptIds /
     * @return /
     */
    int countByDepts(@Param("deptIds") Set<Long> deptIds);

    /**
     * 根据菜单Id查询
     * @param menuIds /
     * @return /
     */
    List<Role> findInMenuId(@Param("menuIds") List<Long> menuIds);

}
