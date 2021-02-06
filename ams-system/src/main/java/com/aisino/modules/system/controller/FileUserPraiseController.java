package com.aisino.modules.system.controller;

import com.aisino.annotation.Log;
import com.aisino.modules.system.entity.FileUserPraise;
import com.aisino.modules.system.service.FileUserPraiseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName FileUserPraiseController
 * @Author raoxingxing
 * @Date 2021/2/3 14:50
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/praise")
@Api(tags = "点赞管理")
public class FileUserPraiseController {

    private final FileUserPraiseService fileUserPraiseService;

    @Log("点赞")
    @ApiOperation("点赞")
    @PutMapping("/like")
    public ResponseEntity<Object> giveLike(@RequestBody FileUserPraise fileUserPraise){
        fileUserPraiseService.giveLike(fileUserPraise);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("取消点赞")
    @ApiOperation("取消点赞")
    @DeleteMapping("/like")
    public ResponseEntity<Object> delLike(@Param("fileId") Long fileId){
        fileUserPraiseService.delLike(fileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
