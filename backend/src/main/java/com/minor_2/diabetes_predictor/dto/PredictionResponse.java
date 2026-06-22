package com.minor_2.diabetes_predictor.dto;

import lombok.Data;

@Data
public class PredictionResponse {

    private Integer prediction;
    private Double probability;
}