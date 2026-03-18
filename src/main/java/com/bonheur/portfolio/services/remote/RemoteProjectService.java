package com.bonheur.portfolio.services.remote;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bonheur.portfolio.models.Project;
import com.bonheur.portfolio.repositories.ProjectRepository;

import jakarta.transaction.Transactional;

@Service
public class RemoteProjectService {
    private final ProjectRepository projectRepository;

    public RemoteProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Page<Project> searchRemoteProjects(String title, String category, String technologies, int page, int size,
            String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sortBy));

        Specification<Project> spec = (root, query, cb) -> {
            jakarta.persistence.criteria.Predicate p = cb.conjunction();
            if (title != null && !title.isBlank()) {
                p = cb.and(p, cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (category != null && !category.isBlank()) {
                p = cb.and(p, cb.like(cb.lower(root.get("category")), "%" + category.toLowerCase() + "%"));
            }
            if (technologies != null && !technologies.isBlank()) {
                p = cb.and(p, cb.like(cb.lower(root.get("technologies")), "%" + technologies.toLowerCase() + "%"));
            }

            p = cb.and(p, cb.equal(root.get("isDeleted"), false));
            return p;
        };

        return projectRepository.findAll(spec, pageable);
    }

    @Transactional
    public Optional<Project> getProjectById(UUID id) {
        return projectRepository.findById(id);
    }
}
