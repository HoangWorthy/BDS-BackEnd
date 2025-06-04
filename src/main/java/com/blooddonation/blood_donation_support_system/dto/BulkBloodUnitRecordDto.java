package com.blooddonation.blood_donation_support_system.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkBloodUnitRecordDto {
    private List<SingleBloodUnitRecordDto> singleBloodUnitRecords;
}
