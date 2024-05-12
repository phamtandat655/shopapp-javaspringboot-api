package com.project.shopapp.token;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.exceptions.PermissionDenyException;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService{
    private static final int MAX_TOKENS = 3;

    private final TokenRepository tokenRepository;

    private final JwtTokenUtils jwtTokenUtils;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Transactional
    @Override
    public Token addToken(User user, String token, boolean isMobileDevice) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        int tokenCount = userTokens.size();

        // kiểm tra nếu vượt quá giới hạn thì xóa 1 token cũ
        if(tokenCount >= MAX_TOKENS) {
            // kiểm tra xem danh sách userTokens tồn tại ít nhất
            // một token ko phải của thiết bị di động (non-mobile)
            boolean hasNonMobileToken = !userTokens.stream().allMatch(Token::isMobile);
            Token tokenToDelete;
            if(hasNonMobileToken) {
                tokenToDelete = userTokens.stream()
                        .filter(userToken -> !userToken.isMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            } else {
                //tất cả token đều là thiết bị di động => xóa thằng đầu tiên
                tokenToDelete = userTokens.get(0);
            }
            tokenRepository.delete(tokenToDelete);
        }
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);

        // tạo mới token cho người dùng
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .isMobile(isMobileDevice)
                .build();

        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(newToken);

        return newToken;
    }

    @Override
    public Token refreshToken(String refreshToken, User user) throws PermissionDenyException, InvalidParamException {
        Token jwtToken = tokenRepository.findByRefreshToken(refreshToken);

        if(user.getActive() != null && user.getActive() == 0) {
            throw new PermissionDenyException("Your account is locked !");
        }

        // generage new token for thís user
        String token = jwtTokenUtils.generateToken(user);

        jwtToken.setToken(token);
        jwtToken.setExpirationDate(LocalDateTime.now().plusSeconds(expiration));

        jwtToken.setRefreshToken(UUID.randomUUID().toString());
        jwtToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(jwtToken);

        return jwtToken;
    }
}
