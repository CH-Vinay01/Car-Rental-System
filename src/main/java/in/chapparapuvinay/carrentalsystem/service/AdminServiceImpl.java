package in.chapparapuvinay.carrentalsystem.service;

import in.chapparapuvinay.carrentalsystem.entity.AdminEntity;
import in.chapparapuvinay.carrentalsystem.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }


    @Override
    public AdminEntity addAdmin(AdminEntity admin) {
        boolean isAdminAdded = false;
        adminRepository.save(admin);
        System.out.println("Admin Added Successfully");
        return admin;
    }

    @Override
    public boolean verifyUser(String username, String password) {
        Optional<AdminEntity> foundAdminOptional = adminRepository.findByUsername(username);
        if (foundAdminOptional.isPresent()) {
            AdminEntity foundAdmin = foundAdminOptional.get();
            return foundAdmin.getPassword().equals(password);
        }
        return false;
    }

    @Override
    public AdminEntity addUser(String username, String password) {
        AdminEntity newEntity = new AdminEntity();
        newEntity.setUsername(username);
        newEntity.setPassword(password);
        AdminEntity savedEntity = adminRepository.save(newEntity);
        return savedEntity;
    }


}
