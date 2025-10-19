package in.chapparapuvinay.carrentalsystem.service;

import in.chapparapuvinay.carrentalsystem.entity.CarEntity;
import in.chapparapuvinay.carrentalsystem.io.CarRequest;
import in.chapparapuvinay.carrentalsystem.io.CarResponse;
import in.chapparapuvinay.carrentalsystem.repository.CarRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final S3Client s3Client;
    private final CarRepository carRepository;

    @Value("${aws.s3.carsbucket}")
    private String bucketName;

    public CarServiceImpl(S3Client s3Client, CarRepository carRepository) {
        this.s3Client = s3Client;
        this.carRepository = carRepository;
    }


    @Override
    public String uploadFile(MultipartFile file){
        String filenameExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String key = UUID.randomUUID().toString()+"."+filenameExtension;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if(response.sdkHttpResponse().isSuccessful()){
                return "https://" + bucketName + ".s3." + s3Client.serviceClientConfiguration().region().id() + ".amazonaws.com/" + key;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
            }
        }catch (IOException ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during file upload");
        }
    }

    @Override
    public CarResponse addCar(CarRequest request, MultipartFile file) {
        CarEntity newCarEntity = convertToEntity(request);
        String imageUrl = uploadFile(file);
        newCarEntity.setImageUrl(imageUrl);
        newCarEntity = carRepository.save(newCarEntity);
        return convertToResponse(newCarEntity);
    }

    @Override
    public List<CarResponse> readCars() {
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        List<CarEntity> databaseEntries = carRepository.findAll(sort);
        return databaseEntries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CarResponse readCar(String id) {
        CarEntity existingCar =  carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found for the id:"+id));
        return convertToResponse(existingCar);
    }


    @Override
    public void deleteCar(String id) {
        CarResponse response = readCar(id);
        String imageUrl = response.getImageUrl();
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        boolean isFileDelete = deleteFile(filename);
        if(isFileDelete){
            carRepository.deleteById(response.getId());
        }

    }

    @Override
    public boolean deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return true;
    }


    private CarEntity convertToEntity(CarRequest request){
        return CarEntity.builder()
                .name(request.getName())
                .model(request.getModel())
                .price(request.getPrice())
                .seats(request.getSeats())
                .build();
    }

    private CarResponse convertToResponse(CarEntity entity){
        return CarResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .model(entity.getModel())
                .seats(entity.getSeats())
                .imageUrl(entity.getImageUrl())
                .build();
    }


}