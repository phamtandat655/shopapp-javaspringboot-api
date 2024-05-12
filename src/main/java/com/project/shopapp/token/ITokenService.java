package com.project.shopapp.token;

import com.project.shopapp.models.User;

public interface ITokenService {
    void addToken(User user, String token, boolean isMobileDevice);
}
