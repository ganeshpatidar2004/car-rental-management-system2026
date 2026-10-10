package com.carrental.service;

import com.carrental.dto.AuthResponse;
import com.carrental.dto.LoginRequest;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UserResponse;

/**
 * Service contract for user registration and authentication.
 */
public interface AuthService {

    /**
     * Registers a new customer in the system.
     * Public registrations are strictly assigned the CUSTOMER role.
     *
     * @param request registration input
     * @return safe UserResponse without credentials
     */
    UserResponse register(RegisterRequest request);

    /**
     * Authenticates credentials and issues a signed JWT token.
     *
     * @param request login input
     * @return AuthResponse containing token and user details
     */
    AuthResponse login(LoginRequest request);
}
