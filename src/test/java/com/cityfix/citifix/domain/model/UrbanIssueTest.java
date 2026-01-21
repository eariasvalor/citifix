package com.cityfix.citifix.domain.model;

import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

class UrbanIssueTest {

    private final UserId defaultReporter = new UserId(1L);
    private final IssueTitle defaultTitle = new IssueTitle("Broken Streetlight");
    private final Coordinates defaultCoordinates = new Coordinates(40.4168, -3.7038);
    private final String defaultImage = "http://cloudinary.com/sample.jpg";

    @Test
    @DisplayName("Should create a valid issue when all mandatory fields are provided")
    void shouldCreateValidIssue() {
        UrbanIssue issue = new UrbanIssue(
                1L,
                defaultTitle,
                "Description",
                defaultCoordinates,
                defaultReporter,
                IssueStatus.REPORTED,
                IssueCategory.LIGHTING,
                defaultImage
        );

        assertNotNull(issue);
        assertEquals(defaultImage, issue.getImageUrl());
        assertEquals(IssueStatus.REPORTED, issue.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when Title is missing")
    void shouldThrowException_WhenTitleIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new UrbanIssue(null, null, "Desc", defaultCoordinates, defaultReporter, IssueStatus.REPORTED, IssueCategory.ROAD, defaultImage)
        );
        assertEquals("Title is mandatory", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when Coordinates are missing")
    void shouldThrowException_WhenCoordinatesAreNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new UrbanIssue(null, defaultTitle, "Desc", null, defaultReporter, IssueStatus.REPORTED, IssueCategory.ROAD, defaultImage)
        );
        assertEquals("Coordinates are mandatory", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when Reporter ID is missing")
    void shouldThrowException_WhenReporterIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new UrbanIssue(null, defaultTitle, "Desc", defaultCoordinates, null, IssueStatus.REPORTED, IssueCategory.ROAD, defaultImage)
        );
        assertEquals("Reporter ID is mandatory", exception.getMessage());
    }

    @Test
    @DisplayName("Should rehydrate issue correctly")
    void shouldRehydrateIssue() {
        UrbanIssue issue = UrbanIssue.rehydrate(
                10L,
                defaultTitle,
                "Rehydrated Desc",
                defaultCoordinates,
                defaultReporter,
                IssueStatus.RESOLVED,
                IssueCategory.TRASH,
                null
        );

        assertEquals(10L, issue.getId());
        assertEquals("Rehydrated Desc", issue.getDescription());
        assertEquals(IssueStatus.RESOLVED, issue.getStatus());
    }

    @Test
    @DisplayName("Should update specific details while keeping others intact")
    void shouldCorrectDetails() {
        UrbanIssue issue = new UrbanIssue(1L, defaultTitle, "Old Desc", defaultCoordinates, defaultReporter, IssueStatus.REPORTED, IssueCategory.OTHER, defaultImage);

        issue.correctDetails("New Title", null, IssueCategory.ROAD);

        assertEquals("New Title", issue.getTitle().value());
        assertEquals("Old Desc", issue.getDescription());
        assertEquals(IssueCategory.ROAD, issue.getCategory());
    }

    @Test
    @DisplayName("Should not update details if inputs are empty or blank")
    void shouldNotUpdate_WhenInputsAreBlank() {
        UrbanIssue issue = new UrbanIssue(1L, defaultTitle, "Old Desc", defaultCoordinates, defaultReporter, IssueStatus.REPORTED, IssueCategory.OTHER, defaultImage);

        issue.correctDetails("", null, null);

        assertEquals(defaultTitle.value(), issue.getTitle().value());
    }

    @Test
    @DisplayName("Should allow transition: REPORTED -> IN_PROGRESS")
    void shouldMarkAsInProgress() {
        UrbanIssue issue = new UrbanIssue(1L, defaultTitle, "Desc", defaultCoordinates, defaultReporter, IssueStatus.REPORTED, IssueCategory.ROAD, defaultImage);
        issue.markAsInProgress();
        assertEquals(IssueStatus.IN_PROGRESS, issue.getStatus());
    }

    @Test
    @DisplayName("Should allow transition: IN_PROGRESS -> RESOLVED")
    void shouldResolveIssue() {
        UrbanIssue issue = new UrbanIssue(1L, defaultTitle, "Desc", defaultCoordinates, defaultReporter, IssueStatus.IN_PROGRESS, IssueCategory.ROAD, defaultImage);
        issue.resolve();
        assertEquals(IssueStatus.RESOLVED, issue.getStatus());
    }

    @Test
    @DisplayName("Should force status change arbitrarily")
    void shouldForceStatusChange() {
        UrbanIssue issue = new UrbanIssue(1L, defaultTitle, "Desc", defaultCoordinates, defaultReporter, IssueStatus.REPORTED, IssueCategory.ROAD, defaultImage);
        issue.forceStatusChange(IssueStatus.RESOLVED);
        assertEquals(IssueStatus.RESOLVED, issue.getStatus());
    }

    @Test
    @DisplayName("Should FAIL transition: RESOLVED -> IN_PROGRESS")
    void shouldThrowException_WhenWorkingOnResolvedIssue() {
        UrbanIssue issue = new UrbanIssue(1L, defaultTitle, "Desc", defaultCoordinates, defaultReporter, IssueStatus.RESOLVED, IssueCategory.ROAD, defaultImage);
        assertThrows(IllegalStateException.class, issue::markAsInProgress);
    }

    @Test
    @DisplayName("Should FAIL transition: REPORTED -> RESOLVED")
    void shouldThrowException_WhenResolvingReportedIssueDirectly() {
        UrbanIssue issue = new UrbanIssue(1L, defaultTitle, "Desc", defaultCoordinates, defaultReporter, IssueStatus.REPORTED, IssueCategory.ROAD, defaultImage);
        assertThrows(IllegalStateException.class, issue::resolve);
    }
}