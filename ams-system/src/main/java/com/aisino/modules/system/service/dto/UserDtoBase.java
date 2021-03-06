package com.aisino.modules.system.service.dto;

import com.aisino.base.BaseDataDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
* @author rxx
* @date 2020-09-25
*/
@Getter
@Setter
public class UserDtoBase extends BaseDataDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Set<RoleSmallDto> roles;

    private String username;

    private String nickName;

    private String gender;

    private String phone;

    private String email;

    private String avatarName;

    private String avatarPath;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private Boolean isAdmin;

    private Boolean enabled;

    private Date pwdResetTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDtoBase dto = (UserDtoBase) o;
        return Objects.equals(id, dto.id) &&
                Objects.equals(username, dto.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

}
