package com.minor_2.diabetes_predictor.service;

import com.minor_2.diabetes_predictor.dto.DiabetesRequest;
import com.minor_2.diabetes_predictor.entity.PredictionRecord;
import com.minor_2.diabetes_predictor.repository.PredictionRepository;

import java.time.LocalDateTime;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PredictionService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final double THRESHOLD = 0.5;

    private static final Logger logger =
            LoggerFactory.getLogger(PredictionService.class);


    @Autowired
    private PredictionRepository repository;

    // ================= MAIN PREDICTION =================
    public Map<String, Object> predict(DiabetesRequest request) {

        try {

            // 1. Convert DTO → BRFSS format (VERY IMPORTANT)
            Map<String, Object> input = toBrfssFormat(request);

            // 2. Call Python Flask model
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:5000/predict",
                    input,
                    Map.class);

            Map<String, Object> responseMap = response.getBody();

            if (responseMap == null) {
                throw new RuntimeException("Flask service returned empty response");
            }

            // 3. Safe extraction
            int prediction = ((Number) responseMap.get("prediction")).intValue();
            double probability = ((Number) responseMap.get("probability")).doubleValue();

            // 4. Save to DB (safe mapping)
            PredictionRecord record = new PredictionRecord();
            record.setPrediction(prediction);
            record.setProbability(probability);
            record.setTimestamp(LocalDateTime.now());

            // request.getBmi() may be Double; PredictionRecord.setBmi expects int
            if (request.getBmi() == null) {
                record.setBmi(0);
            } else {
                record.setBmi(request.getBmi().intValue());
            }
            record.setAge(request.getAge());

            repository.save(record);

            // 5. Build structured medical response
            return buildResponse(prediction, probability);

        } catch (Exception e) {

            // 6. Error-safe response (VERY IMPORTANT for real apps)
            Map<String, Object> error = new HashMap<>();
            error.put("status", "FAILED");
            error.put("message", "Prediction service unavailable");
            error.put("error", e.getMessage());

            return error;
        }
    }

    // ================= RESPONSE BUILDER =================
    private Map<String, Object> buildResponse(int prediction, double probability) {

        Map<String, Object> res = new LinkedHashMap<>();

        // Risk classification using threshold
        String riskLevel;
        String riskCategory;
        String recommendation;

        if (probability < THRESHOLD) {
            riskLevel = "LOW";
            riskCategory = "Low Risk";
            recommendation = "Maintain healthy lifestyle, regular exercise.";
        } else if (probability < 0.50) {
            riskLevel = "MEDIUM";
            riskCategory = "Moderate Risk";
            recommendation = "Monitor diet, reduce sugar intake, regular checkups.";
        } else if (probability < 0.75) {
            riskLevel = "HIGH";
            riskCategory = "High Risk";
            recommendation = "Consult doctor, get HbA1c test.";
        } else {
            riskLevel = "VERY_HIGH";
            riskCategory = "Very High Risk";
            recommendation = "Immediate medical consultation recommended.";
        }

        res.put("status", "SUCCESS");
        res.put("prediction", prediction == 1 ? "DIABETIC_RISK" : "LOW_RISK");
        res.put("probability", probability);
        res.put("threshold_used", THRESHOLD);
        res.put("risk_level", riskLevel);
        res.put("risk_category", riskCategory);
        res.put("recommendation", recommendation);

        res.put("model", "XGBoost_v1.0");
        res.put("model_type", "ML - Gradient Boosting");

        res.put("medical_note",
                "This is a screening result only and not a medical diagnosis.");

        return res;
    }

    // ================= ANALYTICS =================
    public Map<String, Object> getAnalytics() {

        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("totalPredictions", repository.totalPredictions());
        stats.put("diabeticCount", repository.diabeticCount());
        stats.put("nonDiabeticCount", repository.nonDiabeticCount());
        stats.put("avgBMI", repository.averageBMI());

        double total = repository.totalPredictions();
        double diabetic = repository.diabeticCount();

        stats.put("diabetesRate", total == 0 ? 0 : (diabetic / total) * 100);

        return stats;
    }

    // ================= FILTER =================
    public List<PredictionRecord> filter(Integer prediction, Integer age) {

        List<PredictionRecord> list = repository.findAll();

        if (prediction != null) {
            list = list.stream()
                    .filter(p -> p.getPrediction() == prediction)
                    .toList();
        }

        if (age != null) {
            list = list.stream()
                    .filter(p -> p.getAge() == age)
                    .toList();
        }

        return list;
    }

    private Map<String, Object> toBrfssFormat(DiabetesRequest req) {

        Map<String, Object> map = new HashMap<>();

        map.put("HighBP", req.getHighBP());
        map.put("HighChol", req.getHighChol());
        map.put("CholCheck", req.getCholCheck());
        map.put("BMI", req.getBmi());

        map.put("Smoker", req.getSmoker());
        map.put("Stroke", req.getStroke());
        map.put("HeartDiseaseorAttack", req.getHeartDiseaseOrAttack());
        map.put("PhysActivity", req.getPhysActivity());

        map.put("Fruits", req.getFruits());
        map.put("Veggies", req.getVeggies());
        map.put("HvyAlcoholConsump", req.getHvyAlcoholConsump());

        map.put("AnyHealthcare", req.getAnyHealthcare());
        map.put("NoDocbcCost", req.getNoDocbcCost());

        map.put("GenHlth", req.getGenHlth());
        map.put("MentHlth", req.getMentHlth());
        map.put("PhysHlth", req.getPhysHlth());
        map.put("DiffWalk", req.getDiffWalk());

        map.put("Sex", req.getSex());
        map.put("Age", req.getAge());
        map.put("Education", req.getEducation());
        map.put("Income", req.getIncome());

        return map;
    }
}