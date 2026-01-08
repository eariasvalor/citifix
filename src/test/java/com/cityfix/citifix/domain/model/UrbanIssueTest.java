package com.cityfix.citifix.domain.model;

import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrbanIssueTest {

    @Test
    @DisplayName("Should create a valid Aggregate with default REPORTED status")
    void shouldCreateValidAggregate() {
        var title = new IssueTitle("Broken streetlight");
        var coords = new Coordinates(41.38, 2.17);
        var reporterId = new UserId(1L);

        UrbanIssue issue = new UrbanIssue(null, title, coords, reporterId);

        assertNotNull(issue);
        assertEquals("Broken streetlight", issue.getTitle().value());
        Assertions.assertEquals(IssueStatus.REPORTED, issue.getStatus());
        assertEquals(41.38, issue.getCoordinates().latitude());
    }

    @Test
    @DisplayName("Should throw exception if any mandatory component is null")
    void shouldThrowExceptionForNullComponents() {
        var title = new IssueTitle("Valid");
        var coords = new Coordinates(1.0, 1.0);
        var reporter = new UserId(1L);

        assertThrows(IllegalArgumentException.class,
                () -> new UrbanIssue(null, null, coords, reporter));

        assertThrows(IllegalArgumentException.class,
                () -> new UrbanIssue(null, title, null, reporter));

        assertThrows(IllegalArgumentException.class,
                () -> new UrbanIssue(null, title, coords, null));
    }

    @Test
    @DisplayName("Should update status to IN_PROGRESS")
    void shouldMarkAsInProgress() {
        var issue = new UrbanIssue(1L, new IssueTitle("Title"), new Coordinates(1.0, 1.0), new UserId(1L));

        issue.markAsInProgress();

        assertEquals(IssueStatus.IN_PROGRESS, issue.getStatus());
    }

    @Test
    @DisplayName("Should update status to RESOLVED")
    void shouldResolveIssue() {
        var issue = new UrbanIssue(1L, new IssueTitle("Title"), new Coordinates(1.0, 1.0), new UserId(1L));
        issue.markAsInProgress();

        issue.resolve();

        assertEquals(IssueStatus.RESOLVED, issue.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when resolving a reported issue directly (Strict Workflow)")
    void shouldNotResolveDirectlyFromReported() {
        var issue = new UrbanIssue(1L, new IssueTitle("Title"), new Coordinates(1.0, 1.0), new UserId(1L));

        assertThrows(IllegalStateException.class, issue::resolve);
    }
}