package com.onspring.onspring_customer.domain.franchise.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import com.onspring.onspring_customer.domain.common.entity.CustomerFranchise;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "franchise")
public class Franchise extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String userName;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String ownerName;

    @NotNull
    private String businessNumber;

    @NotNull
    @Column(unique = true)
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;


    @NotNull
    @Column(unique = true)
    private String phone;

    @Column
    private String description;

    @NotNull
    private boolean isActivated;

    @OneToMany(mappedBy = "franchise")
    private Set<CustomerFranchise> customerFranchises = new HashSet<>();

    @OneToMany(mappedBy = "franchise")
    private Set<Transaction> transactions = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    List<FranchiseMenuImages> menuImages = new ArrayList<>();

    public void addImage(FranchiseMenuImages image) {
        image.setOrd(this.menuImages.size());
        menuImages.add(image);
    }

    public void addImageString(String fileName) {
        FranchiseMenuImages image = new FranchiseMenuImages();
        image.setFileName(fileName);
        addImage(image);
    }

    public void clearList() {
        this.menuImages.clear();
    }

    public FranchiseDto entityToDto() {
        FranchiseDto franchiseDto = FranchiseDto.builder()
                .id(this.id)
                .name(this.name)
                .address(this.address)
                .phone(this.phone)
                .businessNumber(this.businessNumber)
                .ownerName(this.ownerName)
                .description(this.description)
                .build();

        // 이미지 파일 리스트 변환
        List<String> fileNames = new ArrayList<>();
        for (FranchiseMenuImages image : this.menuImages) {
            fileNames.add(image.getFileName());
        }
        franchiseDto.setUploadFileNames(fileNames);

        return franchiseDto;
    }



}