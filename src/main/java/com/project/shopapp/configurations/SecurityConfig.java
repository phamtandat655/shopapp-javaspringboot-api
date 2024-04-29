package com.project.shopapp.configurations;

import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    // user's details object
    // khi bật app lên sẽ khởi tạo 1 đối tượng UserDetails (là cái User trong model implement UserDetails)
    // Phương thức này tạo và trả về một UserDetailsService.
    // Trong trường hợp này, nó nhận số điện thoại của người dùng làm đầu vào và sử dụng UserRepository để tìm kiếm người dùng tương ứng.
    // Nếu không tìm thấy người dùng, nó sẽ ném một UsernameNotFoundException.
    @Bean
    public UserDetailsService userDetaillsService() {
        return phoneNumber -> userRepository
                    .findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with phone number " + phoneNumber));
    };

    // đối tượng để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        // nếu tự viết mã hóa của riêng thì dùng new PasswordEncoder(...)
        // trong này sẽ kiểm tra mật khẩu như thế nào là ổn, mã hóa ra sao
        // encode là hàm mã hóa
        // matches là hàm kiểm tra
//        return new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence rawPassword) {
//                return null;
//            }
//
//            @Override
//            public boolean matches(CharSequence rawPassword, String encodedPassword) {
//                return false;
//            }
//        };

        // còn cái này đã được set up sẵn
        // trong đây đã thực thi các method của PasswordEncoder sẵn rồi
        // BCryptPasswordEncoder là một trong những PasswordEncoder được cung cấp sẵn trong Spring Security.
        return new BCryptPasswordEncoder();
    }

    // cần UserDeatils và PasswordEncoder để tạo ra AuthenticationProvider
    // Phương thức này tạo và trả về một AuthenticationProvider.
    // Trong trường hợp này, nó sử dụng DaoAuthenticationProvider.
    // Đây là một cách để cung cấp thông tin xác thực từ một UserDetailsService và sử dụng một PasswordEncoder để kiểm tra mật khẩu.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetaillsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Phương thức này tạo và trả về một AuthenticationManager.
    // Trong trường hợp này, nó sử dụng AuthenticationConfiguration để lấy AuthenticationManager từ cấu hình.
    // Điều này cho phép AuthenticationManager được sử dụng trong ứng dụng.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
