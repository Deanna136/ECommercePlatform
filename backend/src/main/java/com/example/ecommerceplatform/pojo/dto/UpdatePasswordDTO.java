package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdatePasswordDTO {
    @NotNull(message = "管理员ID不能为空")
    private Long id;

    @NotNull(message = "修改密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String password;
}
