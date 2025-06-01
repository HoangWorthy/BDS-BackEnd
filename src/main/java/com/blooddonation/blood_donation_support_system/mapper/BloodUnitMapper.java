package com.blooddonation.blood_donation_support_system.mapper;

        import com.blooddonation.blood_donation_support_system.dto.BloodUnitDto;
        import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
        import org.springframework.stereotype.Component;

        @Component
        public class BloodUnitMapper {
            public BloodUnitDto toDto(BloodUnit entity) {
                if (entity == null) return null;

                BloodUnitDto dto = new BloodUnitDto();
                dto.setId(entity.getId());
                dto.setBloodType(entity.getBloodType());
                dto.setVolume(entity.getVolume());
                return dto;
            }

            public BloodUnit toEntity(BloodUnitDto dto) {
                if (dto == null) return null;

                BloodUnit entity = new BloodUnit();
                entity.setId(dto.getId());
                entity.setBloodType(dto.getBloodType());
                entity.setVolume(dto.getVolume());
                return entity;
            }
        }