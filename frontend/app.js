const data = {
    HighBP: Number(document.getElementById("highBP").value),
    HighChol: Number(document.getElementById("highChol").value),
    CholCheck: 1,  // default if not in UI

    BMI: Number(document.getElementById("bmi").value),

    Smoker: Number(document.getElementById("smoker").value),

    Stroke: 0,
    HeartDiseaseorAttack: 0,
    PhysActivity: 0,
    Fruits: 0,
    Veggies: 0,

    HvyAlcoholConsump: 0,
    AnyHealthcare: 1,
    NoDocbcCost: 0,

    GenHlth: 3,
    MentHlth: 0,
    PhysHlth: 0,

    DiffWalk: 0,

    Sex: 1,
    Age: Number(document.getElementById("age").value),

    Education: 4,
    Income: 5
};