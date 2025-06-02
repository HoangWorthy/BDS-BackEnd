package com.blooddonation.blood_donation_support_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkBloodUnitRecordDto {
    private List<SingleBloodUnitRecordDto> singleBloodUnitRecords;
}
