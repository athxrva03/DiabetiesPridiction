from flask import Flask, request, jsonify
import pandas as pd
import joblib

app = Flask(__name__)

model = joblib.load("diabetes_model.pkl")

FEATURES = [
    "HighBP", "HighChol", "CholCheck", "BMI", "Smoker",
    "Stroke", "HeartDiseaseorAttack", "PhysActivity",
    "Fruits", "Veggies", "HvyAlcoholConsump", "AnyHealthcare",
    "NoDocbcCost", "GenHlth", "MentHlth", "PhysHlth",
    "DiffWalk", "Sex", "Age", "Education", "Income"
]

THRESHOLD = 0.45   # FIXED

@app.route("/predict", methods=["POST"])
def predict():
    data = request.json

    if not data:
        return jsonify({"error": "No input data received"}), 400

    missing = [f for f in FEATURES if f not in data]

    if missing:
        return jsonify({
            "error": "Missing features",
            "fields": missing
        }), 400

    df = pd.DataFrame([data], columns=FEATURES)

    df = df.apply(pd.to_numeric, errors='coerce')
    df = df.fillna(0)

    probability = model.predict_proba(df)[0][1]
    prediction = int(probability >= THRESHOLD)

    return jsonify({
        "prediction": prediction,
        "probability": round(float(probability), 4)
    })


if __name__ == "__main__":
    app.run(debug=True, port=5000)