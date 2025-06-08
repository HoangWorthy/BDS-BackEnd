package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.MedicalFacilityStockDto;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MedicalFacilityStockMapper {

    public static MedicalFacilityStockDto toDto(MedicalFacilityStock stock) {
        if (stock == null) return null;

        MedicalFacilityStockDto dto = new MedicalFacilityStockDto();
        dto.setId(stock.getId());
        dto.setVolume(stock.getVolume());
        dto.setBloodType(stock.getBloodType());
        dto.setComponentType(stock.getComponentType());
        dto.setExpiryDate(stock.getExpiryDate());

        return dto;
    }

    public static MedicalFacilityStock toEntity(MedicalFacilityStockDto dto) {
        if (dto == null) return null;

        MedicalFacilityStock stock = new MedicalFacilityStock();
        stock.setId(dto.getId());
        stock.setVolume(dto.getVolume());
        stock.setBloodType(dto.getBloodType());
        stock.setComponentType(dto.getComponentType());
        stock.setExpiryDate(dto.getExpiryDate());

        return stock;
    }

    public static MedicalFacilityStock fromBloodUnit(BloodUnit bloodUnit) {
        if (bloodUnit == null) return null;

        MedicalFacilityStock stock = new MedicalFacilityStock();
        stock.setBloodType(bloodUnit.getBloodType());
        stock.setVolume(bloodUnit.getVolume());
        stock.setComponentType(bloodUnit.getComponentType());
        // Optionally set expiry date here if there's logic for it
        return stock;
    }


    public static MedicalFacilityStock copyWithNewVolume(MedicalFacilityStock original, double newVolume) {
        if (original == null) return null;

        MedicalFacilityStock copy = new MedicalFacilityStock();
        copy.setId(original.getId()); // Use same ID for updates
        copy.setBloodType(original.getBloodType());
        copy.setComponentType(original.getComponentType());
        copy.setVolume(newVolume);
        copy.setExpiryDate(original.getExpiryDate());
        return copy;
    }

    public static MedicalFacilityStock createWithdrawnStock(MedicalFacilityStock source, double withdrawnVolume) {
        MedicalFacilityStock withdrawn = new MedicalFacilityStock();
        withdrawn.setBloodType(source.getBloodType());
        withdrawn.setComponentType(source.getComponentType());
        withdrawn.setVolume(withdrawnVolume);
        withdrawn.setExpiryDate(source.getExpiryDate());
        return withdrawn;
    }

    public static MedicalFacilityStock createComponent(BloodType bloodType, ComponentType componentType, double volume, LocalDate expiryDate) {
        MedicalFacilityStock component = new MedicalFacilityStock();
        component.setBloodType(bloodType);
        component.setComponentType(componentType);
        component.setVolume(volume);
        component.setExpiryDate(expiryDate);
        return component;
    }


}
