package in.chapparapuvinay.carrentalsystem.service;

import in.chapparapuvinay.carrentalsystem.io.CarRequest;
import in.chapparapuvinay.carrentalsystem.io.CarResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CarService {
    String uploadFile(MultipartFile file);


    abstract CarResponse addCar(CarRequest request, MultipartFile file);
}
