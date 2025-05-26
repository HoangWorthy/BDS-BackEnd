package com.blooddonation.blood_donation_support_system.dto;

    import com.blooddonation.blood_donation_support_system.entity.BloodType;
    import com.blooddonation.blood_donation_support_system.entity.Gender;
    import com.blooddonation.blood_donation_support_system.entity.Role;
    import com.fasterxml.jackson.annotation.JsonFormat;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.time.LocalDate;

@Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserDto {
        private Long id;
        private String email;
        private String password;
        private String name;
        private String phone;
        private String address;
        private BloodType bloodType;
        private Gender gender;

        @JsonFormat(pattern = "dd-MM-yyyy")
        private LocalDate dateOfBirth;

        private Role role;

        @JsonFormat(pattern = "dd-MM-yyyy")
        private LocalDate lastDonationDate;

        private String personalId;
    }