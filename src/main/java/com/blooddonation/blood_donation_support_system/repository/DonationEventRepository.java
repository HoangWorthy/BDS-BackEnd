package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import org.springframework.data.repository.CrudRepository;

public interface DonationEventRepository extends CrudRepository<DonationEvent, Long> {
}
