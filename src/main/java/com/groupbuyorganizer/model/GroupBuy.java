package com.groupbuyorganizer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_buys")
public class GroupBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be 100 characters or fewer")
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1500, message = "Description must be 1500 characters or fewer")
    @Column(nullable = false, length = 1500)
    private String description;

    @Column(nullable = false)
    private String organizer;

    @Column(nullable = false)
    private String createdBy;

    @NotNull(message = "Target members is required")
    @Min(value = 2, message = "Target members must be at least 2")
    @Column(nullable = false)
    private Integer targetMembers = 2;

    @NotNull(message = "Current members is required")
    @Min(value = 1, message = "Current members must be at least 1")
    @Column(nullable = false)
    private Integer currentMembers = 1;

    @NotNull(message = "Price per person is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Column(nullable = false)
    private Double pricePerPerson;

    @Column(nullable = false)
    private String status = "Open";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public GroupBuy() {}

    public GroupBuy(String title, String description, String organizer, String createdBy,
                    Integer targetMembers, Integer currentMembers, Double pricePerPerson, String status) {
        this.title = title;
        this.description = description;
        this.organizer = organizer;
        this.createdBy = createdBy;
        this.targetMembers = targetMembers;
        this.currentMembers = currentMembers;
        this.pricePerPerson = pricePerPerson;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Integer getTargetMembers() { return targetMembers; }
    public void setTargetMembers(Integer targetMembers) { this.targetMembers = targetMembers; }
    public Integer getCurrentMembers() { return currentMembers; }
    public void setCurrentMembers(Integer currentMembers) { this.currentMembers = currentMembers; }
    public Double getPricePerPerson() { return pricePerPerson; }
    public void setPricePerPerson(Double pricePerPerson) { this.pricePerPerson = pricePerPerson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
