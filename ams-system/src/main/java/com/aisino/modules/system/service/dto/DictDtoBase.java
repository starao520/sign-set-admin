package com.aisino.modules.system.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.aisino.base.BaseDataDto;

import java.io.Serializable;

/**
* @author rxx
* @date 2020-09-24
*/
@Getter
@Setter
@NoArgsConstructor
public class DictDtoBase extends BaseDataDto implements Serializable {

    private Long id;

    //     private List<DictDetailDto> dictDetails;

    private String name;

    private String description;
}
