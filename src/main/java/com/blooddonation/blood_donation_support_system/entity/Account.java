package com.blooddonation.blood_donation_support_system.entity;

    import com.blooddonation.blood_donation_support_system.enums.Role;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Builder
    @Table(name = "accounts")
    public class Account {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "profile_id", nullable = false)
        private Profile profile;

        @NotBlank
        @Column(unique = true)
        private String email;

        @NotBlank
        private String password;

        @Enumerated(EnumType.STRING)
        private Role role;
    }