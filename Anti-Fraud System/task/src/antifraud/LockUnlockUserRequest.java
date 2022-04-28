package antifraud;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class LockUnlockUserRequest {
    public static final String UnlockOperation="UNLOCK";
    public static final String LockOperation="LOCK";

    @NotBlank(message = "User name is mandatory")
    private String username;
    @NotBlank(message = "Operation is mandatory")
    @Pattern(regexp="/^(UNLOCK|LOCK)$/")
    private String operation;
}
