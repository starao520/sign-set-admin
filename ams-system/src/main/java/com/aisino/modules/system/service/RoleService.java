package com.aisino.modules.system.service;

import com.aisino.modules.system.entity.Role;
import com.aisino.base.BaseService;
import com.aisino.base.PageInfo;
import com.aisino.modules.system.service.dto.RoleDtoBase;
import com.aisino.modules.system.service.dto.RoleQueryParam;
import com.aisino.modules.system.service.dto.RoleSmallDto;
import com.aisino.modules.system.service.dto.UserDtoBase;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
* @author rxx
* @date 2020-09-25
*/
public interface RoleService  extends BaseService<Role> {

    /**
    * 查询数据分页
    * @param query 条件
    * @param pageable 分页参数
    * @return PageInfo<RoleDtoBase>
    */
    PageInfo<RoleDtoBase> queryAll(RoleQueryParam query, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param query 条件参数
    * @return List<RoleDtoBase>
    */
    List<RoleDtoBase> queryAll(RoleQueryParam query);
    List<RoleDtoBase> queryAll();

    Role getById(Long id);
    RoleDtoBase findById(Long id);

    /**
     * 插入一条新数据。
     */
    boolean save(RoleDtoBase resources);
    boolean updateById(RoleDtoBase resources);
    boolean removeById(Long id);
    boolean removeByIds(Set<Long> ids);

    /**
     * 根据用户ID查询
     * @param id 用户ID
     * @return /
     */
    List<RoleSmallDto> findByUsersId(Long id);

    /**
     * 根据角色查询角色级别
     * @param roleIds /
     * @return /
     */
    Integer findByRoles(Set<Long> roleIds);

    /**
     * 修改绑定的菜单
     * @param resources /
     */
    void updateMenu(RoleDtoBase resources);

    /**
     * 获取用户权限信息
     * @param user 用户信息
     * @return 权限信息
     */
    List<GrantedAuthority> mapToGrantedAuthorities(UserDtoBase user);

    /**
     * 验证是否被用户关联
     * @param ids /
     */
    void verification(Set<Long> ids);

    /**
     * 根据菜单Id查询
     * @param menuIds /
     * @return /
     */
    List<Role> findInMenuId(List<Long> menuIds);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<RoleDtoBase> all, HttpServletResponse response) throws IOException;

    /**
     * @Author raoxingxing
     * @Description 分页查询角色
     * @Date 2021/1/16 16:55
     * @Param [criteria, pageable]
     * @return 
    **/
    Object getRolesByPage(RoleQueryParam criteria, Pageable pageable);
}
