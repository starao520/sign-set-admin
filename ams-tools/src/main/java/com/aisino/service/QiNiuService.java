/*
 *  Copyright 2019-2020 Zheng Jie
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
package com.aisino.service;

import com.aisino.base.BaseService;
import com.aisino.entity.QiniuConfig;
import com.aisino.entity.QiniuContent;
import com.aisino.service.dto.FIleListDto;
import com.aisino.service.dto.QiniuContentDto;
import com.aisino.service.dto.QiniuContentQueryParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author rxx
 * @date 2018-12-31
 */
public interface QiNiuService extends BaseService<QiniuContent> {

    /**
     * 查配置
     * @return QiniuConfig
     */
    QiniuConfig find();

    /**
     * 修改配置
     * @param qiniuConfig 配置
     * @return QiniuConfig
     */
    QiniuConfig config(QiniuConfig qiniuConfig);

    /**
     * 分页查询
     * @param criteria 条件
     * @param pageable 分页参数
     * @return /
     */
    Object queryAll(QiniuContentQueryParam criteria, Pageable pageable);

    /**
     * 查询全部
     * @param criteria 条件
     * @return /
     */
    List<QiniuContent> queryAll(QiniuContentQueryParam criteria);

    /**
     * 上传文件
     * @param file 文件
     * @param qiniuConfig 配置
     * @param scope 作用域
     * @return QiniuContent
     */
    QiniuContent upload(MultipartFile file, QiniuConfig qiniuConfig, String scope);
    
    /**
     * @Author raoxingxing
     * @Description 通过url查询文件对象
     * @Date 2021/2/14 23:29
     * @Param [url]
     * @return 
    **/
    QiniuContent findByContentUrl(String url);

    /**
     * 查询文件
     * @param id 文件ID
     * @return QiniuContent
     */
    QiniuContent findByContentId(Long id);

    /**
     * 下载文件
     * @param content 文件信息
     * @param config 配置
     * @return String
     */
    String download(QiniuContent content, QiniuConfig config);

    /**
     * 删除文件
     * @param content 文件
     * @param config 配置
     */
    void delete(QiniuContent content, QiniuConfig config);

    /**
     * 同步数据
     * @param config 配置
     */
    void synchronize(QiniuConfig config);

    /**
     * 删除文件
     * @param ids 文件ID数组
     * @param config 配置
     */
    void deleteAll(Long[] ids, QiniuConfig config);

    /**
     * 更新数据
     * @param type 类型
     */
    void update(String type);

    /**
     * 导出数据
     * @param queryAll /
     * @param response /
     * @throws IOException /
     */
    void downloadList(List<QiniuContent> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 访客分页查询文件
     * @param pageable
     * @return
     */
    IPage<FIleListDto> queryFileList(Pageable pageable);

    /**
     * 访客查询最热文件
     * @param pageable
     * @return
     */
    IPage<FIleListDto> queryPraiseList(Pageable pageable);

    /**
     * 访客查询最爱文件
     * @param pageable
     * @return
     */
    IPage<FIleListDto> queryFavoriteList(Pageable pageable);

    /**
     * @Author raoxingxing
     * @Description 查询交集文件列表
     * @Date 2021/2/14 23:58
     * @Param [pageable]
     * @return
    **/
    Object queryAttentionList(Pageable pageable);

    /**
     * @Author raoxingxing
     * @Description 查询收集文件列表
     * @Date 2021/2/16 22:44
     * @Param [pageable]
     * @return 
    **/
    Object queryCollectList(Pageable pageable);
}
