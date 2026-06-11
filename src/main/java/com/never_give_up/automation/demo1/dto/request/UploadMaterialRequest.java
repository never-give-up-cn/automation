package com.never_give_up.automation.demo1.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadMaterialRequest {
    private MultipartFile file;
    private String materialType;
    private String uploadedBy;
    private String remark;
}