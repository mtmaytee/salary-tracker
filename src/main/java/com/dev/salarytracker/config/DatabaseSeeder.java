package com.dev.salarytracker.config;

import com.dev.salarytracker.entity.TaxBracket;
import com.dev.salarytracker.entity.TaxMasterData;
import com.dev.salarytracker.entity.Users;
import com.dev.salarytracker.repository.TaxBracketRepository;
import com.dev.salarytracker.repository.TaxMasterDataRepository;
import com.dev.salarytracker.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TaxMasterDataRepository taxMasterDataRepository;

    @Autowired
    private TaxBracketRepository taxBracketRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking if database seeding is required...");
        seedAdminUser();
        seedTaxMasterData();
        seedTaxBrackets();
    }

    private void seedAdminUser() {
        if (usersRepository.count() == 0) {
            log.info("Seeding default admin user...");
            Users admin = new Users();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin1234")); // Default password
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@salarytracker.com");
            admin.setRole("ROLE_ADMIN");
            admin.setActiveStatus(true);
            
            usersRepository.save(admin);
            
            log.info("##################################################");
            log.info("#                                                #");
            log.info("#  DEFAULT ADMIN CREATED                         #");
            log.info("#  Username: admin                               #");
            log.info("#  Password: admin1234                           #");
            log.info("#                                                #");
            log.info("##################################################");
        }
    }

    private void seedTaxMasterData() {
        if (taxMasterDataRepository.count() == 0) {
            log.info("Seeding default tax master data...");
            TaxMasterData masterData = new TaxMasterData();
            masterData.setMaxPersonalExpenses(new BigDecimal("100000"));
            masterData.setPersonalAllowance(new BigDecimal("60000"));
            masterData.setMaxSocialSecurity(new BigDecimal("9000"));
            masterData.setActive(true);
            
            taxMasterDataRepository.save(masterData);
        }
    }

    private void seedTaxBrackets() {
        if (taxBracketRepository.count() == 0) {
            log.info("Seeding default tax brackets (Thailand 2024)...");
            List<TaxBracket> brackets = new ArrayList<>();
            
            brackets.add(createBracket(new BigDecimal("0"), new BigDecimal("150000"), new BigDecimal("0"), 1));
            brackets.add(createBracket(new BigDecimal("150001"), new BigDecimal("300000"), new BigDecimal("5"), 2));
            brackets.add(createBracket(new BigDecimal("300001"), new BigDecimal("500000"), new BigDecimal("10"), 3));
            brackets.add(createBracket(new BigDecimal("500001"), new BigDecimal("750000"), new BigDecimal("15"), 4));
            brackets.add(createBracket(new BigDecimal("750001"), new BigDecimal("1000000"), new BigDecimal("20"), 5));
            brackets.add(createBracket(new BigDecimal("1000001"), new BigDecimal("2000000"), new BigDecimal("25"), 6));
            brackets.add(createBracket(new BigDecimal("2000001"), new BigDecimal("5000000"), new BigDecimal("30"), 7));
            brackets.add(createBracket(new BigDecimal("5000001"), null, new BigDecimal("35"), 8));
            
            taxBracketRepository.saveAll(brackets);
        }
    }

    private TaxBracket createBracket(BigDecimal min, BigDecimal max, BigDecimal rate, Integer seq) {
        TaxBracket bracket = new TaxBracket();
        bracket.setMinIncome(min);
        bracket.setMaxIncome(max);
        bracket.setTaxRate(rate);
        bracket.setSequence(seq);
        return bracket;
    }
}
