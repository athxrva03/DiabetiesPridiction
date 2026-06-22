package com.minor_2.diabetes_predictor.dto;

import lombok.Data;

@Data
public class DiabetesRequest {

    // ===== Health Conditions =====
    private Integer highBP;
    private Integer highChol;
    private Integer cholCheck;
    private Integer bmi;

    // ===== Lifestyle Factors =====
    private Integer smoker;
    private Integer stroke;
    private Integer heartDiseaseOrAttack;
    private Integer physActivity;
    private Integer fruits;
    private Integer veggies;
    private Integer hvyAlcoholConsump;

    // ===== Medical Access =====
    private Integer anyHealthcare;
    private Integer noDocbcCost;

    // ===== Health Status =====
    private Integer genHlth;
    private Integer mentHlth;
    private Integer physHlth;
    private Integer diffWalk;

    // ===== Demographics =====
    private Integer sex;
    private Integer age;
    private Integer education;
    private Integer income;
}