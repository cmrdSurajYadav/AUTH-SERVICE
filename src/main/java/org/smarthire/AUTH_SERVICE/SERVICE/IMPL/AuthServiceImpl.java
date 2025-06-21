// File: AuthServiceImpl.java
package org.smarthire.AUTH_SERVICE.SERVICE.IMPL;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarthire.AUTH_SERVICE.DTO.*;
import org.smarthire.AUTH_SERVICE.EXCEPTION.CommonMessageExeception;
import org.smarthire.AUTH_SERVICE.EXCEPTION.EmailAlreadyExistsException;
import org.smarthire.AUTH_SERVICE.EXCEPTION.PhoneNumberAlreadyExistsException;
import org.smarthire.AUTH_SERVICE.EXCEPTION.UserNotFoundException;
import org.smarthire.AUTH_SERVICE.MODELS.OtpDB;
import org.smarthire.AUTH_SERVICE.MODELS.Password;
import org.smarthire.AUTH_SERVICE.MODELS.Role;
import org.smarthire.AUTH_SERVICE.MODELS.User;
import org.smarthire.AUTH_SERVICE.REPOSITORY.OtpRepository;
import org.smarthire.AUTH_SERVICE.REPOSITORY.PasswordRepository;
import org.smarthire.AUTH_SERVICE.REPOSITORY.RoleRepository;
import org.smarthire.AUTH_SERVICE.REPOSITORY.UserRepository;
import org.smarthire.AUTH_SERVICE.SECURITY.CustomUserDetailsService;
import org.smarthire.AUTH_SERVICE.SECURITY.JwtTokenProvider;
import org.smarthire.AUTH_SERVICE.SERVICE.AuthService;
import org.smarthire.AUTH_SERVICE.SERVICE.EmailService;
import org.smarthire.AUTH_SERVICE.SPECIFICATIONS.CommonSpecifications;
import org.smarthire.AUTH_SERVICE.UTILS.RandomNumberGenerator;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordRepository passwordRepository;
    private final EmailService emailService;
    private final RandomNumberGenerator randomNumberGenerator;
    private final OtpRepository otpRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final CommonSpecifications commonSpecifications;




    @Override
    @Transactional
    public UserDTO registerUser(RegisterRequest registerRequest) {

        if (userRepository.existsByEmailIgnoreCase(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email '" + registerRequest.getEmail() + "' is already taken!");
        }

        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException("Phone number '" + registerRequest.getPhoneNumber() + "' is already taken!");
        }

        // Fetch or create role
        String desiredRoleName = "ROLE_" + registerRequest.getRole().toUpperCase();
        Role role = roleRepository.findByRoleName(desiredRoleName);

        if (role == null) {
            role = roleRepository.save(Role.builder().roleName(desiredRoleName).build());
        }

        // Encode password before saving
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Save password entity first
        Password password = passwordRepository.save(
                Password.builder()
                        .hashPassword(encodedPassword)
                        .build()
        );

        // Save user with password and role
        User user = User.builder()
                .email(registerRequest.getEmail())
                .phoneNumber(registerRequest.getPhoneNumber())
                .enable(true)
                .role(role)
                .password(encodedPassword)
                .build();

        User saved =  userRepository.save(user);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(saved.getEmail());
        String token = jwtTokenProvider.generateTokenFromUserDetails(userDetails);

        return UserDTO.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .phoneNumber(saved.getPhoneNumber())
                .enable(saved.getEnable())
                .role(saved.getRole())
                .jwtAuthenticationResponse(new JwtAuthenticationResponse(token))
                .createdAt(saved.getCreatedAt())
                .updatedTime(saved.getUpdatedTime())
                .build();
    }


    @Override
    public JwtAuthenticationResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String key = forgotPasswordRequest.getKey();
        Optional<User> user = key.contains("@") ? userRepository.findByEmail(key) : userRepository.findByPhoneNumber(key);
        Long otp = randomNumberGenerator.generateOtp();
        List<OtpDB> otpDBList = otpRepository.findByUserId(user.orElseThrow().getId());
        otpRepository.deleteAll(otpDBList);
        otpRepository.save(OtpDB.builder().userId(user.orElseThrow().getId()).otp(otp).email(key).build());


        String content = "<H2> Your otp is "+ otp +" for reset your password<H2>";
        emailService.sendEmail(key,"OTP for reset password",content,true);

        return ForgotPasswordResponse.builder().mes("Your otp is send to "+key).build();
    }

    @Transactional
    @Override
    public OtpResponseDTO checkOTPMatch(OTPVerifyRequestDto otpVerifyRequestDto) {
        Specification<OtpDB> spec = Specification.where(
                CommonSpecifications.fieldEquals("userId", otpVerifyRequestDto.getUserId())

        );
        Sort sort = CommonSpecifications.sortByDesc("createdAt");
        List<OtpDB> all = otpRepository.findAll(spec, sort);
        if (all.isEmpty()) return OtpResponseDTO.builder().otpMatch(false).message("no otp found").build();
        OtpDB OtpDb = all.get(0);
        Long userId = OtpDb.getUserId();
        Long otp = OtpDb.getOtp();
        long between = Duration.between(OtpDb.getCreatedAt(), LocalDateTime.now()).toMinutes();
        if(between>3) return OtpResponseDTO.builder().otpMatch(false).message("OTP expired").build();
        boolean success = Objects.equals(otp,otpVerifyRequestDto.getOTP());
        if(success) {
          User user = userRepository.findById(userId).orElseThrow();
          user.setLastOtpVerifyTime(LocalDateTime.now());
          userRepository.save(user);
        }
      //  boolean success = otpRepository.findByUserIdAndOtp(otpVerifyRequestDto.getUserId(), otpVerifyRequestDto.getOTP())!=null && otpRepository.findByUserIdAndOtp(otpVerifyRequestDto.getUserId(), otpVerifyRequestDto.getOTP()).getCreatedAt().plusMinutes(2).isAfter(LocalDateTime.now());
        return OtpResponseDTO.builder().otpMatch(success).message(success?"Otp Verified":"Not is incorrect").build();
    }

    @Override
    public CreatePasswordResponse resetPassword(CreatePasswordRequest createPasswordRequest)  {
        User user = userRepository.findByEmail(createPasswordRequest.getEmail()).orElseThrow(()->new UserNotFoundException("user not found with "+createPasswordRequest.getEmail()));

        if(user.getLastOtpVerifyTime()==null){
            throw new CommonMessageExeception("Please verify your OTP first");
        }
        if(user.getLastOtpVerifyTime().plusMinutes(5).isBefore(LocalDateTime.now())){
            throw new CommonMessageExeception("Your reset password link expire re verify the otp");
        }
        user.setLastOtpVerifyTime(null);

        if(!Objects.equals(createPasswordRequest.getNewPassword(),createPasswordRequest.getConfirmPassword())){
            throw new CommonMessageExeception("Password not match");
        }
        List<Password> passwords = passwordRepository.findByUserId(user.getId());

        for (Password password : passwords) {
            if (passwordEncoder.matches(createPasswordRequest.getConfirmPassword(), password.getHashPassword())) {
                throw new CommonMessageExeception("This is your old password. Please choose a new one.");
            }
        }



        String encodedPassword = passwordEncoder.encode(createPasswordRequest.getConfirmPassword());
        user.setPassword(encodedPassword);
        user.setNoOfattamp(user.getNoOfattamp()+1);

        passwordRepository.save(Password.builder().userId(user.getId()).hashPassword(encodedPassword).build());

        return CreatePasswordResponse
                .builder()
                .noOfAttampt(user.getNoOfattamp())
                .success(true)
                .email(user.getEmail())
                .message("Password reset successfully")
                .build();

    }


}
