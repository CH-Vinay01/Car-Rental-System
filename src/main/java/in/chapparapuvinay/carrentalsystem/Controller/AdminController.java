package in.chapparapuvinay.carrentalsystem.Controller;

import in.chapparapuvinay.carrentalsystem.entity.AdminEntity;
import in.chapparapuvinay.carrentalsystem.service.AdminService;
import lombok.AllArgsConstructor;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("api/admin")
@AllArgsConstructor
public class AdminController {
    private AdminService service;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> verifyUser(@RequestBody Map<String, String> loginMap) {

        String username = loginMap.get("username");
        String password = loginMap.get("password");
        if (username == null || password == null) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("message", "Bad Request: Username and password are required.");
            return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }

        boolean verified = service.verifyUser(username, password);

        if (verified) {
            Map<String, Object> successBody = new HashMap<>();
            successBody.put("success", true);
            successBody.put("message", "Login successful. Welcome back!");

            return new ResponseEntity<>(successBody, HttpStatus.OK);
        } else {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("message", "Invalid username or password.");

            return new ResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/add")
    public AdminEntity addUser(@RequestBody Map<String, String> signupMap){
        String username = signupMap.get("username");
        String password = signupMap.get("password");

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        AdminEntity newAdmin = service.addUser(username, password);
        return newAdmin;
    }




}

