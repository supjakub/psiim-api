package languagequiz.api.controller;

import languagequiz.api.model.Credentials;
import languagequiz.api.model.User;
import languagequiz.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<User> createUser(@RequestBody Credentials _credentials) {
        return userService.createUser(_credentials);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> loginUser(@RequestBody Credentials _credentials) {
        return userService.loginUser(_credentials);
    }

    @GetMapping("/api/user")
    public ResponseEntity<User> getUser(@RequestHeader("token") String _token) {
        return userService.getUser(_token);
    }

    @PutMapping("/api/user")
    public ResponseEntity<HttpStatus> updateUser(@RequestHeader("token") String _token, @RequestBody Credentials _credentials) {
        return userService.updateUser(_token, _credentials);
    }

    @DeleteMapping("api/user")
    public ResponseEntity<HttpStatus> deleteUser(@RequestHeader("token") String _token) {
        return userService.deleteUser(_token);
    }

    @GetMapping("/api/user/stats")
    public ResponseEntity<Map<String, Integer>> getUserStatistics(@RequestHeader("token") String _token) {
        return userService.getStatistics(_token);
    }
}
