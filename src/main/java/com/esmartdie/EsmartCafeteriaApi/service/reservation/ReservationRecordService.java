package com.esmartdie.EsmartCafeteriaApi.service.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ReservationRecordDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationRecordService implements IReservationRecordService{

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Override
    public List<ReservationRecordDTO> getReservationRecordsForMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        Optional<List<ReservationRecord>> optionalReservationRecords =
                reservationRecordRepository.findByReservationDateBetween(startDate, endDate);

        if (optionalReservationRecords.isEmpty()) {
            return Collections.emptyList();
        }

        List<ReservationRecord> reservationRecords = optionalReservationRecords.get();
        return reservationRecords.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReservationRecordDTO mapToDTO(ReservationRecord reservationRecord) {
        ReservationRecordDTO dto = new ReservationRecordDTO();
        dto.setId(reservationRecord.getId());
        dto.setReservationDate(reservationRecord.getReservationDate());
        dto.setShift(reservationRecord.getShift());
        dto.setAvailableReservations(reservationRecord.getEmptySpaces());
        return dto;
    }

}
