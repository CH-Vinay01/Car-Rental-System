package in.chapparapuvinay.carrentalsystem.service;

import in.chapparapuvinay.carrentalsystem.entity.CarEntity;
import in.chapparapuvinay.carrentalsystem.entity.CustomerEntity;
import in.chapparapuvinay.carrentalsystem.io.CarRequest;
import in.chapparapuvinay.carrentalsystem.io.CarResponse;
import in.chapparapuvinay.carrentalsystem.io.CustomerRequest;
import in.chapparapuvinay.carrentalsystem.io.CustomerResponse;
import in.chapparapuvinay.carrentalsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService{
    private final S3Client s3Client;
    private final CustomerRepository customerRepository;

    @Value("{aws.s3.dlbucket}")
    private String dlBucket;
    @Value("{aws.s3.dpbucket}")
    private String dpBucket;

    public CustomerServiceImpl(S3Client s3Client, CustomerRepository customerRepository) {
        this.s3Client = s3Client;
        this.customerRepository = customerRepository;
    }


    @Override
    public String uploadDLFile(MultipartFile file){
        String filenameExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String key = UUID.randomUUID().toString()+"."+filenameExtension;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(dlBucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if(response.sdkHttpResponse().isSuccessful()){
                return "https://" + dlBucket + ".s3." + s3Client.serviceClientConfiguration().region().id() + ".amazonaws.com/" + key;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
            }
        }catch (IOException ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during file upload");
        }
    }

    @Override
    public String uploadDPFile(MultipartFile file){
        String filenameExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String key = UUID.randomUUID().toString()+"."+filenameExtension;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(dpBucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if(response.sdkHttpResponse().isSuccessful()){
                return "https://" + dpBucket + ".s3." + s3Client.serviceClientConfiguration().region().id() + ".amazonaws.com/" + key;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
            }
        }catch (IOException ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during file upload");
        }
    }

    @Override
    public CustomerResponse createNewUser(CustomerRequest request, MultipartFile dlFile, MultipartFile dpFile) {
        CustomerEntity customerEntity = convertToEntity(request);
        String dl = uploadDLFile(dlFile);
        String dp = uploadDPFile(dpFile);
        customerEntity.setImage(dp);
        customerEntity.setDlURL(dl);
        customerEntity = customerRepository.save(customerEntity);
        return convertToResponse(customerEntity);
    }


    private CustomerEntity convertToEntity(CustomerRequest request){
        return CustomerEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dob(request.getDob())
                .email(request.getEmail())
                .password(request.getPassword())
                .aadharno(request.getAadharno())
                .phno(request.getPhno())
                .build();
    }

    private CustomerResponse convertToResponse(CustomerEntity entity){
        return CustomerResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .dob(entity.getDob())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .aadharno(entity.getAadharno())
                .phno(entity.getPhno())
                .dlURL(entity.getDlURL())
                .image(entity.getImage())
                .build();
    }

}
