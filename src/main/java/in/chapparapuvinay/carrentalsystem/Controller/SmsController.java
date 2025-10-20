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
    private final SmsService smsService;

    @Autowired
    private final CustomerService service;

    @Autowired
    public SmsController(SmsService smsService, CustomerService service) {
        this.smsService = smsService;
        this.service = service;
    }

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
    @PostMapping("/verify/login")
    public ResponseEntity<Map<String, Object>> verifySmsOtp(@RequestBody SmsVerifyRequest verifyRequest) {
        Map<String, Object> response = new HashMap<>();

        // --- 1. Validation (No changes here) ---
        if (verifyRequest.getPhoneNumber() == null || verifyRequest.getPhoneNumber().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Phone number is required.");
            response.put("customerId", null);
            return ResponseEntity.badRequest().body(response);
        }
        if (verifyRequest.getOtp() == null || verifyRequest.getOtp().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "OTP is required.");
            response.put("customerId", null);
            return ResponseEntity.badRequest().body(response);
        }

        // --- 2. Business Logic ---
        boolean isOtpValid = smsService.verifyOtp(
                verifyRequest.getPhoneNumber(),
                verifyRequest.getOtp()
        );

        // --- 3. Response Generation (Updated Logic) ---
        if (isOtpValid) {
            String customerId = service.getIdByPhoneNo(verifyRequest.getPhoneNumber());

            // **CRITICAL CHECK ADDED HERE**
            // Ensure customerId is not null or blank before confirming success.
            if (customerId != null && !customerId.trim().isEmpty()) {
                // Happy path: OTP is valid AND we found the user.
                response.put("success", true);
                response.put("message", "Phone number verified successfully.");
                response.put("customerId", customerId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Login failed. Please contact support.");
                response.put("customerId", null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            // Invalid OTP path (No changes here)
            response.put("success", false);
            response.put("message", "Invalid or expired OTP.");
            response.put("customerId", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}