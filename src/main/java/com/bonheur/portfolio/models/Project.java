package com.bonheur.portfolio.models;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "projects")
@Entity
@Builder
public class Project {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT", name = "description")
    private String description;

    @Column(nullable = false, name = "images")
    private String images;

    @Column(nullable = false, name = "url")
    private String url;

    @Column(nullable = false, name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    @Column(nullable = false, name = "category")
    private String category;

    @Column(nullable = false, name = "technologies")
    private String technologies;

    @Column(nullable = false, name = "start_time")
    private String startTime;

    @Column(nullable = true, name = "end_time")
    private String endTime;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
