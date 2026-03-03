package com.community.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private int votes = 0;

    @Enumerated(EnumType.STRING)
    private ProblemStatus status = ProblemStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    private LocalDateTime createdAt;

    // ================= VOTES RELATION (🔥 IMPORTANT FIX) =================
    @OneToMany(
            mappedBy = "problem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Vote> voteList = new ArrayList<>();


    // ================= MULTIPLE IMAGES =================
    @OneToMany(
            mappedBy = "problem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProblemImage> images = new ArrayList<>();


    public Problem() {
        this.createdAt = LocalDateTime.now();
    }

    public Problem(String title, String description, String location) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.votes = 0;
        this.status = ProblemStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public int getVotes() { return votes; }

    public void setVotes(int votes) { this.votes = votes; }

    public ProblemStatus getStatus() { return status; }

    public void setStatus(ProblemStatus status) { this.status = status; }

    public User getCreatedBy() { return createdBy; }

    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Vote> getVoteList() { return voteList; }

    public void setVoteList(List<Vote> voteList) { this.voteList = voteList; }

    public List<ProblemImage> getImages() { return images; }

    public void setImages(List<ProblemImage> images) { this.images = images; }

    // ================= HELPER METHODS =================

    public void addImage(ProblemImage image) {
        images.add(image);
        image.setProblem(this);
    }

    public void removeImage(ProblemImage image) {
        images.remove(image);
        image.setProblem(null);
    }

    public void addVote(Vote vote) {
        voteList.add(vote);
        vote.setProblem(this);
    }

    public void removeVote(Vote vote) {
        voteList.remove(vote);
        vote.setProblem(null);
    }
}