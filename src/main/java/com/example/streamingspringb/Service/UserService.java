package com.example.streamingspringb.Service;

import com.example.streamingspringb.Infrastructure.UserRepository;
import com.example.streamingspringb.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private User user;

    public User login(String username, String password) {
        user = userRepository.checkUser(username);
        if (user != null && BCrypt.checkpw(password, user.getPassword()) ) {
            return user;
        }
        return null;
    }
    public boolean updateUser(User user) {
        if (user != null && !user.getPassword().isEmpty()) {
            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashed);
            return userRepository.updateUser(user);
        }
        else if(user != null && user.getPassword().isEmpty()) {
            return userRepository.updateUserNoPass(user);
        }
        else return false;
    }
    public User registerUser(User user) {
        if (user != null) {
            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashed);
            return userRepository.registerUser(user);
        }
        return null;
    }
    public List<User> getAllUsers(){
        return userRepository.getAllUsers();
    }
    public boolean deleteUser(int id) {
        return userRepository.deleteUser(id);
    }
    public boolean giveAdminRights(int id) {
        return userRepository.giveAdminRights(id);
    }
    public boolean removeAdminRights(int id) {
        return userRepository.removeAdminRights(id);
    }
    public User getUser(int id) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
    public List<User> getUsersPage(int page, int pageSize) {
        return userRepository.getUserPages(page, pageSize);
    }
    public List<User> searchForUser(String query) {
        List<User> users = userRepository.getAllUsers();
        List<User> filteredUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getName().toLowerCase().contains(query.toLowerCase()) || user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }
}
