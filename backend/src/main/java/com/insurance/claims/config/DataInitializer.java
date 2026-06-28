package com.insurance.claims.config;

import com.insurance.claims.model.Policy;
import com.insurance.claims.model.User;
import com.insurance.claims.repository.PolicyRepository;
import com.insurance.claims.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UserRepository userRepository;
    @Autowired private PolicyRepository policyRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        User ph1 = createUser("John", "Doe", "john.doe@insurance.com", "password123", User.Role.POLICYHOLDER, "9876543210");
        User ph2 = createUser("Jane", "Smith", "jane.smith@insurance.com", "password123", User.Role.POLICYHOLDER, "9876543211");
        User adjuster = createUser("David", "Kumar", "adjuster@insurance.com", "admin123", User.Role.ADJUSTER, "9876543212");
        User manager = createUser("Priya", "Mehta", "manager@insurance.com", "admin123", User.Role.MANAGER, "9876543213");
        createUser("Admin", "User", "admin@insurance.com", "admin123", User.Role.ADMIN, "9876543214");

        createPolicy("POL-2019-001", ph1, Policy.PolicyType.AUTO, new BigDecimal("500000"), new BigDecimal("12000"));
        createPolicy("POL-2019-002", ph1, Policy.PolicyType.HOME, new BigDecimal("2000000"), new BigDecimal("25000"));
        createPolicy("POL-2019-003", ph2, Policy.PolicyType.HEALTH, new BigDecimal("300000"), new BigDecimal("18000"));
        createPolicy("POL-2019-004", ph2, Policy.PolicyType.TRAVEL, new BigDecimal("100000"), new BigDecimal("5000"));

        logger.info("Demo data seeded: 5 users, 4 policies");
    }

    private User createUser(String first, String last, String email, String password, User.Role role, String phone) {
        User u = new User();
        u.setFirstName(first);
        u.setLastName(last);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role);
        u.setPhone(phone);
        u.setIsActive(true);
        return userRepository.save(u);
    }

    private void createPolicy(String number, User owner, Policy.PolicyType type,
                               BigDecimal sumInsured, BigDecimal premium) {
        Policy p = new Policy();
        p.setPolicyNumber(number);
        p.setPolicyholder(owner);
        p.setPolicyType(type);
        p.setSumInsured(sumInsured);
        p.setPremiumAmount(premium);
        p.setStartDate(LocalDate.of(2019, 1, 1));
        p.setEndDate(LocalDate.of(2020, 12, 31));
        p.setStatus(Policy.PolicyStatus.ACTIVE);
        policyRepository.save(p);
    }
}
