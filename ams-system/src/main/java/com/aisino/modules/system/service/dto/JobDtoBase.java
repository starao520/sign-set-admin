package com.aisino.modules.system.service.dto;

import com.aisino.base.BaseDataDto;
import lombok.*;

import java.io.Serializable;

/**
* @author rxx
* @date 2020-09-25
*/
@Getter
@Setter
@NoArgsConstructor
public class JobDtoBase extends BaseDataDto implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    private Long id;
//
//    private String name;
//
//    private Boolean enabled;
//
//    private Integer jobSort;
//
//    public JobDtoBase(String name, Boolean enabled) {
//        this.name = name;
//        this.enabled = enabled;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        JobDtoBase dto = (JobDtoBase) o;
//        return Objects.equals(id, dto.id) &&
//                Objects.equals(name, dto.name);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name);
//    }
}
