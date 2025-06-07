package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByPersonalId(String personalId);
}
