package com.onspring.onspring_customer.domain.franchise.service;

import com.onspring.onspring_customer.domain.customer.entity.QCustomer;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.entity.QFranchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class FranchiseServiceImpl implements FranchiseService {
    private final FranchiseRepository franchiseRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JPAQueryFactory queryFactory;

    private Franchise getFranchise(Long id) {
        return franchiseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID " + id + "에 해당하는 프랜차이즈를 찾을 수 없습니다."));
    }

    @Override
    public Long saveFranchise(FranchiseDto franchiseDto) {
        log.info("Saving franchise with user name {}", franchiseDto.getName());

        Franchise franchise = modelMapper.map(franchiseDto, Franchise.class);

        Long id = franchiseRepository.save(franchise)
                .getId();

        log.info("Successfully saved franchise with user name {}", franchiseDto.getName());

        return id;
    }

    /**
     * 특정 ID에 해당하는 프랜차이즈 정보를 조회
     *
     * @param id 프랜차이즈의 ID
     * @return 해당 ID의 프랜차이즈 정보를 담은 FranchiseDto 객체 반환
     */
    @Override
    public FranchiseDto findFranchiseById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("franchise id cannot be null");
        }

        Franchise franchise = getFranchise(id);

        log.info(franchise.getId());

        FranchiseDto franchiseDto = franchise.entityToDto();

        return franchiseDto;
    }

    /**
     * 특정 사용자가 소속된 고객에 등록된 가맹점 리스트
     *
     * @param userId 사용자의 ID
     * @return 해당 ID의 프랜차이즈 정보를 담은 FranchiseDto List 객체 반환
     */
    @Override
    public Page<FranchiseDto> findFranchiseListByUserId(Long userId, Pageable pageable) {
        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return franchiseRepository.findAllFranchiseByEndUserId(userId, pageable)
                .map(franchise -> modelMapper.map(franchise, FranchiseDto.class));
    }

    @Override
    public List<FranchiseDto> findAllFranchise() {
        return List.of();
    }

    @Override
    public Page<FranchiseDto> findAllFranchiseByQuery(String userName, String name, String ownerName,
                                                      String businessNumber, String address, String phone,
                                                      String customerName, boolean isActivated, Pageable pageable) {
        QFranchise franchise = QFranchise.franchise;
        JPAQuery<Franchise> query = queryFactory.selectFrom(franchise);

        if (userName != null) {
            query.where(franchise.userName.containsIgnoreCase(userName));
        }
        if (name != null) {
            query.where(franchise.name.containsIgnoreCase(name));
        }
        if (ownerName != null) {
            query.where(franchise.ownerName.containsIgnoreCase(ownerName));
        }
        if (businessNumber != null) {
            query.where(franchise.businessNumber.containsIgnoreCase(businessNumber));
        }
        if (address != null) {
            query.where(franchise.address.containsIgnoreCase(address));
        }
        if (phone != null) {
            query.where(franchise.phone.contains(phone));
        }
        if (customerName != null) {
            QCustomer customer = QCustomer.customer;
            List<Long> customerIds = queryFactory.select(customer.id)
                    .from(customer)
                    .where(customer.name.containsIgnoreCase(customerName))
                    .fetch();

            query.where(franchise.customerFranchises.any().customer.id.in(customerIds));
        }

        query.where(franchise.isActivated.eq(isActivated));

        Long count = Objects.requireNonNull(query.clone()
                .select(franchise.count())
                .fetchOne());

        query.orderBy(franchise.id.desc());
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<Franchise> franchiseList = query.fetch();

        List<FranchiseDto> franchiseDtoList = franchiseList.stream()
                .map(element -> modelMapper.map(element, FranchiseDto.class))
                .toList();

        return new PageImpl<>(franchiseDtoList, pageable, count);
    }

    // 엔티티의 필드를 업데이트하는 메소드
    public void updateFranchiseFields(Franchise franchise, FranchiseDto franchiseDto) {
        log.info(franchiseDto);
        if (franchiseDto.getName() != null) {
            franchise.setName(franchiseDto.getName());
        }
        if (franchiseDto.getAddress() != null) {
            franchise.setAddress(franchiseDto.getAddress());
        }
        if (franchiseDto.getPhone() != null) {
            franchise.setPhone(franchiseDto.getPhone());
        }
        if (franchiseDto.getDescription() != null) {
            log.info(franchiseDto.getDescription());
            franchise.setDescription(franchiseDto.getDescription());
        }
    }

    /**
     * 프랜차이즈의 정보를 수정
     *
     * @param id 프랜차이즈의 로그인에 사용할 Id 후에 수정 필요
     * @return 업데이트 성공 여부를 담은 boolean 객체
     */
    @Override
    public boolean updateFranchise(Long id, FranchiseDto franchiseDto) {
        Franchise franchise = getFranchise(id);

        updateFranchiseFields(franchise, franchiseDto);

        franchiseRepository.save(franchise);

        return true;
    }

    /**
     * 가맹점의 새 비밀번호 업데이트
     *
     * @param id            가맹점 id
     * @param oldPassword   기존 password
     * @param newPassword   새 password
     * @return 성공여부
     */
    @Override
    public boolean updateFranchisePassword(Long id, String oldPassword, String newPassword) {
        Franchise franchise = getFranchise(id);
        if (passwordEncoder.matches(oldPassword, franchise.getPassword())) {
            franchise.setPassword(passwordEncoder.encode(newPassword));
            franchiseRepository.save(franchise);
            return true;
        }
        return false;
    }

    /**
     * 프랜차이즈의 메뉴 이미지를 수정
     *
     * @param franchiseDto 프랜차이즈의 메뉴 이미지를 담은 dto객체
     * @return 업데이트 성공 여부를 담은 boolean 객체
     */
    @Override
    public boolean updateMenuImage(FranchiseDto franchiseDto) {
        Franchise franchise = franchiseRepository.findById(franchiseDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("ID" + franchiseDto.getId() + "에 해당하는 프랜차이즈를 찾을 수 없습니다."));

        //업로드 된 파일을 우선 지움
        franchise.clearList();

        List<String> uploadFileNames = franchiseDto.getUploadFileNames();

        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(franchise::addImageString);
        }
        franchiseRepository.save(franchise);

        return true;
    }

    @Override
    public boolean deleteFranchiseById(Long id) {
        return false;
    }


    @Override
    public boolean activateFranchiseById(Long id) {
        log.info("Activating franchise with id {}", id);

        Franchise franchise = getFranchise(id);

        franchise.setActivated(true);
        franchiseRepository.save(franchise);

        log.info("Successfully activated franchise with id {}", id);

        return true;
    }

    @Override
    public List<Boolean> activateFranchiseById(List<Long> ids) {
        return ids.stream()
                .map(this::activateFranchiseById)
                .toList();
    }

    @Override
    public boolean deactivateFranchiseById(Long id) {
        log.info("Deactivating franchise with id {}", id);

        Franchise franchise = getFranchise(id);

        franchise.setActivated(false);
        franchiseRepository.save(franchise);

        log.info("Successfully deactivated franchise with id {}", id);

        return true;
    }

    @Override
    public List<Boolean> deactivateFranchiseById(List<Long> ids) {
        return ids.stream()
                .map(this::deactivateFranchiseById)
                .toList();
    }
}
