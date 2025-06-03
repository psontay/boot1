package com.boot1.service;

import com.boot1.dto.request.AuthenticationRequest;
import com.boot1.dto.request.IntrospectRequest;
import com.boot1.dto.response.AuthenticationResponse;
import com.boot1.dto.response.IntrospectResponse;
import com.boot1.exception.ApiException;
import com.boot1.exception.ErrorCode;
import com.boot1.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {
    UserRepository userRepository;
    @NonFinal
    @Value("${jwt.signerKey")
    protected String SIGNER_KEY;
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        var user =
                userRepository.findByUsername((request.getUsername())).orElseThrow(() -> new ApiException(
                        ErrorCode.USER_NOT_EXISTS));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if ( !authenticated ) {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }
        // generate token
        var token = generateToken(request.getUsername());
        return AuthenticationResponse.builder().token(token).success(true).build();
    }
    private String generateToken(String username) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet =
                new JWTClaimsSet.Builder().subject(username).issuer("sontaypham").issueTime(new Date()).expirationTime(new Date(
                        Instant.now().plus(1 , ChronoUnit.HOURS).toEpochMilli()
                )).build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject( jwsHeader , payload);
        try {
            jwsObject.sign( new MACSigner(SIGNER_KEY));
            return jwsObject.serialize();
        }catch(JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verifired = signedJWT.verify(verifier);
        return IntrospectResponse.builder().valid(verifired && expirationDate.after(new Date())).build();
    }
}
