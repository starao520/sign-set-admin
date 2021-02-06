package com.aisino.modules.system.service;

import com.aisino.modules.system.entity.Menu;
import com.aisino.base.BaseService;
import com.aisino.modules.system.entity.vo.MenuTreeVo;
import com.aisino.modules.system.service.dto.MenuDtoBase;
import com.aisino.modules.system.service.dto.MenuQueryParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
* @author rxx
* @date 2020-09-25
*/
public interface MenuService  extends BaseService<Menu> {

    /**
    * 查询数据分页
    * @param query 条件
    * @return PageInfo<MenuDtoBase>
    */
    List<MenuDtoBase> queryAll(MenuQueryParam query, boolean isQuery);

    /**
    * 查询所有数据不分页
    * @param query 条件参数
    * @return List<MenuDtoBase>
    */
    List<MenuDtoBase> queryAll(MenuQueryParam query);

    Menu getById(Long id);
    MenuDtoBase findById(Long id);

    /**
     * 插入一条新数据。
     */
    boolean save(Menu resources);
    boolean updateById(Menu resources);
    boolean removeById(Long id);
    boolean removeByIds(Set<Long> ids);

    /**
     * 获取待删除的菜单
     * @param menuList /
     * @param menuSet /
     * @return /
     */
    Set<Menu> getDeleteMenus(List<Menu> menuList, Set<Menu> menuSet);

    /**
     * 构建菜单树
     * @param menuDtos 原始数据
     * @return /
     */
    List<MenuDtoBase> buildTree(List<MenuDtoBase> menuDtos);

    /**
     * 构建菜单树
     * @param menuDtos /
     * @return /
     */
    Object buildMenus(List<MenuDtoBase> menuDtos);

    /**
     * 懒加载菜单数据
     * @param pid /
     * @return /
     */
    List<MenuDtoBase> getMenus(Long pid);

    /**
     * 根据ID获取同级与上级数据
     * @param menuDto /
     * @param objects /
     * @return /
     */
    List<MenuDtoBase> getSuperior(MenuDtoBase menuDto, List<Menu> objects);

    /**
     * 根据当前用户获取菜单
     * @param currentUserId /
     * @return /
     */
    List<MenuDtoBase> findByUser(Long currentUserId);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MenuDtoBase> all, HttpServletResponse response) throws IOException;

    /**
     * 通过菜单ID获取所有父节点ID
     * @param menuId
     * @return
     */
    List<Long> getFullPidByMenuId(Long menuId);

    /**
     * 菜单树
     * @return
     */
    List<MenuTreeVo> getMenusTree();

    /**
     * @Author raoxingxing
     * @Description 查询角色的最下级菜单列表
     * @Date 2021/1/16 16:38
     * @Param [roleId]
     * @return 
    **/
    List<MenuTreeVo> getLowerMenus(Long roleId);
}
