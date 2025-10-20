package in.chapparapuvinay.carrentalsystem.service;

import in.chapparapuvinay.carrentalsystem.entity.CustomerEntity;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService{
    private final S3Client s3Client;
    private final CustomerRepository customerRepository;

    @Value("${aws.s3.dlbucket}")
    private String dlBucket;
    @Value("${aws.s3.dpbucket}")
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

    @Override
    public boolean verifyUser(String email, String password) {
        Optional<CustomerEntity> foundCustomerOptional = customerRepository.findByEmail(email);
        if (foundCustomerOptional.isPresent()) {
            CustomerEntity foundCustomer = foundCustomerOptional.get();
            return foundCustomer.getPassword().equals(password);
        }
        return false;
    }

    @Override
    public CustomerResponse readCustomer(String id) {
        CustomerEntity existingCustomer =  customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found for the id:"+id));
        return convertToResponse(existingCustomer);
    }

    @Override
    public String getId(String email) {
        Optional<CustomerEntity> customerOptional = customerRepository.findByEmail(email);
        return customerOptional.map(CustomerEntity::getId).orElse(null);
    }

    public String getIdByPhoneNo(String phoneNo) {
        Optional<CustomerEntity> customerOptional = customerRepository.findByPhno(phoneNo);

        // Check if a customer was found
        if (customerOptional.isPresent()) {
            // If found, get the customer object
            CustomerEntity customer = customerOptional.get();
            // Return the ID, converted to a String
            return customer.getId().toString();
        } else {
            // If no customer was found, throw a "Not Found" exception
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with phone number " + phoneNo + " not found.");
        }
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
