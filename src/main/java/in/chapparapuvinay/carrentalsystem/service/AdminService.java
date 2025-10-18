package in.chapparapuvinay.carrentalsystem.service;

import in.chapparapuvinay.carrentalsystem.entity.AdminEntity;

public interface AdminService {
    AdminEntity addAdmin(AdminEntity admin);
    boolean verifyUser(String username, String password);
    AdminEntity addUser(String username, String password);
}
