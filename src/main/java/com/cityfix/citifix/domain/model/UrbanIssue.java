package com.cityfix.citifix.domain.model;

import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;

public class UrbanIssue {

    private Long id;
    private IssueTitle title;
    private String description;
    private Coordinates coordinates;
    private UserId reporterId;
    private IssueStatus status;
    private IssueCategory category;


    public UrbanIssue(Long id, IssueTitle title, String description, Coordinates coordinates, UserId reporterId, IssueCategory issueCategory) {
        if (title == null) {
            throw new IllegalArgumentException("Title is mandatory");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates are mandatory");
        }
        if (reporterId == null) {
            throw new IllegalArgumentException("Reporter ID is mandatory");
        }

        this.id = id;
        this.title = title;
        this.description = description;
        this.coordinates = coordinates;
        this.reporterId = reporterId;
        this.status = IssueStatus.REPORTED;
        this.category = (issueCategory != null) ? issueCategory : IssueCategory.OTHER;
    }

    private UrbanIssue(Long id, IssueTitle title, String description, Coordinates coordinates, UserId reporterId, IssueStatus status, IssueCategory category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.coordinates = coordinates;
        this.reporterId = reporterId;
        this.status = status;
        this.category = category;
    }

    public static UrbanIssue rehydrate(Long id, IssueTitle title, String description, Coordinates coordinates, UserId reporterId, IssueStatus status, IssueCategory category) {
        return new UrbanIssue(id, title, description, coordinates, reporterId, status, category);
    }

    public Long getId() { return id; }
    public IssueTitle getTitle() { return title; }
    public String getDescription() {return description;}
    public Coordinates getCoordinates() { return coordinates; }
    public UserId getReporterId() { return reporterId; }
    public IssueStatus getStatus() { return status; }
    public IssueCategory getCategory() { return category; }


    public void setId(Long id) { this.id = id; }

    public void markAsInProgress() {
        if (this.status == IssueStatus.RESOLVED) {
            throw new IllegalStateException("Cannot work on a resolved issue");
        }
        this.status = IssueStatus.IN_PROGRESS;
    }

    public void resolve() {
        if (this.status == IssueStatus.REPORTED) {
            throw new IllegalStateException("Issue must be IN_PROGRESS before resolving");
        }
        this.status = IssueStatus.RESOLVED;
    }
}