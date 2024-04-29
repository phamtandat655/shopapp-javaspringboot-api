package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("fullname") // để khi người dùng gửi request lên là chữ n trong name viết thường
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    private Long facebookAccountId;

    @JsonProperty("google_account_id")
    private Long googleAccountId;

    @JsonProperty("role_id")
    private Long roleId;

    public static UserResponse fromUserToUserResponse(User user) {
        return UserResponse.builder()
               .fullName(user.getFullName())
               .phoneNumber(user.getPhoneNumber())
               .address(user.getAddress())
               .dateOfBirth(user.getDateOfBirth())
               .facebookAccountId(user.getFacebookAccountId())
               .googleAccountId(user.getGoogleAccountId())
               .roleId(user.getRole().getId())
               .build();
    }
}
