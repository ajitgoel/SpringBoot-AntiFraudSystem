package antifraud;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
@Entity
@Data
@Table(name="user")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "User name is mandatory")
    @Column(unique = true)
    private String username;
    @NotBlank(message = "Password is mandatory")
    private String password;
}
