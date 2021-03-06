package antifraud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,RoleRepository roleRepository)
    {
        return args -> {
            roleRepository.save(Role.AnonymousRole);
            roleRepository.save(Role.MERCHANTRole);
            roleRepository.save(Role.ADMINISTRATORRole);
            roleRepository.save(Role.SUPPORTRole);

            /*Stream<User> userStream =
                    userRepository.findAllByOrderByidAsc().stream().
                    filter(user -> user.getRole() == null);*/
        };
    }
}