package antifraud;
import java.util.Set;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "roles")
public class Role {
    public static final String Anonymous="Anonymous";
    public static final String MERCHANT="MERCHANT";
    public static final String ADMINISTRATOR="ADMINISTRATOR";
    public static final String SUPPORT="SUPPORT";
    public static final Role AnonymousRole=new Role(1, Anonymous);
    public static final Role MERCHANTRole=new Role(2,MERCHANT);
    public static final Role ADMINISTRATORRole=new Role(3,ADMINISTRATOR);
    public static final Role SUPPORTRole=new Role(4,SUPPORT);

    public Role(Integer id, String name){
        this.id=id;
        this.name=name;
    }
    @Id
    @Column(name = "role_id")
    private Integer id;
    private String name;
    @OneToMany(mappedBy="role")
    private Set<User> users;
    public Role() {
    }
}
