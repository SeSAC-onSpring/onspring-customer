package com.onspring.onspring_customer.global.config;

import com.onspring.onspring_customer.domain.common.entity.CustomerFranchise;
import com.onspring.onspring_customer.domain.common.entity.PlatformAdmin;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.common.repository.CustomerFranchiseRepository;
import com.onspring.onspring_customer.domain.common.repository.PlatformAdminRepository;
import com.onspring.onspring_customer.domain.common.repository.TransactionRepository;
import com.onspring.onspring_customer.domain.customer.entity.Admin;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.AdminRepository;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.Point;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class DataLoadConfig implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final FranchiseRepository franchiseRepository;
    private final EndUserRepository endUserRepository;
    private final PartyRepository partyRepository;
    private final TransactionRepository transactionRepository;
    private final PointRepository pointRepository;
    private final AdminRepository adminRepository;
    private final PlatformAdminRepository platformAdminRepository;
    private final CustomerFranchiseRepository customerFranchiseRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        // 고객 데이터가 없으면 고객 추가
        if (customerRepository.findAll().isEmpty()) {
            List<Customer> customers = IntStream.range(0, 3).mapToObj(i -> {
                Customer customer = new Customer();
                customer.setName(faker.company().name());
                customer.setAddress(faker.address().fullAddress());
                customer.setPhone(faker.phoneNumber().phoneNumber());
                customer.setActivated(faker.bool().bool());
                return customer;
            }).toList();
            customerRepository.saveAll(customers);
        }

        // 프랜차이즈 데이터가 없으면 프랜차이즈 추가
        if (franchiseRepository.findAll().isEmpty()) {
            List<Franchise> franchises = IntStream.range(0, 20).mapToObj(i -> {
                Franchise franchise = new Franchise();
                franchise.setName(faker.company().name());
                franchise.setAddress(faker.address().fullAddress());
                franchise.setPhone(faker.phoneNumber().phoneNumber());
                return franchise;
            }).toList();
            franchiseRepository.saveAll(franchises);
        }

        // 고객-프랜차이즈 관계 데이터가 없으면 관계 추가
        if (customerFranchiseRepository.findAll().isEmpty()) {
            List<CustomerFranchise> customerFranchises = IntStream.range(0, 30).mapToObj(i -> {
                CustomerFranchise cf = new CustomerFranchise();
                cf.setCustomer(customerRepository.findAll().get(random.nextInt(3)));
                cf.setFranchise(franchiseRepository.findAll().get(random.nextInt(20)));
                return cf;
            }).toList();
            customerFranchiseRepository.saveAll(customerFranchises);
        }

        // 엔드 유저 데이터가 없으면 엔드 유저 추가
        if (endUserRepository.findAll().isEmpty()) {
            List<EndUser> endUsers = IntStream.range(0, 100).mapToObj(i -> {
                EndUser endUser = new EndUser();
                endUser.setName(faker.name().fullName());
                endUser.setPhone(faker.phoneNumber().cellPhone());
                endUser.setActivated(faker.bool().bool());
                return endUser;
            }).toList();
            endUserRepository.saveAll(endUsers);
        }

        // 파티 데이터가 없으면 파티 추가
        if (partyRepository.findAll().isEmpty()) {
            List<Party> parties = IntStream.range(0, 10).mapToObj(i -> {
                Party party = new Party();
                party.setCustomer(customerRepository.findAll().get(random.nextInt(3)));
                party.setName(faker.team().name());
                party.setPeriod(LocalDateTime.now().plusDays(random.nextInt(30)));
                party.setAmount(BigDecimal.valueOf(random.nextDouble() * 1000));
                party.setActivated(faker.bool().bool());
                party.setSunday(faker.bool().bool());
                party.setMonday(faker.bool().bool());
                party.setTuesday(faker.bool().bool());
                party.setWednesday(faker.bool().bool());
                party.setThursday(faker.bool().bool());
                party.setFriday(faker.bool().bool());
                party.setSaturday(faker.bool().bool());
                party.setValidThru((long) random.nextInt(365)); // 예: 1~365일 내 유효

                // allowedTimeStart와 allowedTimeEnd 변수 선언
                int startHour = random.nextInt(24);
                int startMinute = random.nextInt(60);
                LocalTime startTime = LocalTime.of(startHour, startMinute);

                // allowedTimeEnd는 startTime 이후로 설정
                int endHour = random.nextInt(24);
                int endMinute = random.nextInt(60);

                // Ensure end time is after start time
                LocalTime endTime = startTime.plusMinutes(random.nextInt(24 * 60 - startTime.toSecondOfDay() / 60));

                party.setAllowedTimeStart(startTime);
                party.setAllowedTimeEnd(endTime);
                party.setMaximumAmount(BigDecimal.valueOf(random.nextDouble() * 15000));
                party.setMaximumTransaction((long) random.nextInt(3));

                return party;
            }).toList();
            partyRepository.saveAll(parties);
        }

        // 파티-엔드 유저 관계 데이터가 없으면 관계 추가
        if (pointRepository.findAll().isEmpty()) {
            List<Point> points = IntStream.range(0, 1500).mapToObj(i -> {
                Point point = new Point();
                point.setParty(partyRepository.findAll().get(random.nextInt(10)));
                point.setEndUser(endUserRepository.findAll().get(random.nextInt(100)));
                return point;
            }).toList();
            pointRepository.saveAll(points);
        }

        // 트랜잭션 데이터가 없으면 트랜잭션 추가
        if (transactionRepository.findAll().isEmpty()) {
            List<Transaction> transactions = IntStream.range(0, 1000).mapToObj(i -> {
                Transaction transaction = new Transaction();
                transaction.setFranchise(franchiseRepository.findAll().get(random.nextInt(20)));
                transaction.setEndUser(endUserRepository.findAll().get(random.nextInt(100)));
                transaction.setTransactionTime(LocalDateTime.now().minusDays(random.nextInt(10)));
                transaction.setAmount(BigDecimal.valueOf(random.nextDouble() * 500));
                transaction.setAccepted(faker.bool().bool());
                transaction.setClosed(faker.bool().bool());
                return transaction;
            }).toList();
            transactionRepository.saveAll(transactions);
        }

        // 포인트 데이터가 없으면 포인트 추가
        if (pointRepository.findAll().isEmpty()) {
            List<Point> points = IntStream.range(0, 200).mapToObj(i -> {
                Point point = new Point();
                point.setParty(partyRepository.findAll().get(random.nextInt(10)));
                point.setEndUser(endUserRepository.findAll().get(random.nextInt(100)));
                point.setCurrentAmount(BigDecimal.valueOf(random.nextDouble() * 100));
                point.setValidThru(LocalDateTime.now().plusDays(random.nextInt(365)));
                return point;
            }).toList();
            pointRepository.saveAll(points);
        }

        // 관리자 데이터가 없으면 관리자 추가
        if (adminRepository.findAll().isEmpty()) {
            List<Admin> admins = IntStream.range(0, 10).mapToObj(i -> {
                Admin admin = new Admin();
                admin.setCustomer(customerRepository.findAll().get(random.nextInt(3)));
                admin.setUserName(faker.internet().username());
                admin.setSuperAdmin(faker.bool().bool());
                return admin;
            }).toList();
            adminRepository.saveAll(admins);
        }

        // 플랫폼 관리자 데이터가 없으면 플랫폼 관리자 추가
        if (platformAdminRepository.findAll().isEmpty()) {
            List<PlatformAdmin> platformAdmins = IntStream.range(0, 3).mapToObj(i -> {
                PlatformAdmin platformAdmin = new PlatformAdmin();
                platformAdmin.setUserName(faker.internet().username());
                return platformAdmin;
            }).toList();
            platformAdminRepository.saveAll(platformAdmins);
        }

        log.info("Data load complete.");
    }
}