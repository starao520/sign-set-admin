package com.aisino.mapper;

import com.aisino.base.CommonMapper;
import com.aisino.entity.QiniuContent;
import com.aisino.service.dto.FIleListDto;
import com.aisino.service.dto.QiniuContentDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;

/**
* @author rxx
* @date 2020-09-27
*/
@Repository
public interface QiniuContentMapper extends CommonMapper<QiniuContent> {

    QiniuContent findByKey(String key);

    /**
     * 分页查询文件
     * @param page
     * @param sort
     * @param userId
     * @return
     */
    IPage<FIleListDto> queryFileListByPage(Page<QiniuContent> page, String sort, Long userId);

    /**
     * 点赞次数加1
     * @param fileId
     */
    void addPraiseCount(Long fileId);

    /**
     * 收藏次数加1
     * @param fileId
     */
    void addCollectCount(Long fileId);

    /**
     * 收藏次数减1
     * @param fileId
     */
    void subCollectCount(Long fileId);

    /**
     * 点赞次数减1
     * @param fileId
     */
    void subPraiseCount(Long fileId);

    /**
     * @Author raoxingxing
     * @Description 查询交集列表
     * @Date 2021/2/18 10:08
     * @Param [page, currentUserId]
     * @return
    **/
    IPage<FIleListDto> queryAttentionList(Page<QiniuContent> page, Long userId);

    /**
     * @Author raoxingxing
     * @Description 查询收集列表
     * @Date 2021/2/18 10:13
     * @Param [page, currentUserId]
     * @return
    **/
    IPage<FIleListDto> queryCollectList(Page<QiniuContent> page, Long userId);
}
