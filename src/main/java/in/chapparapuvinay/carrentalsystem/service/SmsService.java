package in.chapparapuvinay.carrentalsystem.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import in.chapparapuvinay.carrentalsystem.entity.CustomerEntity;
import in.chapparapuvinay.carrentalsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")
    private String twilioPhoneNumber;

    private CustomerRepository customerRepository;

    private static final long OTP_VALID_DURATION_MINUTES = 5;
    private final Map<String, OtpData> otpCache = new ConcurrentHashMap<>();

    public void sendOtpToPhone(String phoneNumber) {
        // Initialize Twilio client
        Twilio.init(accountSid, authToken);

        // Generate a 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));

        // Store OTP in cache
        otpCache.put(phoneNumber, new OtpData(otp, Instant.now()));

        // Create the SMS message
        String messageBody = "Your verification code is: " + otp;

        try {
            Message.creator(
                    new PhoneNumber(phoneNumber), // To: The user's phone number
                    new PhoneNumber(twilioPhoneNumber), // From: Your Twilio phone number
                    messageBody
            ).create();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP SMS: " + e.getMessage());
        }
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        OtpData otpData = otpCache.get(phoneNumber);
        if (otpData == null || !otpData.getOtp().equals(otp)) {
            return false;
        }

        long elapsedTime = Duration.between(otpData.getCreationTime(), Instant.now()).toMinutes();
        if (elapsedTime >= OTP_VALID_DURATION_MINUTES) {
            otpCache.remove(phoneNumber);
            return false;
        }

        otpCache.remove(phoneNumber);
        return true;
    }

    // Inner class to hold OTP and its creation time (can be reused from EmailService)
    private static class OtpData {
        private final String otp;
        private final Instant creationTime;

        public OtpData(String otp, Instant creationTime) {
            this.otp = otp;
            this.creationTime = creationTime;
        }
        public String getOtp() { return otp; }
        public Instant getCreationTime() { return creationTime; }
    }
}