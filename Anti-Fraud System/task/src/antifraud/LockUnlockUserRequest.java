package antifraud;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LockUnlockUserRequest {
    @NotBlank(message = "User name is mandatory")
    private String username;
    @NotBlank(message = "Operation is mandatory")
    private Operation operation;
}
