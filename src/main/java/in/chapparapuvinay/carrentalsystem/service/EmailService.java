package in.chapparapuvinay.carrentalsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import the @Value annotation
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender gmailSender;

    // --- CHANGE 1: Inject the sender email from application.properties ---
    @Value("${spring.mail.username}")
    private String senderEmail;

    private static final long OTP_VALID_DURATION_MINUTES = 5;

    private final Map<String, OtpData> otpCache = new ConcurrentHashMap<>();

    public void sendOtpToEmail(String email) {
        String otp = String.format("%05d", new Random().nextInt(100000));
        Instant otpCreationTime = Instant.now();
        otpCache.put(email, new OtpData(otp, otpCreationTime));

        SimpleMailMessage message = new SimpleMailMessage();

        // --- CHANGE 2: Use the injected email variable ---
        message.setFrom(senderEmail);

        message.setTo(email);
        message.setSubject("Your Verification Code");
        message.setText("Your OTP for verification is: " + otp);

        gmailSender.send(message);
    }

    public boolean verifyOtp(String email, String otp) {
        OtpData otpData = otpCache.get(email);
        if (otpData == null || !otpData.getOtp().equals(otp)) {
            return false;
        }

        long elapsedTime = Duration.between(otpData.getCreationTime(), Instant.now()).toMinutes();
        if (elapsedTime >= OTP_VALID_DURATION_MINUTES) {
            otpCache.remove(email);
            return false;
        }

        otpCache.remove(email);
        return true;
    }

    // Inner class to hold OTP and its creation time
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