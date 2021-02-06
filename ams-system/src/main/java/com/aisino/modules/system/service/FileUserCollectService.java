package com.aisino.modules.system.service;

import com.aisino.base.BaseService;
import com.aisino.modules.system.entity.FileUserCollect;

/**
 * @author faceb
 */
public interface FileUserCollectService extends BaseService<FileUserCollect> {

    /**
     * 收藏
     * @param fileUserCollect
     */
    void collectFile(FileUserCollect fileUserCollect);

    /**
     * 取消收藏
     * @param fileId
     */
    void delCollect(Long fileId);
}
