package com.groupbuyorganizer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class GroupBuy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Organizer name is required")
    private String organizer;

    @NotNull(message = "Target members is required")
    @Min(value = 2, message = "Target members must be at least 2")
    private Integer targetMembers;

    @NotNull(message = "Current members is required")
    @Min(value = 1, message = "Current members must be at least 1")
    private Integer currentMembers;

    @NotNull(message = "Price per person is required")
    @Min(value = 1, message = "Price must be greater than zero")
    private Double pricePerPerson;

    private String status = "Open";

    public GroupBuy() {
    }

    public GroupBuy(String title, String description, String organizer,
                   Integer targetMembers, Integer currentMembers,
                   Double pricePerPerson, String status) {
        this.title = title;
        this.description = description;
        this.organizer = organizer;
        this.targetMembers = targetMembers;
        this.currentMembers = currentMembers;
        this.pricePerPerson = pricePerPerson;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Integer getTargetMembers() {
        return targetMembers;
    }

    public void setTargetMembers(Integer targetMembers) {
        this.targetMembers = targetMembers;
    }

    public Integer getCurrentMembers() {
        return currentMembers;
    }

    public void setCurrentMembers(Integer currentMembers) {
        this.currentMembers = currentMembers;
    }

    public Double getPricePerPerson() {
        return pricePerPerson;
    }

    public void setPricePerPerson(Double pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
