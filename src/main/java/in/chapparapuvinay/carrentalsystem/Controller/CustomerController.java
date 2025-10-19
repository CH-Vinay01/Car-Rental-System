package in.chapparapuvinay.carrentalsystem.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.chapparapuvinay.carrentalsystem.io.CustomerRequest;
import in.chapparapuvinay.carrentalsystem.io.CustomerResponse;
import in.chapparapuvinay.carrentalsystem.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/customer")
@AllArgsConstructor
public class CustomerController {
    private CustomerService service;

    @PostMapping
    public CustomerResponse createNewCustomer(@RequestPart("Customer") String customer,
                                   @RequestPart("image") MultipartFile dp,
                                   @RequestPart("dl") MultipartFile dl){

        ObjectMapper objectMapper = new ObjectMapper();
        CustomerRequest request;
        System.out.println(customer);
        try {
            request = objectMapper.readValue(customer, CustomerRequest.class);
        }catch (JsonProcessingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON Format for 'Customer' part: " + ex.getMessage());
        }
        CustomerResponse response = service.createNewUser(request,dl,dp);
        return response;
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyUser(@RequestBody Map<String, String> loginMap) {

        String email = loginMap.get("email");
        String password = loginMap.get("password");
        if (email == null || password == null) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("message", "Bad Request: Username and password are required.");

            return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }

        boolean verified = service.verifyUser(email, password);

        if (verified) {
            Map<String, Object> successBody = new HashMap<>();
            successBody.put("success", true);
            successBody.put("message", "Login successful. Welcome back!");
            successBody.put("id",service.getId(email));;

            return new ResponseEntity<>(successBody, HttpStatus.OK);
        } else {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("message", "Invalid username or password.");

            return new ResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getIdByEmail(@RequestParam String email) {
        String customerId = service.getId(email);

        if (customerId != null) {
            Map<String, String> response = new HashMap<>();
            response.put("customerId", customerId);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Customer with email " + email + " not found.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public CustomerResponse readCustomer(@PathVariable String id){
        return service.readCustomer(id);
    }
}
