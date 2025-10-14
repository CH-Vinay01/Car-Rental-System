package in.chapparapuvinay.carrentalsystem.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.chapparapuvinay.carrentalsystem.io.CarRequest;
import in.chapparapuvinay.carrentalsystem.io.CarResponse;
import in.chapparapuvinay.carrentalsystem.service.CarService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/cars")
@AllArgsConstructor
public class CarController {

    private CarService service;

    @PostMapping
    public CarResponse addCar(@RequestPart("Car") String carString,
                              @RequestPart("file") MultipartFile file){

        ObjectMapper objectMapper = new ObjectMapper();
        CarRequest request;

        try {
            request = objectMapper.readValue(carString, CarRequest.class);
        }catch (JsonProcessingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON Format for 'Car' part: " + ex.getMessage());
        }
        CarResponse response = service.addCar(request,file);
        return response;
    }
    @GetMapping
    public List<CarResponse> readCars(){
        return service.readCars();
    }

    @GetMapping("/{id}")
    public CarResponse readCar(@PathVariable String id){
        return service.readCar(id);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable String id){
        service.deleteCar(id);
    }
}