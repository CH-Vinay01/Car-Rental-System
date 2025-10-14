package in.chapparapuvinay.carrentalsystem.repository;

import in.chapparapuvinay.carrentalsystem.entity.AdminEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface AdminRepository extends MongoRepository<AdminEntity,String> {

    Optional<AdminEntity> findByUsername(String username);
}
