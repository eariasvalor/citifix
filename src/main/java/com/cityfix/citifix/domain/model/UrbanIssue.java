package com.cityfix.citifix.domain.model;

import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public final class UrbanIssue {

    private final Long id;
    private final IssueTitle title;
    private final String description;
    private final Coordinates coordinates;
    private final UserId reporterId;
    private final IssueStatus status;
    private final IssueCategory category;
    private final String imageUrl;
    private final LocalDateTime createdAt;

    public UrbanIssue(
            Long id,
            IssueTitle title,
            String description,
            Coordinates coordinates,
            UserId reporterId,
            IssueStatus status,
            IssueCategory category,
            String imageUrl,
            LocalDateTime createdAt
    ) {
        if (title == null) throw new IllegalArgumentException("Title is mandatory");
        if (coordinates == null) throw new IllegalArgumentException("Coordinates are mandatory");
        if (reporterId == null) throw new IllegalArgumentException("Reporter ID is mandatory");

        this.id = id;
        this.title = title;
        this.description = description;
        this.coordinates = coordinates;
        this.reporterId = reporterId;
        this.status = status != null ? status : IssueStatus.REPORTED;
        this.category = category;
        this.imageUrl = imageUrl;
        this.createdAt = (id == null) ? LocalDateTime.now() : createdAt;
    }

    public static UrbanIssue rehydrate(
            Long id,
            IssueTitle title,
            String description,
            Coordinates coordinates,
            UserId reporterId,
            IssueStatus status,
            IssueCategory category,
            String imageUrl,
            LocalDateTime createdAt
    ) {
        return new UrbanIssue(
                id,
                title,
                description,
                coordinates,
                reporterId,
                status,
                category,
                imageUrl,
                createdAt
        );
    }

    public static UrbanIssue rehydrate(
            Long id, IssueTitle title, String description, Coordinates coordinates,
            UserId reporterId, IssueStatus status, IssueCategory category, String imageUrl) {
        return new UrbanIssue(id, title, description, coordinates, reporterId,
                status, category, imageUrl, LocalDateTime.now());
    }

    public UrbanIssue updateDetails(String newTitle, String newDescription, IssueCategory newCategory) {
        return new UrbanIssue(
                this.id,
                (newTitle != null && !newTitle.isBlank()) ? new IssueTitle(newTitle) : this.title,
                newDescription != null ? newDescription : this.description,
                this.coordinates,
                this.reporterId,
                this.status,
                newCategory != null ? newCategory : this.category,
                this.imageUrl,
                this.createdAt
        );
    }

    public UrbanIssue markAsInProgress() {
        return withStatus(IssueStatus.IN_PROGRESS);
    }

    public UrbanIssue backToReported() {
        return withStatus(IssueStatus.REPORTED);
    }

    public UrbanIssue resolve() {
        if (this.status == IssueStatus.REPORTED) {
            throw new IllegalStateException("Issue must be IN_PROGRESS before resolving");
        }
        return withStatus(IssueStatus.RESOLVED);
    }

    private UrbanIssue withStatus(IssueStatus newStatus) {
        return new UrbanIssue(
                this.id,
                this.title,
                this.description,
                this.coordinates,
                this.reporterId,
                newStatus,
                this.category,
                this.imageUrl,
                this.createdAt
        );
    }

    public UrbanIssue withImageUrl(String newImageUrl) {
        return new UrbanIssue(
                this.id,
                this.title,
                this.description,
                this.coordinates,
                this.reporterId,
                this.status,
                this.category,
                newImageUrl,
                this.createdAt
        );
    }

    public Long getId() { return id; }
    public IssueTitle getTitle() { return title; }
    public String getDescription() { return description; }
    public Coordinates getCoordinates() { return coordinates; }
    public UserId getReporterId() { return reporterId; }
    public IssueStatus getStatus() { return status; }
    public IssueCategory getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}