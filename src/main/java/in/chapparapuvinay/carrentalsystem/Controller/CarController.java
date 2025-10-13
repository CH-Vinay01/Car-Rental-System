package in.chapparapuvinay.carrentalsystem.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
// Removed unused import: in.chapparapuvinay.carrentalsystem.entity.CarEntity;
import in.chapparapuvinay.carrentalsystem.io.CarRequest;
import in.chapparapuvinay.carrentalsystem.io.CarResponse;
import in.chapparapuvinay.carrentalsystem.service.CarService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
// Removed unused import: org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/cars")
@AllArgsConstructor
public class CarController {

    private CarService service;

    @PostMapping
    public CarResponse addCar(@RequestPart("Car") String carString,
                              @RequestPart("file") MultipartFile file){

        ObjectMapper objectMapper = new ObjectMapper();
        CarRequest request; // Changed declaration to fix the scope issue

        try {
            // ⭐ CRITICAL FIX: The result of readValue MUST be assigned to 'request'
            request = objectMapper.readValue(carString, CarRequest.class);
        }catch (JsonProcessingException ex){
            // It's good practice to include the message in the exception for debugging
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON Format for 'Car' part: " + ex.getMessage());
        }

        // 'request' is now guaranteed to be non-null if the try block succeeded.
        CarResponse response = service.addCar(request,file);
        return response;
    }
}