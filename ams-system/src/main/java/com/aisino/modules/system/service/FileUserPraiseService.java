package com.aisino.modules.system.service;

import com.aisino.base.BaseService;
import com.aisino.modules.system.entity.FileUserPraise;

/**
 * @author faceb
 */
public interface FileUserPraiseService extends BaseService<FileUserPraise> {

    /**
     * 点赞
     * @param fileUserPraise
     */
    void giveLike(FileUserPraise fileUserPraise);

    /**
     * 取消点赞
     * @param fileId
     */
    void delLike(Long fileId);
}
