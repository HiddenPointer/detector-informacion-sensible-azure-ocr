package com.example.imageprotection.repository;

import com.example.imageprotection.model.DetectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetectionRecordRepository extends JpaRepository<DetectionRecord, Long> {
}
