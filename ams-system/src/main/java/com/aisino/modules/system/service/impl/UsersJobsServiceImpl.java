package com.aisino.modules.system.service.impl;

import com.aisino.modules.system.entity.UsersJobs;
import com.aisino.modules.system.service.UsersJobsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.AllArgsConstructor;
import com.aisino.base.impl.BaseServiceImpl;
import com.aisino.modules.system.mapper.UsersJobsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jinjin on 2020-09-25.
 */
@AllArgsConstructor
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UsersJobsServiceImpl extends BaseServiceImpl<UsersJobs> implements UsersJobsService {

//    private final UsersJobsMapper usersJobsMapper;
//
//    @Override
//    public List<Long> queryUserIdByJobId(Long id) {
//        LambdaQueryWrapper<UsersJobs> query = new LambdaQueryWrapper<>();
//        query.eq(UsersJobs::getJobId, id);
//        return usersJobsMapper.selectList(query).stream().map(UsersJobs::getUserId)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Long> queryJobIdByUserId(Long id) {
//        LambdaQueryWrapper<UsersJobs> query = new LambdaQueryWrapper<>();
//        query.eq(UsersJobs::getUserId, id);
//        return usersJobsMapper.selectList(query).stream().map(UsersJobs::getJobId)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int removeByUserId(Long id) {
//        LambdaUpdateWrapper<UsersJobs> wrapper = new LambdaUpdateWrapper<>();
//        wrapper.eq(UsersJobs::getUserId, id);
//        return usersJobsMapper.delete(wrapper);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int removeByJobId(Long id) {
//        LambdaUpdateWrapper<UsersJobs> wrapper = new LambdaUpdateWrapper<>();
//        wrapper.eq(UsersJobs::getJobId, id);
//        return usersJobsMapper.delete(wrapper);
//    }
}