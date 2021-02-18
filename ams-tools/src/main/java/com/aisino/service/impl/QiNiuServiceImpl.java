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
package com.aisino.service.impl;

import com.aisino.service.dto.FIleListDto;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.RequiredArgsConstructor;
import com.aisino.base.PageInfo;
import com.aisino.base.QueryHelpMybatisPlus;
import com.aisino.base.impl.BaseServiceImpl;
import com.aisino.entity.QiniuConfig;
import com.aisino.entity.QiniuContent;
import com.aisino.mapper.QiniuConfigMapper;
import com.aisino.mapper.QiniuContentMapper;
import com.aisino.service.dto.QiniuContentDto;
import com.aisino.service.dto.QiniuContentQueryParam;
import com.aisino.utils.*;
import com.aisino.exception.BadRequestException;
import com.aisino.service.QiNiuService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author rxx
 * @date 2018-12-31
 */
@Service
@RequiredArgsConstructor
public class QiNiuServiceImpl extends BaseServiceImpl<QiniuContent> implements QiNiuService {

    private final QiniuConfigMapper qiNiuConfigRepository;
    private final QiniuContentMapper qiniuContentRepository;

    @Value("${qiniu.max-size}")
    private Long maxSize;

    @Override
    public QiniuConfig find() {
        return qiNiuConfigRepository.selectById(1L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QiniuConfig config(QiniuConfig qiniuConfig) {
        qiniuConfig.setId(1L);
        String http = "http://", https = "https://";
        if (!(qiniuConfig.getHost().toLowerCase().startsWith(http)||qiniuConfig.getHost().toLowerCase().startsWith(https))) {
            throw new BadRequestException("外链域名必须以http://或者https://开头");
        }
        qiNiuConfigRepository.updateById(qiniuConfig);
        return qiniuConfig;
    }

    @Override
    public PageInfo<?> queryAll(QiniuContentQueryParam query, Pageable pageable){
        IPage<QiniuContent> page = PageUtil.toMybatisPage(pageable);
        QueryWrapper<QiniuContent> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(query.getKey())){
            wrapper.like("name", query.getKey());
        }
        IPage<QiniuContent> pageList = qiniuContentRepository.selectPage(page, wrapper);
        return ConvertUtil.convertPage(pageList, QiniuContentDto.class);
    }

    @Override
    public List<QiniuContent> queryAll(QiniuContentQueryParam query) {
        return qiniuContentRepository.selectList(QueryHelpMybatisPlus.getPredicate(query));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QiniuContent upload(MultipartFile file, QiniuConfig qiniuConfig, String scope) {
        FileUtil.checkSize(maxSize, file.getSize());
        if(qiniuConfig.getId() == null){
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(qiniuConfig.getZone()));
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        String upToken = auth.uploadToken(qiniuConfig.getBucket());
        try {
            String key = file.getOriginalFilename();
            String suffix = FileUtil.getExtensionName(key);
            String realName = FileUtil.getFileNameNoEx(key);
            String fileName = realName + System.currentTimeMillis() + "." + suffix;
            if(qiniuContentRepository.findByKey(key) != null) {
                key = QiNiuUtil.getKey(key);
            }
            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            //解析上传成功的结果

            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            QiniuContent content = qiniuContentRepository.findByKey(FileUtil.getFileNameNoEx(putRet.key));
            if(content == null){
                //存入数据库
                QiniuContent qiniuContent = new QiniuContent();
                qiniuContent.setSuffix(FileUtil.getExtensionName(putRet.key));
                qiniuContent.setBucket(qiniuConfig.getBucket());
                qiniuContent.setType(qiniuConfig.getType());
                qiniuContent.setRealName(realName);
                qiniuContent.setName(FileUtil.getFileNameNoEx(putRet.key));
                qiniuContent.setUrl(qiniuConfig.getHost()+"/"+putRet.key);
                qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(file.getSize()+"")));
                qiniuContent.setCreateUserId(SecurityUtils.getCurrentUserId());
                qiniuContent.setScope(scope);
                qiniuContentRepository.insert(qiniuContent);
                return qiniuContent;
            }
            return content;
        } catch (Exception e) {
           throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * @param url
     * @return
     * @Author raoxingxing
     * @Description 通过url查询文件对象
     * @Date 2021/2/14 23:22
     * @Param [url]
     */
    @Override
    public QiniuContent findByContentUrl(String url) {
        try {
            QueryWrapper<QiniuContent> wrapper = new QueryWrapper<>();
            wrapper.eq("url", url);
            return qiniuContentRepository.selectOne(wrapper);
        }catch (BadRequestException e){
            throw new BadRequestException("查询文件失败");
        }
    }

    @Override
    public QiniuContent findByContentId(Long id) {
        return qiniuContentRepository.selectById(id);
    }

    @Override
    public String download(QiniuContent content,QiniuConfig config){
        String finalUrl;
        String type = "公开";
        if(type.equals(content.getType())){
            finalUrl  = content.getUrl();
        } else {
            Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
            // 1小时，可以自定义链接过期时间
            long expireInSeconds = 3600;
            finalUrl = auth.privateDownloadUrl(content.getUrl(), expireInSeconds);
        }
        return finalUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(QiniuContent content, QiniuConfig config) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(config.getZone()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(content.getBucket(), content.getName() + "." + content.getSuffix());
            qiniuContentRepository.deleteById(content);
        } catch (QiniuException ex) {
            qiniuContentRepository.deleteById(content);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronize(QiniuConfig config) {
        if(config.getId() == null){
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(config.getZone()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";
        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(config.getBucket(), prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            QiniuContent qiniuContent;
            FileInfo[] items = fileListIterator.next();
            if (items == null || items.length == 0) {
                continue;
            }
            for (FileInfo item : items) {
                if(qiniuContentRepository.findByKey(FileUtil.getFileNameNoEx(item.key)) == null){
                    qiniuContent = new QiniuContent();
                    qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(item.fsize+"")));
                    qiniuContent.setSuffix(FileUtil.getExtensionName(item.key));
                    qiniuContent.setName(FileUtil.getFileNameNoEx(item.key));
                    qiniuContent.setType(config.getType());
                    qiniuContent.setBucket(config.getBucket());
                    qiniuContent.setUrl(config.getHost()+"/"+item.key);
                    qiniuContentRepository.insert(qiniuContent);
                }
            }
        }
    }

    @Override
    public void deleteAll(Long[] ids, QiniuConfig config) {
        for (Long id : ids) {
            delete(findByContentId(id), config);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String type) {
        qiNiuConfigRepository.updateType(type);
    }

    @Override
    public void downloadList(List<QiniuContent> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QiniuContent content : queryAll) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("文件名", content.getName());
            map.put("文件类型", content.getSuffix());
            map.put("空间名称", content.getBucket());
            map.put("文件大小", content.getSize());
            map.put("空间类型", content.getType());
            map.put("创建日期", content.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 访客分页查询文件
     *
     * @param pageable
     * @return
     */
    @Override
    public IPage<FIleListDto> queryFileList(Pageable pageable) {

        return query(pageable, null);
    }

    /**
     * 访客查询最热文件
     *
     * @param pageable
     * @return
     */
    @Override
    public IPage<FIleListDto> queryPraiseList(Pageable pageable) {

        return query(pageable, "praise_count");
    }

    /**
     * 访客查询最爱文件
     *
     * @param pageable
     * @return
     */
    @Override
    public IPage<FIleListDto> queryFavoriteList(Pageable pageable) {

        return query(pageable, "collect_count");
    }

    /**
     * @param pageable
     * @return
     * @Author raoxingxing
     * @Description 查询交集文件列表
     * @Date 2021/2/14 23:58
     * @Param [pageable]
     */
    @Override
    public IPage<FIleListDto> queryAttentionList(Pageable pageable) {
        Page<QiniuContent> page = getPage(pageable);
        IPage<FIleListDto> pageList = qiniuContentRepository.queryAttentionList(page, SecurityUtils.getCurrentUserId());
        return pageList;
    }

    /**
     * @param pageable
     * @return
     * @Author raoxingxing
     * @Description 查询收集文件列表
     * @Date 2021/2/15 00:03
     * @Param [pageable]
     */
    @Override
    public IPage<FIleListDto> queryCollectList(Pageable pageable) {
        Page<QiniuContent> page = getPage(pageable);
        IPage<FIleListDto> pageList = qiniuContentRepository.queryCollectList(page, SecurityUtils.getCurrentUserId());
        return pageList;
    }

    /**
     * 公共查询类
     * @param pageable
     * @param sort
     * @return
     */
    private IPage<FIleListDto> query(Pageable pageable, String sort) {
        Page<QiniuContent> page = getPage(pageable);
        IPage<FIleListDto> pageList = qiniuContentRepository.queryFileListByPage(page, sort, SecurityUtils.getCurrentUserId());
        return pageList;
    }

    private Page<QiniuContent> getPage(Pageable pageable){
        Page<QiniuContent> page = new Page<>();
        page.setSize(pageable.getPageSize());
        page.setCurrent(pageable.getPageNumber());
        return page;
    }
}
