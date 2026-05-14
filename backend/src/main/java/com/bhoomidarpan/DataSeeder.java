//package com.bhoomidarpan;
//
//import com.bhoomidarpan.entity.User;
//import com.bhoomidarpan.entity.enums.Role;
//import com.bhoomidarpan.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class DataSeeder implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Create default Sub-Registrar officer
//        if (!userRepository.existsByEmail("subregistrar@bhoomidarpan.gov")) {
//            User subRegistrar = User.builder()
//                    .name("Sub-Registrar Officer")
//                    .email("subregistrar@bhoomidarpan.gov")
//                    .password(passwordEncoder.encode("subregistrar123"))
//                    .aadhaar("123456789012")
//                    .pan("SUBRS1234R")
//                    .mobile("9876543210")
//                    .role(Role.SUB_REGISTRAR)
//                    .active(true)
//                    .build();
//            userRepository.save(subRegistrar);
//            System.out.println("Created Sub-Registrar officer account");
//        }
//
//        // Create default Tehsildar officer
//        if (!userRepository.existsByEmail("tehsildar@bhoomidarpan.gov")) {
//            User tehsildar = User.builder()
//                    .name("Tehsildar Officer")
//                    .email("tehsildar@bhoomidarpan.gov")
//                    .password(passwordEncoder.encode("tehsildar123"))
//                    .aadhaar("987654321098")
//                    .pan("TEHSL1234D")
//                    .mobile("8765432109")
//                    .role(Role.TEHSILDAR)
//                    .active(true)
//                    .build();
//            userRepository.save(tehsildar);
//            System.out.println("Created Tehsildar officer account");
//        }
//
//        // Create sample user
//        if (!userRepository.existsByEmail("amit.joshi@example.com")) {
//            User user = User.builder()
//                    .name("Amit Joshi")
//                    .email("amit.joshi@example.com")
//                    .password(passwordEncoder.encode("password123"))
//                    .aadhaar("111122223333")
//                    .pan("AJTPK1234Q")
//                    .mobile("9998887776")
//                    .role(Role.USER)
//                    .active(true)
//                    .build();
//            userRepository.save(user);
//            System.out.println("Created sample user account");
//        }
//    }
//}