package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.PlatformAdminDto;
import com.onspring.onspring_customer.domain.common.entity.PlatformAdmin;
import com.onspring.onspring_customer.domain.common.entity.QPlatformAdmin;
import com.onspring.onspring_customer.domain.common.repository.PlatformAdminRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class PlatformAdminServiceImpl implements PlatformAdminService {
    private final PlatformAdminRepository platformAdminRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;


    private PlatformAdmin getPlatformAdmin(Long id) {
        Optional<PlatformAdmin> result = platformAdminRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("PlatformAdmin with ID " + id + " not found"));
    }

    /**
     * 새로운 플랫폼 관리자를 저장합니다.
     *
     * @param platformAdminDto 저장할 플랫폼 관리자에 대한 DTO
     * @return 저장된 플랫폼 관리자의 ID
     * @throws IllegalArgumentException platformAdminDto가 null인 경우
     */
    @Override
    public Long savePlatformAdmin(PlatformAdminDto platformAdminDto) {
        log.info("Saving platform admin with user name {}", platformAdminDto.getUserName());

        PlatformAdmin platformAdmin = modelMapper.map(platformAdminDto, PlatformAdmin.class);

        Long id = platformAdminRepository.save(platformAdmin)
                .getId();

        log.info("Successfully saved platform admin with user name {}", platformAdminDto.getUserName());

        return id;
    }

    /**
     * 주어진 ID로 플랫폼 관리자를 찾습니다.
     *
     * @param id 찾을 플랫폼 관리자의 ID
     * @return 플랫폼 관리자의 DTO
     * @throws EntityNotFoundException 해당 ID로 플랫폼 관리자를 찾을 수 없는 경우
     */
    @Override
    public PlatformAdminDto findPlatformAdminById(Long id) {
        PlatformAdmin platformAdmin = getPlatformAdmin(id);

        return modelMapper.map(platformAdmin, PlatformAdminDto.class);
    }


    /**
     * 모든 플랫폼 관리자들을 조회합니다.
     *
     * @return 모든 플랫폼 관리자의 DTO 리스트
     */
    @Override
    public List<PlatformAdminDto> findAllPlatformAdmin() {
        return platformAdminRepository.findAll()
                .stream()
                .map(element -> modelMapper.map(element, PlatformAdminDto.class))
                .toList();
    }

    @Override
    public Page<PlatformAdminDto> findAllPlatformAdminByQuery(String userName, boolean isActivated, Pageable pageable) {
        QPlatformAdmin platformAdmin = QPlatformAdmin.platformAdmin;
        JPAQuery<PlatformAdmin> query = queryFactory.selectFrom(platformAdmin);

        if (userName != null) {
            query.where(platformAdmin.userName.containsIgnoreCase(userName));
        }
        if (isActivated) {
            query.where(platformAdmin.isActivated);
        }

        Long count = Objects.requireNonNull(query.clone()
                .select(platformAdmin.count())
                .fetchOne());

        query.orderBy(platformAdmin.id.desc());
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<PlatformAdmin> platformAdminList = query.fetch();

        List<PlatformAdminDto> platformAdminDtoList = platformAdminList.stream()
                .map(element -> modelMapper.map(element, PlatformAdminDto.class))
                .toList();

        return new PageImpl<>(platformAdminDtoList, pageable, count);
    }

    /**
     * 기존 플랫폼 관리자의 암호를 업데이트합니다.
     *
     * @param id       암호를 변경할 플랫폼 관리자의 ID
     * @param password 변경할 암호
     * @return 암호 업데이트가 성공하면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
     * @throws EntityNotFoundException 제공된 ID로 플랫폼 관리자를 찾을 수 없는 경우
     */
    @Override
    public boolean updatePlatformAdminPasswordById(Long id, String password) {
        log.info("Updating password for platform admin with ID {}", id);

        PlatformAdmin platformAdmin = getPlatformAdmin(id);

        platformAdmin.setPassword(password);
        platformAdminRepository.save(platformAdmin);

        log.info("Successfully updated password for platform admin with ID {}", id);

        return true;
    }

    /**
     * 주어진 ID에 해당하는 플랫폼 관리자를 활성화합니다.
     *
     * @param id 활성화할 플랫폼 관리자의 ID
     * @return 활성화가 성공하면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
     * @throws EntityNotFoundException 제공된 ID로 플랫폼 관리자를 찾을 수 없는 경우
     */
    @Override
    public boolean activatePlatformAdminById(Long id) {
        log.info("Activating platform admin with ID {}", id);

        PlatformAdmin platformAdmin = getPlatformAdmin(id);

        platformAdmin.setActivated(true);
        platformAdminRepository.save(platformAdmin);

        log.info("Successfully activated platform admin with ID {}", id);

        return true;
    }

    /**
     * 주어진 ID에 해당하는 플랫폼 관리자를 비활성화합니다.
     *
     * @param id 비활성화할 플랫폼 관리자의 ID
     * @return 비활성화가 성공하면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
     * @throws EntityNotFoundException 제공된 ID로 플랫폼 관리자를 찾을 수 없는 경우
     */
    @Override
    public boolean deactivatePlatformAdminById(Long id) {
        log.info("Deactivating platform admin with ID {}", id);

        PlatformAdmin platformAdmin = getPlatformAdmin(id);

        if (platformAdmin.isSuperAdmin()) {
            log.warn("Attempted to deactivate a super platform admin with ID {}", id);

            return false;
        }

        platformAdmin.setActivated(false);
        platformAdminRepository.save(platformAdmin);

        log.info("Successfully deactivated a super platform admin with ID {}", id);

        return true;
    }

    /**
     * 주어진 ID에 해당하는 플랫폼 관리자를 제거합니다.
     *
     * @param id 제거할 플랫폼 관리자의 ID
     * @return 제거가 성공하면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
     * @throws EntityNotFoundException 제공된 ID로 플랫폼 관리자를 찾을 수 없는 경우
     */
    @Override
    public boolean deletePlatformAdminById(Long id) {
        log.info("Deleting platform admin with ID {}", id);

        PlatformAdmin platformAdmin = getPlatformAdmin(id);

        if (platformAdmin.isSuperAdmin()) {
            log.warn("Attempted to delete a super platform admin with ID {}", id);

            return false;
        }

        platformAdminRepository.delete(platformAdmin);

        log.info("Successfully deleted platform admin with ID {}", id);

        return true;
    }
}
