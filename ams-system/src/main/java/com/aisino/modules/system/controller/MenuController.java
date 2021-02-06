/*
 *  Copyright 2019-2020 rxx
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aisino.modules.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.aisino.modules.system.service.dto.MenuDtoBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import com.aisino.annotation.Log;
import com.aisino.modules.system.entity.Menu;
import com.aisino.exception.BadRequestException;
import com.aisino.modules.system.service.MenuService;
import com.aisino.modules.system.service.dto.MenuQueryParam;
import com.aisino.modules.system.mapper.MenuMapper;
import com.aisino.utils.ConvertUtil;
import com.aisino.utils.PageUtil;
import com.aisino.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author rxx
 * @date 2018-12-03
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "系统：菜单管理")
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;
    private final MenuMapper menuMapper;
    private static final String ENTITY_NAME = "menu";

    @Log("查询菜单")
    @ApiOperation("查询菜单")
    @GetMapping
    @PreAuthorize("@ams.check('menu:list')")
    public ResponseEntity<Object> query(MenuQueryParam criteria) throws Exception {
        List<MenuDtoBase> menuDtoList = menuService.queryAll(criteria, true);
        return new ResponseEntity<>(PageUtil.toPage(menuDtoList, menuDtoList.size()),HttpStatus.OK);
    }

    @ApiOperation("返回全部的菜单")
    @GetMapping(value = "/lazy")
    @PreAuthorize("@ams.check('menu:list','roles:list')")
    public ResponseEntity<Object> query(@RequestParam Long pid){
        return new ResponseEntity<>(menuService.getMenus(pid),HttpStatus.OK);
    }

    /**
     * @Author raoxingxing
     * @Description 查询简单的菜单树
     * @Date 2021/1/16 16:36
     * @Param []
     * @return
    **/
    @GetMapping("/tree")
    public ResponseEntity<Object> getMenusTree(){
        return new ResponseEntity<>(menuService.getMenusTree(), HttpStatus.OK);
    }

    /**
     * @Author raoxingxing
     * @Description 查询角色的最下级菜单列表
     * @Date 2021/1/16 16:37
     * @Param [roleId]
     * @return
    **/
    @GetMapping("/lower")
    public ResponseEntity<Object> getLowerMenus(Long roleId){
        return new ResponseEntity<>(menuService.getLowerMenus(roleId), HttpStatus.OK);
    }

    @GetMapping(value = "/build")
    @ApiOperation("获取前端所需菜单")
    public ResponseEntity<Object> buildMenus(){
        //  获取角色菜单列表
        List<MenuDtoBase> menuDtoList = menuService.findByUser(SecurityUtils.getCurrentUserId());
        //  构建菜单树
        List<MenuDtoBase> menuDtos = menuService.buildTree(menuDtoList);
        return new ResponseEntity<>(menuService.buildMenus(menuDtos),HttpStatus.OK);
    }

    @Log("查询菜单")
    @ApiOperation("查询菜单:根据ID获取同级与上级数据")
    @PostMapping("/superior")
    @PreAuthorize("@ams.check('menu:list')")
    public ResponseEntity<Object> getSuperior(@RequestBody List<Long> ids) {
        Set<MenuDtoBase> menuDtos = new LinkedHashSet<>();
        if(CollectionUtil.isNotEmpty(ids)){
            for (Long id : ids) {
                MenuDtoBase menuDto = menuService.findById(id);
                menuDtos.addAll(menuService.getSuperior(menuDto, new ArrayList<>()));
            }
            return new ResponseEntity<>(menuService.buildTree(new ArrayList<>(menuDtos)),HttpStatus.OK);
        }
        return new ResponseEntity<>(menuService.getMenus(null),HttpStatus.OK);
    }

    @Log("新增菜单")
    @ApiOperation("新增菜单")
    @PostMapping
    @PreAuthorize("@ams.check('menu:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Menu resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        menuService.save(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改菜单")
    @ApiOperation("修改菜单")
    @PutMapping
    @PreAuthorize("@ams.check('menu:edit')")
    public ResponseEntity<Object> update(@Validated(Menu.Update.class) @RequestBody Menu resources){
        menuService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除菜单")
    @ApiOperation("删除菜单")
    @DeleteMapping
    @PreAuthorize("@ams.check('menu:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        Set<Menu> menuSet = new HashSet<>();
        for (Long id : ids) {
            List<MenuDtoBase> menuList = menuService.getMenus(id);
            menuSet.add(menuService.getById(id));
            menuSet = menuService.getDeleteMenus(ConvertUtil.convertList(menuList, Menu.class), menuSet);
        }
        menuService.removeByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("导出菜单数据")
    @ApiOperation("导出菜单数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@ams.check('menu:list')")
    public void download(HttpServletResponse response, MenuQueryParam criteria) throws Exception {
        menuService.download(menuService.queryAll(criteria, false), response);
    }

}
