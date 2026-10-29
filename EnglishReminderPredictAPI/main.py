from fastapi import FastAPI
import joblib
import pandas as pd
from pydantic import BaseModel

app = FastAPI()

model = joblib.load("word_memory_model.joblib")

FEATURE_COLUMNS = [
    "correct_count",
    "wrong_count",
    "sec_since_last_correct",
    "sec_since_last_wrong",
]

class PredictionRequest(BaseModel):
    word_id: str
    correct_count: int
    wrong_count: int
    sec_since_last_correct: int
    sec_since_last_wrong: int


class PredictionResponse(BaseModel):
    word_id: str
    prediction: int
    probability_percent: float


@app.post("/predict", response_model=list[PredictionResponse])
def predict(data: list[PredictionRequest]) -> list[PredictionResponse]:
    if not data:
        return []

    df = pd.DataFrame([item.model_dump() for item in data])
    features_df = df[FEATURE_COLUMNS]

    preds = model.predict(features_df)
    probs = model.predict_proba(features_df)[:, 1]

    predictions_list = []
    for word, pred, prob in zip(data, preds, probs):
        predictions_list.append(
            PredictionResponse(
                word_id=word.word_id,
                prediction=int(pred),
                probability_percent=round(float(prob) * 100, 2),
            )
        )

    return predictions_list