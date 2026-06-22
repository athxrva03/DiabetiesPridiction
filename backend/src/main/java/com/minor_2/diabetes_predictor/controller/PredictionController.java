package com.minor_2.diabetes_predictor.controller;

import com.minor_2.diabetes_predictor.service.PredictionService;
import com.minor_2.diabetes_predictor.entity.PredictionRecord;
import com.minor_2.diabetes_predictor.dto.DiabetesRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diabetes")
@CrossOrigin("*")
public class PredictionController {

    @Autowired
    private PredictionService service;

    // ================= PREDICTION API =================
    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(
            @RequestBody DiabetesRequest input) {
        Map<String, Object> response = service.predict(input);

        return ResponseEntity.ok(response);
    }

    // ================= HISTORY API =================
    @GetMapping("/history")
    public ResponseEntity<List<PredictionRecord>> getHistory() {
        return ResponseEntity.ok(service.filter(null, null));
    }

    // ================= ANALYTICS API =================
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(service.getAnalytics());
    }

    // ================= FILTER API =================
    @GetMapping("/history/filter")
    public ResponseEntity<List<PredictionRecord>> filter(
            @RequestParam(required = false) Integer prediction,
            @RequestParam(required = false) Integer age) {

        return ResponseEntity.ok(service.filter(prediction, age));
    }
}