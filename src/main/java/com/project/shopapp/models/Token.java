package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="token", nullable = false, length = 255)
    private String token;

    @Column(name="token_type", nullable = false, length = 50)
    private String tokenType;

    @Column(name="expiration_date")
    private LocalDateTime expirationDate;

    private boolean revoked;
    private boolean expired;

    private boolean isMobile;

    @Column(name="refresh_token", nullable = false, length = 255)
    private String refreshToken;

    @Column(name="refresh_expiration")
    private LocalDateTime refreshExpirationDate;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
}
