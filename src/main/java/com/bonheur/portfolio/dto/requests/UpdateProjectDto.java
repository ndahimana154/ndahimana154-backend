package com.bonheur.portfolio.dto.requests;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateProjectDto {
    @Size(min = 3)
    private String title;

    private String description;

    private MultipartFile[] images;

    private String url;

    private String category;

    private String technologies;

    private String startTime;

    private String endTime;

    private Boolean isDeleted;

}
