package in.chapparapuvinay.carrentalsystem.Controller;

import in.chapparapuvinay.carrentalsystem.io.SmsRequest;
import in.chapparapuvinay.carrentalsystem.io.SmsVerifyRequest;
import in.chapparapuvinay.carrentalsystem.service.CustomerService;
import in.chapparapuvinay.carrentalsystem.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;
    private CustomerService service;

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestBody SmsRequest smsRequest) {
        // IMPORTANT: Ensure the phone number from the client includes the country code (e.g., +919876543210)
        if (smsRequest.getPhoneNumber() == null || smsRequest.getPhoneNumber().isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number is required.");
        }
        try {
            smsService.sendOtpToPhone(smsRequest.getPhoneNumber());
            return ResponseEntity.ok("OTP sent successfully to " + smsRequest.getPhoneNumber());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending OTP: " + e.getMessage());
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody SmsVerifyRequest verifyRequest) {
        if (verifyRequest.getPhoneNumber() == null || verifyRequest.getPhoneNumber().isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number is required.");
        }
        if (verifyRequest.getOtp() == null || verifyRequest.getOtp().isEmpty()) {
            return ResponseEntity.badRequest().body("OTP is required.");
        }

        boolean isOtpValid = smsService.verifyOtp(
                verifyRequest.getPhoneNumber(),
                verifyRequest.getOtp()
        );

        if (isOtpValid) {
            return ResponseEntity.ok("Phone number verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP.");
        }
    }

//    @PostMapping("/verify/login")
//    public ResponseEntity<Map<String, Object>> verifySmsOtp(@RequestBody SmsVerifyRequest verifyRequest) {
//        // Use a single map for the response body
//        Map<String, Object> response = new HashMap<>();
//
//        // --- Validation Checks ---
//        if (verifyRequest.getPhoneNumber() == null || verifyRequest.getPhoneNumber().trim().isEmpty()) {
//            response.put("success", false);
//            response.put("message", "Phone number is required.");
//            // Return the map as the body of the bad request
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        if (verifyRequest.getOtp() == null || verifyRequest.getOtp().trim().isEmpty()) {
//            response.put("success", false);
//            response.put("message", "OTP is required.");
//            // Return the map as the body of the bad request
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // --- Business Logic ---
//        boolean isOtpValid = smsService.verifyOtp(
//                verifyRequest.getPhoneNumber(),
//                verifyRequest.getOtp()
//        );
//
//        // --- Response Generation ---
//        if (isOtpValid) {
//            response.put("success", true);
//            response.put("message", "Phone number verified successfully.");
//            // Return the map as the body of the OK response
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "Invalid or expired OTP.");
//            // Return the map as the body of the bad request with a specific status
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }
}