package com.aisino.modules.system.service.dto;

import lombok.Getter;
import lombok.Setter;
import com.aisino.base.BaseDataDto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
* @author rxx
* @date 2020-09-25
*/
@Getter
@Setter
public class RoleDtoBase extends BaseDataDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Set<MenuDtoBase> menus;

//    private Set<DeptDto> depts;

    private String name;

    private Integer level;

    private String description;

//    private String dataScope;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleDtoBase roleDto = (RoleDtoBase) o;
        return Objects.equals(id, roleDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
