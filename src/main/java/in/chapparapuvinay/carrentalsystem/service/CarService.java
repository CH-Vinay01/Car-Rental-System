package in.chapparapuvinay.carrentalsystem.service;

import in.chapparapuvinay.carrentalsystem.io.CarRequest;
import in.chapparapuvinay.carrentalsystem.io.CarResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarService {
    String uploadFile(MultipartFile file);

    CarResponse addCar(CarRequest request, MultipartFile file);

    List<CarResponse> readCars();

    CarResponse readCar(String id);

    void deleteCar(String id);

     boolean deleteFile(String fileName);

}
