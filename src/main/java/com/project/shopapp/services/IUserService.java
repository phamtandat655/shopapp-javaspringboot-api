package com.project.shopapp.services;

import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;

import java.util.List;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login (String phoneNumber, String password) throws DataNotFoundException, Exception;

    User getUserDetailsFromToken(String token);

    User updateUser(Long userId, UpdateUserDTO userUpadtedDTO) throws Exception;
}
