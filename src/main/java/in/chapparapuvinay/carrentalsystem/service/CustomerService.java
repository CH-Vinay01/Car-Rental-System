package in.chapparapuvinay.carrentalsystem.service;

import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    String uploadDLFile(MultipartFile dlfile);
    String uploadDPFile(MultipartFile dpfile);
}
