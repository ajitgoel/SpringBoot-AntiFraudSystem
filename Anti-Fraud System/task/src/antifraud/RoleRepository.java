package antifraud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByname(String name);
}
