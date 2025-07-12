package com.boot1.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.boot1.dto.request.AuthenticationRequest;
import com.boot1.dto.request.IntrospectRequest;
import com.boot1.dto.request.LogoutRequest;
import com.boot1.dto.request.RefreshRequest;
import com.boot1.dto.response.ApiResponse;
import com.boot1.dto.response.AuthenticationResponse;
import com.boot1.dto.response.IntrospectResponse;
import com.boot1.dto.response.RefreshResponse;
import com.boot1.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    private final RestClient.Builder builder;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        var ans = authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder().result(ans).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var ans = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(ans).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/refresh")
    ApiResponse<RefreshResponse> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var ans = authenticationService.refreshToken(request);
        return ApiResponse.<RefreshResponse>builder().result(ans).build();
    }
}
