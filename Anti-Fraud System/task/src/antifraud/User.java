package antifraud;
import java.util.Set;
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
    @Column(nullable = false, unique = true)
    private String username;
    @NotBlank(message = "Password is mandatory")
    private String password;
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_privileges", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Set<Privilege> privileges;
}
