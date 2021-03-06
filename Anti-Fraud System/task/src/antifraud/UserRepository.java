package antifraud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
interface UserRepository extends JpaRepository<User, Long> {
    User findByusername(String username);
    Boolean existsByrole(Role role);
}
