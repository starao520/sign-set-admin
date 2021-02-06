package com.aisino.modules.system.mapper;

import com.aisino.modules.system.entity.Menu;
import com.aisino.base.CommonMapper;
import com.aisino.modules.system.entity.vo.MenuTreeVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
* @author rxx
* @date 2020-09-25
*/
@Repository
public interface MenuMapper extends CommonMapper<Menu> {

    /**
     * 根据角色ID查询菜单
     * @param roleId
     * @return
     */
    Set<Menu> selectLink(@Param("roleId") Long roleId);

    /**
     * 通过多个角色ID获取菜单
     * @param roleIds
     * @return
     */
    Set<Menu> selectByRoleIds(@Param("roleIds") Set<Long> roleIds);

    /**
     * 查询父节点
     * @param menuId
     * @return
     */
    List<Long> getFullPidByMenuId(@Param("menuId") Long menuId);

    /**
     * @Author raoxingxing
     * @Description 查询某个菜单的所有上级菜单
     * @Date 2021/1/16 22:50
     * @Param [menuId]
     * @return
    **/
    List<Menu> getFullMenuByMenuId(@Param("menuId") Long menuId);

    /**
     * 简化菜单树
     * @return
     */
    List<MenuTreeVo> selectMenuTrees();

    /**
     * @Author raoxingxing
     * @Description 查询当前角色最下级菜单列表
     * @Date 2021/1/16 16:58
     * @Param [roleId]
     * @return
    **/
    List<MenuTreeVo> getLowerMenus(@Param("roleId") Long roleId);
}
