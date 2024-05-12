package com.project.shopapp.token;

import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.exceptions.PermissionDenyException;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);

    Token refreshToken(String refreshToken, User userDetails) throws PermissionDenyException, InvalidParamException;
}
