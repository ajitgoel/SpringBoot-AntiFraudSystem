package antifraud;

import lombok.Data;

import javax.persistence.*;
@Entity
@Data
public class UserResponse {
    @Id
    private Long id;
    private String name;
    private String username;
    private String role;
}