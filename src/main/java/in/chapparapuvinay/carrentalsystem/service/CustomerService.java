package in.chapparapuvinay.carrentalsystem.service;

import in.chapparapuvinay.carrentalsystem.io.CustomerRequest;
import in.chapparapuvinay.carrentalsystem.io.CustomerResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    String uploadDLFile(MultipartFile dlfile);
    String uploadDPFile(MultipartFile dpfile);

    CustomerResponse createNewUser(CustomerRequest request, MultipartFile dl, MultipartFile dp);
}
