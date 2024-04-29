package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails { //implement UserDetails của security để config bên SecurityConfig
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name= "fullname", length = 100)
    private String fullName;

    @Column(name= "phone_number", length = 10, nullable = false)
    private String phoneNumber;

    @Column(name= "address", length = 200)
    private String address;

    @Column(name= "password", length = 100, nullable = false)
    private String password;

    @Column(name= "is_active", length = 1)
    private Long active;

    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;

    @Column(name= "facebook_account_id")
    private Long facebookAccountId;

    @Column(name= "google_account_id")
    private Long googleAccountId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    //Phương thức này trả về các quyền (roles) của người dùng.
    // Trong đoạn mã, nó tạo ra một SimpleGrantedAuthority từ tên vai trò của người dùng và trả về một danh sách chứa một phần tử này.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // convert qua SimpleGrantedAuthority để thêm role tương thích với GrantedAuthority
        List<SimpleGrantedAuthority> authorityList = new ArrayList<SimpleGrantedAuthority>();
        // authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorityList.add(new SimpleGrantedAuthority("ROLE_" + this.getRole().getName().toUpperCase()));
        return authorityList;
    }

    // Phương thức này trả về tên đăng nhập của người dùng.
    // Trong trường hợp này, người dùng được xác định bằng số điện thoại, nên số điện thoại được trả về.
    @Override
    public String getUsername() {
        // vì dùng phoneNumber để đăng nhập
        return this.phoneNumber;
    }

    // Phương thức này kiểm tra xem tài khoản của người dùng có hết hạn hay không.
    // Trong trường hợp này, tài khoản được coi là không bao giờ hết hạn, do đó phương thức trả về true.
    @Override
    public boolean isAccountNonExpired() {
        // account có thời lượng vô thời hạn
        return true;
    }

    // Phương thức này kiểm tra xem tài khoản của người dùng có bị khóa hay không.
    // Trong trường hợp này, tài khoản không bao giờ bị khóa, nên phương thức trả về true.
    @Override
    public boolean isAccountNonLocked() {
        // không thể khóa user
        return true;
    }

    // Phương thức này kiểm tra xem thông tin xác thực của người dùng (ví dụ: mật khẩu) có hết hạn hay không.
    // Trong trường hợp này, thông tin xác thực không bao giờ hết hạn, nên phương thức trả về true.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //  Phương thức này kiểm tra xem tài khoản của người dùng có được kích hoạt hay không.
    //  Trong trường hợp này, tài khoản được coi là được kích hoạt (không bị khóa), nên phương thức trả về true.
    @Override
    public boolean isEnabled() {
        // user có hiệu lực (không bị khóa)
        return true;
    }
}
