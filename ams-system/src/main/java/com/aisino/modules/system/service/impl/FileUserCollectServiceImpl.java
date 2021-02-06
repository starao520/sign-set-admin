package com.aisino.modules.system.service.impl;

import com.aisino.base.impl.BaseServiceImpl;
import com.aisino.exception.BadRequestException;
import com.aisino.mapper.QiniuContentMapper;
import com.aisino.modules.system.entity.FileUserCollect;
import com.aisino.modules.system.mapper.FileUserCollectMapper;
import com.aisino.modules.system.service.FileUserCollectService;
import com.aisino.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName FileUserCollectServiceImpl
 * @Author raoxingxing
 * @Date 2021/2/3 15:39
 **/
@Service
@RequiredArgsConstructor
public class FileUserCollectServiceImpl extends BaseServiceImpl<FileUserCollect> implements FileUserCollectService {

    private final FileUserCollectMapper fileUserCollectMapper;

    private final QiniuContentMapper qiniuContentMapper;
    /**
     * 收藏
     *
     * @param fileUserCollect
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectFile(FileUserCollect fileUserCollect) {
        try {
            fileUserCollect.setUserId(SecurityUtils.getCurrentUserId());
            fileUserCollectMapper.insert(fileUserCollect);

            qiniuContentMapper.addCollectCount(fileUserCollect.getFileId());
        }catch (BadRequestException e){
            e.printStackTrace();
            throw new BadRequestException("收藏失败");
        }
    }

    /**
     * 取消收藏
     *
     * @param fileId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delCollect(Long fileId) {
        try {
            QueryWrapper<FileUserCollect> wrapper = new QueryWrapper<>();
            wrapper.eq("file_id", fileId);
            wrapper.eq("user_id", SecurityUtils.getCurrentUserId());
            fileUserCollectMapper.delete(wrapper);

            qiniuContentMapper.subCollectCount(fileId);
        }catch (BadRequestException e){
            e.printStackTrace();
            throw new BadRequestException("取消失败");
        }
    }
}
