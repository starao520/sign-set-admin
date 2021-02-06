package com.aisino.modules.system.service.impl;

import com.aisino.base.impl.BaseServiceImpl;
import com.aisino.exception.BadRequestException;
import com.aisino.mapper.QiniuContentMapper;
import com.aisino.modules.system.entity.FileUserPraise;
import com.aisino.modules.system.mapper.FileUserPraiseMapper;
import com.aisino.modules.system.mapper.UserMapper;
import com.aisino.modules.system.service.FileUserPraiseService;
import com.aisino.modules.system.service.UserService;
import com.aisino.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName FileUserPraiseServiceImpl
 * @Author raoxingxing
 * @Date 2021/2/3 14:54
 **/
@Service
@RequiredArgsConstructor
public class FileUserPraiseServiceImpl extends BaseServiceImpl<FileUserPraise> implements FileUserPraiseService {

    final private FileUserPraiseMapper fileUserPraiseMapper;

    final private QiniuContentMapper qiniuContentMapper;

    /**
     * 点赞
     *
     * @param fileUserPraise
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void giveLike(FileUserPraise fileUserPraise) {
        try {
            // 插入中间表
            fileUserPraise.setUserId(SecurityUtils.getCurrentUserId());
            fileUserPraiseMapper.insert(fileUserPraise);

            //  计数加一
            qiniuContentMapper.addPraiseCount(fileUserPraise.getFileId());
        }catch (BadRequestException e){
            e.printStackTrace();
            throw new BadRequestException("点赞失败");
        }
    }

    /**
     * 取消点赞
     *
     * @param fileId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delLike(Long fileId) {
        try {
            QueryWrapper<FileUserPraise> wrapper = new QueryWrapper<>();
            wrapper.eq("file_id", fileId);
            wrapper.eq("user_id", SecurityUtils.getCurrentUserId());
            fileUserPraiseMapper.delete(wrapper);

            qiniuContentMapper.subPraiseCount(fileId);
        }catch (BadRequestException e){
            e.printStackTrace();
            throw new BadRequestException("取消失败");
        }
    }
}
