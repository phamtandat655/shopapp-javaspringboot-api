package com.project.shopapp.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname", length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "address", length = 200, nullable = false)
    private String address;

    @Column(name = "note", length = 100)
    private String note;

    @Column(name = "order_date", length = 100)
    private Date orderDate;

    @Column(name = "total_money")
    private Float totalMoney;

    @Column(name = "shipping_method", length = 100)
    private String shippingMethod;

    @Column(name = "shipping_address", length = 200)
    private String shippingAddress;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;

    private Boolean active;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // dùng List<OrderDetail> nếu ko có cái này ở đây và cái @JsonBackReference thì sẽ bị nhảy vô vòng lặp vô tận
    private List<OrderDetail> orderDetails;
}
// Annotation @JsonManagedReference và @JsonBackReference được sử dụng trong Jackson,
// một thư viện JSON cho Java, để quản lý vòng lặp tham chiếu khi chuyển đổi các đối tượng
// thành định dạng JSON. Cả hai annotation này đều giúp giải quyết vấn đề vòng lặp vô hạn
// khi một đối tượng có tham chiếu đến một đối tượng khác, và đối tượng đó lại tham chiếu trở lại đối tượng ban đầu.

// @JsonManagedReference: Được sử dụng trên một thuộc tính trong lớp cha, hoặc lớp chính là chủ sở hữu của mối quan hệ.
// Khi Jackson gặp annotation này, nó sẽ thêm tên thuộc tính được định danh bởi value vào JSON output
// và tiếp tục chuyển đổi các thuộc tính của đối tượng đó.

// @JsonBackReference: Được sử dụng trên một thuộc tính trong lớp con, hoặc lớp tham chiếu đến lớp chủ sở hữu của mối quan hệ.
//  Thay vào đó, nó chỉ định rằng thuộc tính đó sẽ được bỏ qua khi chuyển đổi đối tượng thành JSON.
//  Điều này ngăn Jackson khỏi việc thử chuyển đổi vòng lặp tham chiếu bằng cách bỏ qua thuộc tính này.

// Order và OrderDetail có mối quan hệ hai chiều, trong đó Order là chủ sở hữu của mối quan hệ.
// Do đó, bạn đặt @JsonManagedReference trên thuộc tính orderDetails của Order, và @JsonBackReference trên thuộc tính order của OrderDetail.
// Điều này giúp ngăn Jackson chuyển đổi mối quan hệ thành vòng lặp và tạo ra đầu ra JSON hợp lý.