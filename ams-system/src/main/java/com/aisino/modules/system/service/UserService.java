package com.aisino.modules.system.service;

import com.aisino.modules.system.entity.User;
import com.aisino.base.BaseService;
import com.aisino.base.PageInfo;
import com.aisino.modules.system.service.dto.UserDtoBase;
import com.aisino.modules.system.service.dto.UserQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author rxx
* @date 2020-09-25
*/
public interface UserService  extends BaseService<User> {

    /**
    * 查询数据分页
    * @param query 条件
    * @param pageable 分页参数
    * @return PageInfo<UserDtoBase>
    */
    PageInfo<UserDtoBase> queryAll(UserQueryParam query, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param query 条件参数
    * @return List<UserDtoBase>
    */
    List<UserDtoBase> queryAll(UserQueryParam query);

    User getById(Long id);
    UserDtoBase findById(Long id);

    /**
     * 根据用户名查询
     * @param userName /
     * @return /
     */
    User getByUsername(String userName);
    UserDtoBase findByName(String userName);
    /**
     * 插入一条新数据。
     */
    boolean save(UserDtoBase resources);
    boolean updateById(UserDtoBase resources);
    boolean removeById(Long id);
    boolean removeByIds(Set<Long> ids);

    /**
     * 修改密码
     * @param username 用户名
     * @param encryptPassword 密码
     */
    void updatePass(String username, String encryptPassword);

    /**
     * 修改头像
     * @param file 文件
     * @return /
     */
    Map<String, String> updateAvatar(MultipartFile file);

    /**
     * 修改邮箱
     * @param username 用户名
     * @param email 邮箱
     */
    void updateEmail(String username, String email);

    /**
     * 用户自助修改资料
     * @param resources /
     */
    void updateCenter(User resources);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<UserDtoBase> all, HttpServletResponse response) throws IOException;

    /**
     * 通过邮件查询用户
     * @param email
     * @return
     */
    User findByEmail(String email);

    /**
     * 用户注册
     * @param user
     */
    void registerUser(User user);
}
