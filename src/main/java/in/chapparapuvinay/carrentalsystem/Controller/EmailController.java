package in.chapparapuvinay.carrentalsystem.Controller;

import in.chapparapuvinay.carrentalsystem.io.OtpRequest;
import in.chapparapuvinay.carrentalsystem.io.OtpVerifyRequest;
import in.chapparapuvinay.carrentalsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // Endpoint to send an OTP to a user's email
    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest otpRequest) {
        try {
            emailService.sendOtpToEmail(otpRequest.getEmail());
            return ResponseEntity.ok("OTP sent successfully to " + otpRequest.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending OTP: " + e.getMessage());
        }
    }

    // Endpoint to verify the OTP submitted by the user
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequest verifyRequest) {
        boolean isOtpValid = emailService.verifyOtp(
                verifyRequest.getEmail(),
                verifyRequest.getOtp()
        );

        if (isOtpValid) {
            return ResponseEntity.ok("OTP is valid.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP.");
        }
    }
}