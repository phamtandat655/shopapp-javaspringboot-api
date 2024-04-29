package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @JsonProperty("user_id")
    @Min(value = 1, message = "Id must be greater than 0")
    @NotNull(message = "UserId is required")
    private Long userId;

    @JsonProperty("fullname")
    private String fullName;
    private String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "PhoneNumber is required")
    @Min(value = 5, message = "PhoneNumber must be >= 5")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    private String note;

    @JsonProperty("order_date")
    private Date orderDate;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total must be > 0")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;

    //    @JsonProperty("tracking_number")
//    private String trachkingNumber;
}

