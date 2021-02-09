package com.aisino.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aisino.modules.system.entity.User;
import com.aisino.modules.system.entity.UsersRoles;
import com.aisino.modules.system.service.UserService;
import com.aisino.modules.system.service.UsersJobsService;
import com.aisino.modules.system.service.UsersRolesService;
import com.aisino.modules.system.service.dto.UserDtoBase;
import com.aisino.utils.*;
import com.aisino.utils.enums.CodeEnum;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.AllArgsConstructor;
import com.aisino.base.PageInfo;
import com.aisino.base.QueryHelpMybatisPlus;
import com.aisino.base.impl.BaseServiceImpl;
import com.aisino.config.FileProperties;
import com.aisino.exception.BadRequestException;
import com.aisino.exception.EntityExistException;
import com.aisino.modules.system.mapper.UsersJobsMapper;
import com.aisino.modules.system.mapper.UsersRolesMapper;
import com.aisino.modules.system.service.dto.UserQueryParam;
import com.aisino.modules.security.service.OnlineUserService;
import com.aisino.modules.security.service.UserCacheClean;
import com.aisino.modules.system.mapper.UserMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;

/**
* @author rxx
* @date 2020-09-25
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    private final FileProperties properties;
    private final RedisUtils redisUtils;
    private final UserCacheClean userCacheClean;
    private final OnlineUserService onlineUserService;

    private final UserMapper userMapper;
    private final UsersRolesService usersRolesService;
    private final UsersJobsService usersJobsService;
    private final UsersRolesMapper usersRolesMapper;
    private final UsersJobsMapper usersJobsMapper;

    @Override
    public PageInfo<UserDtoBase> queryAll(UserQueryParam query, Pageable pageable) {
        IPage<User> page = PageUtil.toMybatisPage(pageable);
        IPage<User> pageData = userMapper.selectPage(page, QueryHelpMybatisPlus.getPredicate(query));
        List<UserDtoBase> userDtos = ConvertUtil.convertList(pageData.getRecords(), UserDtoBase.class);
        if (pageData.getTotal() > 0) {

            QueryWrapper<UsersRoles> userRoleWrapper = new QueryWrapper<>();
            userRoleWrapper.lambda().in(UsersRoles::getUserId, userDtos.stream().map(UserDtoBase::getId).collect(Collectors.toSet()));
            Map<Long, Set<UsersRoles>> usersRolesMap = usersRolesMapper.selectList(userRoleWrapper).stream()
                    .collect(Collectors.groupingBy(UsersRoles::getUserId, Collectors.toSet()));
        }
        return new PageInfo<>(pageData.getTotal(), userDtos);
    }

    @Override
    public List<UserDtoBase> queryAll(UserQueryParam query){
        return ConvertUtil.convertList(userMapper.selectList(QueryHelpMybatisPlus.getPredicate(query)), UserDtoBase.class);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserDtoBase findById(Long id) {
        return ConvertUtil.convert(getById(id), UserDtoBase.class);
    }

    @Override
    public User getByUsername(String userName) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUsername, userName);
        User user = userMapper.selectOne(wrapper);
        return user;
    }

    @Override
    public UserDtoBase findByName(String userName) {
        UserDtoBase dto = ConvertUtil.convert(getByUsername(userName), UserDtoBase.class);
        return dto;
    }

    private User getByEmail(String email) {
        Wrapper<User> wrapper = new QueryWrapper<User>().eq("email", email);
        return userMapper.selectOne(wrapper);
    }
    private User getByPhone(String phone) {
        Wrapper<User> wrapper = new QueryWrapper<User>().eq("phone", phone);
        return userMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(UserDtoBase resources) {
        User user = getByUsername(resources.getUsername());
        if (user != null) {
            throw new EntityExistException(User.class, "username", user.getUsername());
        }
        user = getByEmail(resources.getEmail());
        if (user != null) {
            throw new EntityExistException(User.class, "email", resources.getEmail());
        }
        user = getByPhone(resources.getPhone());
        if (user != null) {
            throw new EntityExistException(User.class, "phone", resources.getPhone());
        }

        user = ConvertUtil.convert(resources, User.class);

        int ret = userMapper.insert(user);
        final Long userId = user.getId();
        if (CollectionUtils.isNotEmpty(resources.getRoles())) {
            resources.getRoles().forEach(role -> {
                UsersRoles ur = new UsersRoles();
                ur.setUserId(userId);
                ur.setRoleId(role.getId());
                usersRolesMapper.insert(ur);
            });
        }
        return ret > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(UserDtoBase resources){
        User user = getById(resources.getId());
        User user1 = getByUsername(user.getUsername());
        User user2 = getByEmail(user.getEmail());
        User user3 = getByPhone(user.getPhone());
        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(User.class, "username", user.getUsername());
        }
        if (user2 != null && !user.getId().equals(user2.getId())) {
            throw new EntityExistException(User.class, "email", user.getEmail());
        }
        if (user3 != null && !user.getId().equals(user3.getId())) {
            throw new EntityExistException(User.class, "phone", user.getPhone());
        }

        // 如果用户的角色改变
        //if (!resources.getRoles().equals(xxxx.getRoles())) {
            redisUtils.del(CacheKey.DATE_USER + resources.getId());
            redisUtils.del(CacheKey.MENU_USER + resources.getId());
            redisUtils.del(CacheKey.ROLE_AUTH + resources.getId());
        //}

        // 如果用户名称修改
        if(!resources.getUsername().equals(user.getUsername())){
            throw new BadRequestException("不能修改用户名");
        }
        // 如果用户被禁用，则清除用户登录信息
        if(!resources.getEnabled()){
            onlineUserService.kickOutForUsername(resources.getUsername());
        }
        if (CollectionUtils.isNotEmpty(resources.getRoles())) {
            usersRolesService.removeByUserId(resources.getId());
            resources.getRoles().stream().forEach(role -> {
                UsersRoles ur = new UsersRoles();
                ur.setUserId(resources.getId());
                ur.setRoleId(role.getId());
                usersRolesMapper.insert(ur);
            });
        }

        user.setUsername(resources.getUsername());
        user.setEmail(resources.getEmail());
        user.setEnabled(resources.getEnabled());
        user.setPhone(resources.getPhone());
        user.setNickName(resources.getNickName());
        user.setGender(resources.getGender());

        delCaches(user.getId(), user.getUsername());
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(String username, String encryptPassword) {
        UpdateWrapper<User> updater = new UpdateWrapper<>();
        updater.lambda().eq(User::getUsername, username);
        User user = new User();
        user.setPassword(encryptPassword);
        user.setPwdResetTime(new Date());
        userMapper.update(user, updater);
        redisUtils.del("user::username:" + username);
        flushCache(username);
    }

    @Override
    public Map<String, String> updateAvatar(MultipartFile multipartFile) {
        User user = getByUsername(SecurityUtils.getCurrentUsername());
        String oldPath = user.getAvatarPath();
        File file = FileUtil.upload(multipartFile, properties.getPath().getAvatar());
        user.setAvatarName(file.getName());
        user.setAvatarPath(Objects.requireNonNull(file).getPath());
        userMapper.updateById(user);
        if (StrUtil.isNotBlank(oldPath)) {
            FileUtil.del(oldPath);
        }
        redisUtils.del("user::username:" + user.getUsername());
        return new HashMap<String, String>() {
            {
                put("avatar", file.getName());
            }
        };
    }

    @Override
    public void updateEmail(String username, String email) {
        User user = getByUsername(username);
        User user2 = getByEmail(email);
        if (ObjectUtil.notEqual(user.getId(), user2.getId())) {
            throw new EntityExistException(User.class, "email", email);
        }
        UpdateWrapper<User> updater = new UpdateWrapper<>();
        updater.lambda().eq(User::getUsername, username);
        User userUpdate = new User();
        userUpdate.setEmail(email);
        userMapper.update(userUpdate, updater);
        redisUtils.del("user::username:" + username);
    }

    @Override
    public void updateCenter(User resources) {
        User user2 = getByPhone(resources.getPhone());
        if (ObjectUtil.notEqual(resources.getId(), user2.getId())) {
            throw new EntityExistException(User.class, "phone", resources.getPhone());
        }
        UpdateWrapper<User> updater = new UpdateWrapper<>();
        updater.lambda().eq(User::getId, resources.getId());
        User userUpdate = new User();
        userUpdate.setPhone(resources.getPhone());
        userUpdate.setGender(resources.getGender());
        userUpdate.setNickName(resources.getNickName());
        userMapper.update(userUpdate, updater);
        redisUtils.del("user::username:" + resources.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Set<Long> ids){
        for (Long id: ids) {
            User user = getById(id);
            delCaches(user.getId(), user.getUsername());
            usersRolesService.removeByUserId(id);
//            usersJobsService.removeByUserId(id);
        }
        return userMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id){
        Set<Long> ids = new HashSet<>(1);
        ids.add(id);
        return this.removeByIds(ids);
    }

    @Override
    public void download(List<UserDtoBase> all, HttpServletResponse response) throws IOException {
      List<Map<String, Object>> list = new ArrayList<>();
      for (UserDtoBase user : all) {
        Map<String,Object> map = new LinkedHashMap<>();
              map.put("用户名", user.getUsername());
              map.put("昵称", user.getNickName());
              map.put("性别", user.getGender());
              map.put("手机号码", user.getPhone());
              map.put("邮箱", user.getEmail());
              map.put("头像地址", user.getAvatarName());
              map.put("头像真实路径", user.getAvatarPath());
              map.put("密码", user.getPassword());
              map.put("是否为admin账号", user.getIsAdmin());
              map.put("状态：1启用、0禁用", user.getEnabled());
              map.put("创建者", user.getCreateBy());
              map.put("更新着", user.getUpdateBy());
              map.put("修改密码的时间", user.getPwdResetTime());
              map.put("创建日期", user.getCreateTime());
              map.put("更新时间", user.getUpdateTime());
        list.add(map);
      }
      FileUtil.downloadExcel(list, response);
    }

    /**
     * 通过邮箱查询用户
     *
     * @param email
     */
    @Override
    public User findByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 用户注册
     *
     * @param user
     */
    @Override
    public void registerUser(User user) {
        checkVerifyCode(user.getVerifyCode(), user.getEmail());
        checkEmail(user.getEmail());
        checkUsername(user.getUsername());
        userMapper.insert(user);
    }

    /**
     * @param username
     * @return
     * @Author raoxingxing
     * @Description 通过用户名或邮箱查询用户
     * @Date 2021/2/9 23:10
     * @Param [username]
     */
    @Override
    public User findByNameOrEmail(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.or();
        wrapper.eq("email", username);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 清理缓存
     *
     * @param id /
     */
    public void delCaches(Long id, String username) {
        redisUtils.del(CacheKey.USER_ID + id);
        redisUtils.del(CacheKey.USER_NAME + username);
        flushCache(username);
    }

    /**
     * 清理 登陆时 用户缓存信息
     *
     * @param username /
     */
    private void flushCache(String username) {
        userCacheClean.cleanUserCache(username);
    }

    /**
     * @Author raoxingxing
     * @Description 查询验证码是否正常
     * @Date 2021/2/7 22:19
     * @Param [verifyCode]
     * @return
    **/
    private void checkVerifyCode(String verifyCode, String email) {
        String key = CodeEnum.REGISTER_EMAIL_CODE.getKey() + email;
        Object codeObj = redisUtils.get(key);
        if (codeObj == null || !verifyCode.equals(codeObj.toString())){
            throw new BadRequestException("验证码错误");
        }
    }

    /**
     * @Author raoxingxing
     * @Description 校验邮箱是否已被注册
     * @Date 2021/2/10 01:42
     * @Param [email]
     * @return
    **/
    private void checkEmail(String email){
        User user = findByEmail(email);
        if (user != null){
            throw new BadRequestException("邮箱已被注册");
        }
    }

    /**
     * @Author raoxingxing
     * @Description 校验用户名是否可用
     * @Date 2021/2/10 01:43
     * @Param [username]
     * @return
    **/
    private void checkUsername(String username){
        UserDtoBase user = findByName(username);
        if (user != null){
            throw new BadRequestException("用户名已存在");
        }
    }
}
