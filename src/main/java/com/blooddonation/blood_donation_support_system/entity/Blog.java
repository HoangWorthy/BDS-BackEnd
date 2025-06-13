package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    private Status status;
    private LocalDate creationDate;
    private LocalDate lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
        lastModifiedDate = LocalDate.now();
        status = Status.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDate.now();
    }
}
