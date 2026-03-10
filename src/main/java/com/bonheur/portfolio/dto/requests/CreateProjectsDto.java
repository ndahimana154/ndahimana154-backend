package com.bonheur.portfolio.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateProjectsDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Image URL is required")
    private String images;

    @NotBlank(message = "Project URL is required")
    private String url;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Technologies are required")
    private String technologies;

    @NotBlank(message = "Start time is required")
    private String startTime;

    private String endTime;

}
