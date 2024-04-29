package com.project.shopapp.services;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.PermissionDenyException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        // ktra sdt đã tồn tại
        if(userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists !");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found !"));
        if(role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new PermissionDenyException("You cannot register an admin account !");
        }

        // convert userDTO to user
        User newUser = User.builder()
             .fullName(userDTO.getFullName())
             .phoneNumber(userDTO.getPhoneNumber())
             .address(userDTO.getAddress())
             .password(userDTO.getPassword())
             .dateOfBirth(userDTO.getDateOfBirth())
             .facebookAccountId(userDTO.getFacebookAccountId())
             .googleAccountId(userDTO.getGoogleAccountId())
             .build();

        newUser.setRole(role);

        // kiểm tra nếu có account id thì ko cần mật khẩu
        if(userDTO.getGoogleAccountId() == 0 && userDTO.getFacebookAccountId() == 0) {
            String password = userDTO.getPassword();
            // mã hóa
            String encodePassword = passwordEncoder.encode(password);
            newUser.setPassword(encodePassword);
            // trong phần spring security
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException("Invalid phonenumber or password !");
        }
        User existingUser = optionalUser.get();
        // check password
        // chỉ kiểm tra khi ko đăng nhập bằng gg or fb
        if (existingUser.getFacebookAccountId() == 0 && existingUser.getGoogleAccountId() == 0) {
            if(!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Wrond phonenumber or password !");
            }
        }

        // authenticate with java spring security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password, existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);

        return jwtTokenUtils.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) {
        if(jwtTokenUtils.isTokenExpired(token)) {
            throw new DateTimeException("Token is expired !");
        }

        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);

        return userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DateTimeException("Could not find user with phone number " + phoneNumber));
    }

    @Override
    public User updateUser(Long userId, UpdateUserDTO userUpadtedDTO) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Could not find user with id " + userId));

        String phoneNumber = userUpadtedDTO.getPhoneNumber();
        if(!existingUser.getPhoneNumber().equals(phoneNumber) && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists !");
        }

        // Kiểm tra từng thuộc tính của userUpadtedDTO và chỉ set cho existingUser nếu giá trị không phải null
        if (userUpadtedDTO.getFullName() != null) {
            existingUser.setFullName(userUpadtedDTO.getFullName());
        }
        if (userUpadtedDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userUpadtedDTO.getPhoneNumber());
        }
        if (userUpadtedDTO.getAddress() != null) {
            existingUser.setAddress(userUpadtedDTO.getAddress());
        }
        if (userUpadtedDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userUpadtedDTO.getDateOfBirth());
        }
        if (userUpadtedDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(userUpadtedDTO.getFacebookAccountId());
        }
        if (userUpadtedDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(userUpadtedDTO.getGoogleAccountId());
        }

        // encode password before updating
        if(userUpadtedDTO.getPassword() != null && !userUpadtedDTO.getPassword().isEmpty()) {
            if(userUpadtedDTO.getRetypePassword() == null || !userUpadtedDTO.getPassword().equals(userUpadtedDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same !");
            }

            String newPassword = userUpadtedDTO.getPassword();
            String encodeNewPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodeNewPassword);
        }

        return userRepository.save(existingUser);
    }
}
