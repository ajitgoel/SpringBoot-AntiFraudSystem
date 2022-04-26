package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try{
            user.setUsername(user.getUsername().toLowerCase());
            User result1 = userRepository.findByUserName(user.getUsername());
            if(result1!=null)
            {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
            User result2 = userRepository.save(user);
            Map<String,Object> map = new HashMap<>(3);
            map.put("id", result2.getId());
            map.put("name", result2.getName());
            map.put("username", result2.getUsername());
            return new ResponseEntity<>(map,HttpStatus.CREATED);
        }
        catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/list")
    ResponseEntity<List<User>> all() {
        List<User> users = userRepository.findAll();
        if((long) users.size() ==0)
        {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        Comparator<User> compareById = Comparator.comparing(User::getId);
        users.sort(compareById);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @DeleteMapping("/user/{username}")
    ResponseEntity<Map<String,String>> deleteEmployee(@PathVariable @NotBlank String username) {
        username=username.toLowerCase();
        User user = userRepository.findByUserName(username);
        if(user==null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
