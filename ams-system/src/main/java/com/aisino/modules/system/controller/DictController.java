/*
 *  Copyright 2019-2020 rxx
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
package com.aisino.modules.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import com.aisino.annotation.Log;
import com.aisino.exception.BadRequestException;
import com.aisino.modules.system.entity.Dict;
import com.aisino.modules.system.service.DictService;
import com.aisino.modules.system.service.dto.DictQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
* @author rxx
* @date 2019-04-10
*/
@RestController
@RequiredArgsConstructor
@Api(tags = "系统：字典管理")
@RequestMapping("/api/dict")
public class DictController {

    private final DictService dictService;
    private static final String ENTITY_NAME = "dict";

    @Log("导出字典数据")
    @ApiOperation("导出字典数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@ams.check('dict:list')")
    public void download(HttpServletResponse response, DictQueryParam criteria) throws IOException {
        dictService.download(dictService.queryAll(criteria), response);
    }

    @Log("查询字典")
    @ApiOperation("查询字典")
    @GetMapping(value = "/all")
    @PreAuthorize("@ams.check('dict:list')")
    public ResponseEntity<Object> queryAll(){
        return new ResponseEntity<>(dictService.queryAll(new DictQueryParam()),HttpStatus.OK);
    }

    @Log("查询字典")
    @ApiOperation("查询字典")
    @GetMapping
    @PreAuthorize("@ams.check('dict:list')")
    public ResponseEntity<Object> query(DictQueryParam query, Pageable pageable){
        return new ResponseEntity<>(dictService.queryAll(query,pageable),HttpStatus.OK);
    }

    @Log("新增字典")
    @ApiOperation("新增字典")
    @PostMapping
    @PreAuthorize("@ams.check('dict:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Dict resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        dictService.save(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改字典")
    @ApiOperation("修改字典")
    @PutMapping
    @PreAuthorize("@ams.check('dict:edit')")
    public ResponseEntity<Object> update(@RequestBody Dict resources){
        dictService.updateById(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除字典")
    @ApiOperation("删除字典")
    @DeleteMapping
    @PreAuthorize("@ams.check('dict:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        dictService.removeByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}