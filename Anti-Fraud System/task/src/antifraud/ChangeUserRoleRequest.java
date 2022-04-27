package antifraud;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
public class ChangeUserRoleRequest{
    @NotBlank(message = "User name is mandatory")
    private String username;
    @NotBlank(message = "Role is mandatory")
    private String role;
}
