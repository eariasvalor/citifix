package com.cityfix.citifix.domain.model;

import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;

public class UrbanIssue {

    private Long id;
    private IssueTitle title;
    private Coordinates coordinates;
    private UserId reporterId;
    private IssueStatus status;


    public UrbanIssue(Long id, IssueTitle title, Coordinates coordinates, UserId reporterId) {
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
        this.coordinates = coordinates;
        this.reporterId = reporterId;

        this.status = IssueStatus.REPORTED;
    }

    private UrbanIssue(Long id, IssueTitle title, Coordinates coordinates, UserId reporterId, IssueStatus status) {
        this.id = id;
        this.title = title;
        this.coordinates = coordinates;
        this.reporterId = reporterId;
        this.status = status;
    }

    public static UrbanIssue rehydrate(Long id, IssueTitle title, Coordinates coordinates, UserId reporterId, IssueStatus status) {
        return new UrbanIssue(id, title, coordinates, reporterId, status);
    }

    public Long getId() { return id; }
    public IssueTitle getTitle() { return title; }
    public Coordinates getCoordinates() { return coordinates; }
    public UserId getReporterId() { return reporterId; }
    public IssueStatus getStatus() { return status; }

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