package in.chapparapuvinay.carrentalsystem.Controller;

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

        // --- 1. Basic Validation ---
        if (username == null || password == null) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("message", "Bad Request: Username and password are required.");
            return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }

        boolean verified = service.verifyUser(username, password);

        if (verified) {
            // --- 2. SUCCESS RESPONSE (JSON) ---
            Map<String, Object> successBody = new HashMap<>();
            successBody.put("success", true);
            successBody.put("message", "Login successful!");

            // This returns HTTP 200 OK with the JSON body
            return new ResponseEntity<>(successBody, HttpStatus.OK);
        } else {
            // --- 3. FAILURE RESPONSE (JSON) ---
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("message", "Login Failed: Invalid username or password.");

            // This returns HTTP 401 Unauthorized with the JSON body
            return new ResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
        }
    }


}

