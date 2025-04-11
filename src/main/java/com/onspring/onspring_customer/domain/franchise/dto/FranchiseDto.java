package com.onspring.onspring_customer.domain.franchise.dto;

import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FranchiseDto implements Serializable {
    Long id;

    @NotNull
    @Size(min = 1)
    String userName;

    String password;

    @NotNull
    @Size(min = 1)
    String name;

    @NotNull
    @Size(min = 1)
    String ownerName;

    @NotNull
    @Size(min = 1)
    String businessNumber;

    @NotNull
    @Size(min = 1)
    String address;

    Double latitude;

    Double longitude;

    @NotNull
    @Size(min = 1)
    String phone;

    String description;

    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();

    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();

    public Franchise dtoToEntity() {
        Franchise franchise = new Franchise();
        franchise.setId(this.id);
        franchise.setName(this.name);
        franchise.setAddress(this.address);
        franchise.setPhone(this.phone);
        franchise.setDescription(this.description);

        // 파일 처리
        List<String> uploadFileNames = this.uploadFileNames;
        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(franchise::addImageString);
        }

        return franchise;
    }

    boolean isActivated;
}
