package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user){
        try{
            user.setUsername(user.getUsername().toLowerCase());
            User result1 = userRepository.findByusername(user.getUsername());
            if(result1!=null)
            {
                return new ResponseEntity<>("Username "+ user.getUsername() + " already exists",
                        HttpStatus.CONFLICT);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            //region role, operation
            Role role;
            String operation;
            if(!userRepository.existsByrole(Role.ADMINISTRATORRole))
            {
                role=Role.ADMINISTRATORRole;
                operation=LockUnlockUserRequest.UnlockOperation;
            }
            else
            {
                role=Role.MERCHANTRole;
                operation=LockUnlockUserRequest.LockOperation;
            }
            user.setRole(role);
            user.setLockedstatus(operation);
            //endregion role, operation

            User result2 = userRepository.save(user);
            Map<String,Object> map = new HashMap<>(4);
            map.put("id", result2.getId());
            map.put("name", result2.getName());
            map.put("username", result2.getUsername());
            map.put("role", result2.getRole().getName());
            return new ResponseEntity<>(map,HttpStatus.CREATED);
        }
        catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list")
    //@PreAuthorize("hasAnyRole(\""+Role.ADMINISTRATOR+"\", \""+Role.SUPPORT+"\")")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    ResponseEntity<List<UserResponse>> all() {
        List<UserResponse> users = userRepository.findAll().stream().map(x->{
            UserResponse listUserResponse=new UserResponse();
            listUserResponse.setUsername(x.getUsername());
            listUserResponse.setName(x.getName());
            listUserResponse.setId(x.getId());
            listUserResponse.setRole(x.getRole()==null?null:x.getRole().getName());
            return listUserResponse;
        }).collect(Collectors.toList());
        if((long) users.size() ==0)
        {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        Comparator<UserResponse> compareById =
                Comparator.comparing(UserResponse::getId);
        users.sort(compareById);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/role")
    ResponseEntity<UserResponse> changeUserRole(@RequestBody ChangeUserRoleRequest changeUserRoleRequest) {
        String role=changeUserRoleRequest.getRole();
        if(!role.equals(Role.SUPPORT) && !role.equals(Role.MERCHANT))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Role roleResult = roleRepository.findByname(role);
        if(roleResult==null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,role+ " is not a valid role");
        }
        String username=changeUserRoleRequest.getUsername().toLowerCase();
        User userResult = userRepository.findByusername(username);
        if(userResult==null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User with " +username+ " user name was not found");
        }
        if(userResult.getRole().getName().equals(role))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        userResult.setRole(roleResult);
        userRepository.save(userResult);

        UserResponse user=new UserResponse();
        user.setId(userResult.getId());
        user.setName(userResult.getName());
        user.setUsername(userResult.getUsername());
        user.setRole(userResult.getRole()==null?null:userResult.getRole().getName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/access")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    ResponseEntity<Map<String,String>> lockUnlockUser(@RequestBody LockUnlockUserRequest lockUnlockUserRequest) {
        String operation=lockUnlockUserRequest.getOperation();
        String username=lockUnlockUserRequest.getUsername().toLowerCase();
        User userResult = userRepository.findByusername(username);
        if(userResult==null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User with " +username+ " user name was not found");
        }
        if(userResult.getRole().getName().equals(Role.ADMINISTRATOR))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User with administrator role cannot be blocked");
        }
        userResult.setLockedstatus(operation);
        userRepository.save(userResult);

        Map<String,String> map = new HashMap<>(1);
        map.put("status", "User " +userResult.getUsername()+ " " +userResult.getLockedstatus()+"!");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    ResponseEntity<Map<String,String>> deleteUser(@PathVariable @NotBlank String username) {
        username=username.toLowerCase();
        User user = userRepository.findByusername(username);
        if(user==null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User with " +username+ " user name was not found");
        }
        userRepository.delete(user);
        Map<String,String> map = new HashMap<>(2);
        map.put("username", username);
        map.put("status", "Deleted successfully!");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
