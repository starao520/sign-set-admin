package com.aisino.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aisino.modules.system.entity.Menu;
import com.aisino.modules.system.entity.User;
import com.aisino.modules.system.entity.vo.MenuMetaVo;
import com.aisino.modules.system.entity.vo.MenuTreeVo;
import com.aisino.modules.system.entity.vo.MenuVo;
import com.aisino.modules.system.service.MenuService;
import com.aisino.modules.system.service.RoleService;
import com.aisino.modules.system.service.RolesMenusService;
import com.aisino.modules.system.service.dto.MenuDtoBase;
import com.aisino.modules.system.service.dto.MenuQueryParam;
import com.aisino.modules.system.service.dto.RoleSmallDto;
import com.aisino.modules.system.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import com.aisino.base.QueryHelpMybatisPlus;
import com.aisino.base.impl.BaseServiceImpl;
import com.aisino.exception.BadRequestException;
import com.aisino.exception.EntityExistException;
import com.aisino.modules.system.mapper.MenuMapper;
import com.aisino.utils.ConvertUtil;
import com.aisino.utils.FileUtil;
import com.aisino.utils.RedisUtils;
import com.aisino.utils.ValidationUtil;
import com.aisino.utils.enums.MenuType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author rxx
* @date 2020-09-25
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl extends BaseServiceImpl<Menu> implements MenuService {

    private final RoleService roleService;
    private final UserMapper userMapper;
    private final MenuMapper menuMapper;
    private final RedisUtils redisUtils;
    private final RolesMenusService rolesMenusService;

    @Override
    public List<MenuDtoBase> queryAll(MenuQueryParam query, boolean isQuery) {
        if (isQuery) {
            query.setPidIsNull(true);
        }
        boolean notEmpty = null != query.getPid() || StrUtil.isNotEmpty(query.getBlurry())
                || CollectionUtils.isNotEmpty(query.getCreateTime());
        if (isQuery && notEmpty) {
            query.setPidIsNull(null);
        }
        QueryWrapper wrapper = QueryHelpMybatisPlus.getPredicate(query);
        wrapper.orderByAsc("menu_sort");
        return ConvertUtil.convertList(menuMapper.selectList(wrapper), MenuDtoBase.class);
    }

    @Override
    public List<MenuDtoBase> queryAll(MenuQueryParam query){
        return queryAll(query, true);
    }

    @Override
    public Menu getById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public MenuDtoBase findById(Long id) {
        return ConvertUtil.convert(getById(id), MenuDtoBase.class);
    }

    @Override
    public List<MenuDtoBase> findByUser(Long currentUserId) {
        List<RoleSmallDto> roles = roleService.findByUsersId(currentUserId);
        Set<Long> roleIds = roles.stream().map(RoleSmallDto::getId).collect(Collectors.toSet());

        LinkedHashSet<Menu> menus = new LinkedHashSet<>();
        if (!roleIds.isEmpty()){
            Set<Long> menuIds = new HashSet<>();
            for (Long roleId : roleIds){
                menuIds.addAll(rolesMenusService.queryMenuIdByRoleId(roleId));
            }
            if (!menuIds.isEmpty()){
                for (Long menuId : menuIds ){
                    menus.addAll(menuMapper.getFullMenuByMenuId(menuId));
                }
            }
        }
        List<MenuDtoBase> list = menus.stream().map(menu -> ConvertUtil.convert(menu, MenuDtoBase.class)).collect(Collectors.toList());
        //  排序
        Collections.sort(list, (t1, t2) -> {
            long diff = t1.getId() - t2.getId();
            if (diff > 0){
                return 1;
            } else if (diff < 0){
                return -1;
            }
            return 0;
        });
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Menu resources) {
        QueryWrapper<Menu> query = new QueryWrapper<Menu>();
        query.lambda().eq(Menu::getTitle, resources.getTitle());
        if (menuMapper.selectOne(query) != null) {
            throw new EntityExistException(Menu.class, "title", resources.getTitle());
        }
        if (StringUtils.isNotBlank(resources.getComponentName())) {
            QueryWrapper<Menu> query2 = new QueryWrapper<Menu>();
            query2.lambda().eq(Menu::getComponentName, resources.getComponentName());
            if (menuMapper.selectOne(query2) != null) {
                throw new EntityExistException(Menu.class, "组件名称", resources.getComponentName());
            }
        }
        if (resources.getIFrame()) {
            String http = "http://", https = "https://";
            if (!(resources.getPath().toLowerCase().startsWith(http)
                    || resources.getPath().toLowerCase().startsWith(https))) {
                throw new BadRequestException("外链必须以http://或者https://开头");
            }
        }
        resources.setSubCount(resources.getSubCount() == null ? 0 : resources.getSubCount());
        resources.setMenuSort(resources.getMenuSort() == null ? 999 : resources.getMenuSort());
        int ret = menuMapper.insert(resources);

        // 计算子节点数目
        if (resources.getPid() != null) {
            // 清理缓存
            updateSubCnt(resources.getPid());
        }
        redisUtils.del("menu::pid:" + (resources.getPid() == null ? 0 : resources.getPid()));
        List<String> keys = redisUtils.scan("menu::user:*");
        keys.forEach(item -> redisUtils.del(item));
        return ret > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Menu resources){
        if (resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        Menu menu = Optional.ofNullable(this.getById(resources.getId())).orElseGet(Menu::new);
        // 记录旧的父节点ID
        Long pid = menu.getPid();
        ValidationUtil.isNull(menu.getId(), "Permission", "id", resources.getId());

        if (resources.getIFrame()) {
            String http = "http://", https = "https://";
            if (!(resources.getPath().toLowerCase().startsWith(http)
                    || resources.getPath().toLowerCase().startsWith(https))) {
                throw new BadRequestException("外链必须以http://或者https://开头");
            }
        }
        QueryWrapper<Menu> query = new QueryWrapper<Menu>();
        query.lambda().eq(Menu::getTitle, resources.getTitle());
        Menu menu1 = menuMapper.selectOne(query);

        if (menu1 != null && !menu1.getId().equals(menu.getId())) {
            throw new EntityExistException(Menu.class, "name", resources.getTitle());
        }

        if (resources.getPid().equals(0L)) {
            resources.setPid(null);
        }

        if (StringUtils.isNotBlank(resources.getComponentName())) {
            QueryWrapper<Menu> query2 = new QueryWrapper<Menu>();
            query2.lambda().eq(Menu::getComponentName, resources.getComponentName());
            menu1 = menuMapper.selectOne(query2);
            if (menu1 != null && !menu1.getId().equals(menu.getId())) {
                throw new EntityExistException(Menu.class, "componentName", resources.getComponentName());
            }
        }

        // 记录的父节点ID
        Long oldPid = menu.getPid();
        Long newPid = resources.getPid();

        // 类型从菜单或按钮变更为目录，清空路径和权限
        if (menu.getType() != MenuType.FOLDER.getValue() && resources.getType() == MenuType.FOLDER.getValue()) {
            menu.setComponent(null);
            menu.setPermission(null);
            menu.setComponentName(null);
        } else {
            menu.setComponent(resources.getComponent());
            menu.setPermission(resources.getPermission());
            menu.setComponentName(resources.getComponentName());
        }
        menu.setTitle(resources.getTitle());
        menu.setPath(resources.getPath());
        menu.setIcon(resources.getIcon());
        menu.setIFrame(resources.getIFrame());
        menu.setPid(resources.getPid());
        menu.setMenuSort(resources.getMenuSort());
        menu.setCache(resources.getCache());
        menu.setHidden(resources.getHidden());
        menu.setType(resources.getType());
        int ret = menuMapper.updateById(menu);

        // 计算父级菜单节点数目
        updateSubCnt(oldPid);
        updateSubCnt(newPid);
        // 清理缓存
        delCaches(resources.getId(), pid);
        return ret > 0;
    }

    @Override
    public Set<Menu> getDeleteMenus(List<Menu> menuList, Set<Menu> menuSet) {
        // 递归找出待删除的菜单
        for (Menu menu1 : menuList) {
            menuSet.add(menu1);
            QueryWrapper<Menu> query = new QueryWrapper<Menu>();
            query.lambda().eq(Menu::getPid, menu1.getId());
            List<Menu> menus = menuMapper.selectList(query);
            if (menus != null && menus.size() != 0) {
                getDeleteMenus(menus, menuSet);
            }
        }
        return menuSet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Set<Long> ids){
        for (Long id: ids) {
            Menu menu = getById(id);
            delCaches(menu.getId(), menu.getPid());
            rolesMenusService.removeByMenuId(id);
            if (menu.getPid() != null) {
                updateSubCnt(menu.getPid());
            }
        }
        return menuMapper.deleteBatchIds(ids) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id){
        Set<Long> set = new HashSet<>(1);
        set.add(id);
        return this.removeByIds(set);
    }

    // @Override
    public Object getMenuTree(Long pid) {
        Map<Long, List<Menu>> allMap = menuMapper.selectList(Wrappers.emptyWrapper()).stream().collect(Collectors.groupingBy(Menu::getPid));
        return buildMenuTree(allMap, pid);
    }

    private Object buildMenuTree(Map<Long, List<Menu>> allMap, Long pid) {
        List<Map<String, Object>> list = new LinkedList<>();
        allMap.get(pid).forEach(menu -> {
            if (menu != null) {
                List<Menu> menuList = allMap.get(menu.getId());
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", menu.getId());
                map.put("label", menu.getTitle());
                if (menuList != null && menuList.size() != 0) {
                    map.put("children", getMenuTree(menu.getId()));
                }
                list.add(map);
            }
        });
        return list;
    }

    @Override
    public List<MenuDtoBase> getMenus(Long pid) {
        List<Menu> menus;
        if (pid != null && !pid.equals(0L)) {
            QueryWrapper<Menu> query = new QueryWrapper<Menu>();
            query.lambda().eq(Menu::getPid, pid).orderByAsc(Menu::getMenuSort);
            menus = menuMapper.selectList(query);
        } else {
            QueryWrapper<Menu> query = new QueryWrapper<Menu>();
            query.lambda().isNull(Menu::getPid).orderByAsc(Menu::getMenuSort);
            menus = menuMapper.selectList(query);
        }
        return ConvertUtil.convertList(menus, MenuDtoBase.class);
    }

    @Override
    public List<MenuDtoBase> getSuperior(MenuDtoBase menuDto, List<Menu> menus) {
        QueryWrapper<Menu> query = new QueryWrapper<Menu>();
        if (menuDto.getPid() == null) {
            query.lambda().isNull(Menu::getPid).orderByAsc(Menu::getMenuSort);
            menus.addAll(menuMapper.selectList(query));
            return ConvertUtil.convertList(menus, MenuDtoBase.class);
        }
        query.lambda().eq(Menu::getPid, menuDto.getPid()).orderByAsc(Menu::getMenuSort);
        menus.addAll(menuMapper.selectList(query));
        return getSuperior(findById(menuDto.getPid()), menus);
    }

    @Override
    public List<MenuDtoBase> buildTree(List<MenuDtoBase> menuDtos) {
        List<MenuDtoBase> trees = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (MenuDtoBase menuDTO : menuDtos) {
            if (null == menuDTO.getPid()) {
                trees.add(menuDTO);
            }
            for (MenuDtoBase it : menuDtos) {
                if (it.getPid() != null && it.getPid().equals(menuDTO.getId())) {
                    if (menuDTO.getChildren() == null) {
                        menuDTO.setChildren(new ArrayList<>());
                    }
                    menuDTO.getChildren().add(it);
                    ids.add(it.getId());
                }
            }
        }
        if (trees.size() == 0) {
            trees = menuDtos.stream().filter(s -> !ids.contains(s.getId())).collect(Collectors.toList());
        }
        return trees;
    }

    @Override
    public List<MenuVo> buildMenus(List<MenuDtoBase> menuDtos) {
        List<MenuVo> list = new LinkedList<>();
        menuDtos.forEach(menuDTO -> {
            if (menuDTO != null) {
                List<MenuDtoBase> menuDtoList = menuDTO.getChildren();
                MenuVo menuVo = new MenuVo();
                menuVo.setName(ObjectUtil.isNotEmpty(menuDTO.getComponentName()) ? menuDTO.getComponentName()
                        : menuDTO.getTitle());
                // 一级目录需要加斜杠，不然会报警告
                menuVo.setPath(menuDTO.getPid() == null ? "/" + menuDTO.getPath() : menuDTO.getPath());
                menuVo.setHidden(menuDTO.getHidden());
                // 如果不是外链
                if (!menuDTO.getIFrame()) {
                    if (menuDTO.getPid() == null) {
                        menuVo.setComponent(
                                StrUtil.isEmpty(menuDTO.getComponent()) ? "Layout" : menuDTO.getComponent());
                    } else if (!StrUtil.isEmpty(menuDTO.getComponent())) {
                        menuVo.setComponent(menuDTO.getComponent());
                    }
                }
                menuVo.setMeta(new MenuMetaVo(menuDTO.getTitle(), menuDTO.getIcon(), !menuDTO.getCache()));
                if (menuDtoList != null && menuDtoList.size() != 0) {
                    menuVo.setAlwaysShow(true);
                    menuVo.setRedirect("noredirect");
                    menuVo.setChildren(buildMenus(menuDtoList));
                    // 处理是一级菜单并且没有子菜单的情况
                } else if (menuDTO.getPid() == null) {
                    MenuVo menuVo1 = new MenuVo();
                    menuVo1.setMeta(menuVo.getMeta());
                    // 非外链
                    if (!menuDTO.getIFrame()) {
                        menuVo1.setPath("index");
                        menuVo1.setName(menuVo.getName());
                        menuVo1.setComponent(menuVo.getComponent());
                    } else {
                        menuVo1.setPath(menuDTO.getPath());
                    }
                    menuVo.setName(null);
                    menuVo.setMeta(null);
                    menuVo.setComponent("Layout");
                    List<MenuVo> list1 = new ArrayList<>();
                    list1.add(menuVo1);
                    menuVo.setChildren(list1);
                }
                list.add(menuVo);
            }
        });
        return list;
    }

    private void updateSubCnt(Long menuId) {
        QueryWrapper<Menu> query = new QueryWrapper<Menu>();
        query.lambda().eq(Menu::getPid, menuId);
        int count = menuMapper.selectCount(query);

        UpdateWrapper<Menu> update = new UpdateWrapper<Menu>();
        update.lambda().eq(Menu::getId, menuId);
        Menu menu = new Menu();
        menu.setSubCount(count);
        menuMapper.update(menu, update);
    }

    /**
     * 清理缓存
     *
     * @param id  菜单ID
     * @param pid 菜单父级ID
     */
    public void delCaches(Long id, Long pid) {
        List<User> users = userMapper.findByMenuId(id);
        redisUtils.del("menu::id:" + id);
        redisUtils.delByKeys("menu::user:", users.stream().map(User::getId).collect(Collectors.toSet()));
        redisUtils.del("menu::pid:" + (pid == null ? 0 : pid));
    }
    
    @Override
    public void download(List<MenuDtoBase> all, HttpServletResponse response) throws IOException {
      List<Map<String, Object>> list = new ArrayList<>();
      for (MenuDtoBase menu : all) {
        Map<String,Object> map = new LinkedHashMap<>();
              map.put("上级菜单ID", menu.getPid());
              map.put("子菜单数目", menu.getSubCount());
              map.put("菜单类型", menu.getType());
              map.put("菜单标题", menu.getTitle());
              map.put("组件名称", menu.getComponentName());
              map.put("组件", menu.getComponent());
              map.put("排序", menu.getMenuSort());
              map.put("图标", menu.getIcon());
              map.put("链接地址", menu.getPath());
              map.put("是否外链", menu.getIFrame());
              map.put("缓存", menu.getCache());
              map.put("隐藏", menu.getHidden());
              map.put("权限", menu.getPermission());
              map.put("创建者", menu.getCreateBy());
              map.put("更新者", menu.getUpdateBy());
              map.put("创建日期", menu.getCreateTime());
              map.put("更新时间", menu.getUpdateTime());
        list.add(map);
      }
      FileUtil.downloadExcel(list, response);
    }

    /**
     * 通过菜单ID获取所有父节点ID
     *
     * @param menuId
     * @return
     */
    @Override
    public List<Long> getFullPidByMenuId(Long menuId) {
        try {
            return menuMapper.getFullPidByMenuId(menuId);
        }catch (BadRequestException e){
            throw new BadRequestException("获取父节点失败");
        }
    }

    @Override
    public List<MenuTreeVo> getMenusTree() {
        List<MenuTreeVo> menuList = this.menuMapper.selectMenuTrees();

        List<MenuTreeVo> menuTreeVoList = new ArrayList<>();
        if (!menuList.isEmpty()) {
            menuList.forEach((menu -> {
                if (menu.getPid() == null){
                    //  顶级目录
                    menuTreeVoList.add(menu);
                }

                if (!menuTreeVoList.isEmpty()){
                    menuTreeVoList.forEach(menuTreeVo -> {
                        menuTreeVo.setChildren(getChildren(menuTreeVo.getId(), menuList));
                    });
                }
            }));
        }
        return menuTreeVoList;
    }

    /**
     * @param roleId
     * @return
     * @Author raoxingxing
     * @Description 查询角色的最下级菜单列表
     * @Date 2021/1/16 16:38
     * @Param [roleId]
     */
    @Override
    public List<MenuTreeVo> getLowerMenus(Long roleId) {
        List<MenuTreeVo> list = this.menuMapper.getLowerMenus(roleId);
        return list;
    }


    public List<MenuTreeVo> getChildren(Long id, List<MenuTreeVo> list){
        List<MenuTreeVo> children = new ArrayList<>();
        list.forEach(menuTreeVo -> {
            if (menuTreeVo.getPid() != null && menuTreeVo.getPid().equals(id)){
                children.add(menuTreeVo);
            }
        });

        //  递归
        if (!children.isEmpty()){
            children.forEach(menuTreeVo -> {
                list.forEach(menuTreeVo1 -> {
                    if (menuTreeVo.getId().equals(menuTreeVo1.getPid())){
                        menuTreeVo.setChildren(getChildren(menuTreeVo.getId(), list));
                    }
                });
            });
        } else {
            return null;
        }
        return children;
    }
}
