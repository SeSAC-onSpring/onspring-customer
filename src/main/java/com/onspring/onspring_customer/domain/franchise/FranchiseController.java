package com.onspring.onspring_customer.domain.franchise;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import com.onspring.onspring_customer.global.util.file.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/franchise")
public class FranchiseController {
    private final FranchiseService franchiseService;
    private final CustomFileUtil customFileUtil;

    //프랜차이즈 정보 보기
    @GetMapping("/info")
    public ResponseEntity<FranchiseDto> info() {
        Long id = 1L;// 테스트용 ID

        FranchiseDto franchiseDto = franchiseService.findFranchiseById(id);

        return ResponseEntity.ok(franchiseDto);
    }


    //메뉴 사진 업로드
    @PutMapping(value = "/menu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMenu(@ModelAttribute FranchiseDto franchiseDto) {
        Long id = 1L; // 테스트용 ID

        FranchiseDto oldFranchiseDto = franchiseService.findFranchiseById(id);

        //기존 데이터베이스에 존재하는 파일들
        List<String> oldFileNames = oldFranchiseDto.getUploadFileNames();

        //새로 업로드 해야하는 파일
        List<MultipartFile> files = franchiseDto.getFiles();

        log.info("files : " + files);



        //새로 업로드 될 파일 이름들
        List<String> currentFileNames = customFileUtil.saveFiles(files);

        //화면에서 유지할 파일들
        List<String> uploadFileNames = franchiseDto.getUploadFileNames();

        //유질될 파일들 + 새로만든 파일 이름들
        if(currentFileNames != null && currentFileNames.size() > 0) {
            uploadFileNames.addAll(currentFileNames);
        }

        franchiseService.updateMenuImage(franchiseDto);


        if(oldFileNames != null && !oldFileNames.isEmpty()){
            //지울 파일 목록 찾기
            //예전 파일 이름 중에서 지워져야 할 파일 이름들
            //기존에 있던 파일 이름들 중에 새로 업로드될 파일 이름에 없는 파일들 remove
            List<String> removeFiles = oldFileNames
                    .stream()
                    .filter(fileName -> !uploadFileNames.contains(fileName)).collect(Collectors.toList());

            customFileUtil.deleteFiles(removeFiles);
        }

        return ResponseEntity.ok("메뉴 이미지 업데이트가 완료되었습니다.");
    }

    //메뉴 사진 조회
    @GetMapping("menu/{fileName}")
    public ResponseEntity<Resource> getMenu(@PathVariable String fileName) {
        return customFileUtil.getFile(fileName);
    }



}
