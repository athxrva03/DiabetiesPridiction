package com.minor_2.diabetes_predictor.repository;

import com.minor_2.diabetes_predictor.entity.PredictionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PredictionRepository extends JpaRepository<PredictionRecord, Long> {

    @Query("SELECT COUNT(p) FROM PredictionRecord p")
    long totalPredictions();

    @Query("SELECT COUNT(p) FROM PredictionRecord p WHERE p.prediction = 1")
    long diabeticCount();

    @Query("SELECT COUNT(p) FROM PredictionRecord p WHERE p.prediction = 0")
    long nonDiabeticCount();

    @Query("SELECT AVG(p.bmi) FROM PredictionRecord p")
    Double averageBMI();
}