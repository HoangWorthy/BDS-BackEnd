package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.MedicalFacilityStockDto;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import org.springframework.stereotype.Component;

@Component
public class MedicalFacilityStockMapper {

    public static MedicalFacilityStockDto toDto(MedicalFacilityStock stock) {
        if (stock == null) return null;

        MedicalFacilityStockDto dto = new MedicalFacilityStockDto();
        dto.setId(stock.getId());
        dto.setVolume(stock.getVolume());
        dto.setBloodType(stock.getBloodType());
        dto.setComponentType(stock.getComponentType());

        return dto;
    }

    public static MedicalFacilityStock toEntity(MedicalFacilityStockDto dto) {
        if (dto == null) return null;

        MedicalFacilityStock stock = new MedicalFacilityStock();
        stock.setId(dto.getId());
        stock.setVolume(dto.getVolume());
        stock.setBloodType(dto.getBloodType());
        stock.setComponentType(dto.getComponentType());

        return stock;
    }
}
