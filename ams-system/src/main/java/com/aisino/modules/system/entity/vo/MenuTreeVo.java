package com.aisino.modules.system.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName MenuTreeVo
 * 构建最简单的菜单树
 * @Author raoxingxing
 * @Date 2021/1/14 16:24
 **/
@Data
public class MenuTreeVo {

    private Long id;

    private Long pid;

    private String label;

    private List<MenuTreeVo> children;
}
