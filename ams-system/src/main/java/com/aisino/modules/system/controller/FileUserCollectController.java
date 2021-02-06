package com.aisino.modules.system.controller;

import com.aisino.annotation.Log;
import com.aisino.modules.system.entity.FileUserCollect;
import com.aisino.modules.system.service.FileUserCollectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * @ClassName FileUserCollectController
 * @Author raoxingxing
 * @Date 2021/2/3 15:30
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collect")
public class FileUserCollectController implements Serializable {

    private final FileUserCollectService fileUserCollectService;

    @Log("收藏")
    @ApiOperation("收藏")
    @PutMapping("/collect")
    public ResponseEntity<Object> collectFile(@RequestBody FileUserCollect fileUserCollect){
        fileUserCollectService.collectFile(fileUserCollect);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("取消收藏")
    @ApiOperation("取消收藏")
    @DeleteMapping("/collect")
    public ResponseEntity<Object> delCollect(@Param("fileId") Long fileId){
        fileUserCollectService.delCollect(fileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
