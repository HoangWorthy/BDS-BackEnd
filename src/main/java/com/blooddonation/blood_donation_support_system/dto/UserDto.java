package com.blooddonation.blood_donation_support_system.dto;

    import com.blooddonation.blood_donation_support_system.entity.BloodType;
    import com.blooddonation.blood_donation_support_system.entity.Gender;
    import com.blooddonation.blood_donation_support_system.entity.Role;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.util.Date;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserDto {
        private Long id;
        private String email;
        private String password;
        private String name;
        private int phone;
        private String address;
        private BloodType bloodType;
        private Gender gender;
        private Date dateOfBirth;
        private Role role;
        private Date lastDonationDate;
        private int personalId;
    }