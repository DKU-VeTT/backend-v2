import os
import re
import textwrap
import json
import pytz
from datetime import datetime
from pinecone import Pinecone
from langchain_pinecone import PineconeVectorStore
from langchain_upstage import UpstageEmbeddings
from langchain_community.document_loaders import Docx2txtLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from fastapi import FastAPI, Request, HTTPException, Depends, UploadFile, File ,Form
from fastapi.responses import PlainTextResponse, JSONResponse
from dotenv import load_dotenv
from fastapi.responses import StreamingResponse
from llm import get_rag_chain
from llm import get_dictionary_chain
from llm import get_llm
from llm import get_diagnosis_from_llm
from fastapi.middleware.cors import CORSMiddleware
from mongoDBClient import MongoDBChatMessageHistory

import torch
import io
import cv2  
import torch.nn as nn
from torchvision import transforms
from torchvision.models import efficientnet_b0, EfficientNet_B0_Weights
from PIL import Image

load_dotenv()

seoul_tz = pytz.timezone("Asia/Seoul")
app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True,
    allow_methods=["*"], 
    allow_headers=["*"],
)

async def authentication_filter(request: Request):
    token = request.headers.get("X-Passport-Secret")
    expected_token = os.getenv("APP_PASSPORT_SECRET")
    if token != expected_token:
        now = datetime.now(seoul_tz).strftime("%Y-%m-%d %H:%M:%S")
        raise HTTPException(
            status_code=401,
            detail={
                "code" : "A001",
                "success": False,
                "message": "인증되지 않은 접근입니다.",
                "timestamp": now
            }
        )

eye_class_labels = ['무', '유']

eye_model_names = {
    'dog': ['안검내반증', '안검염', '안검종양', '유루증', '핵경화'],
    'cat': ['각막궤양', '각막부골편', '결막염', '비궤양성각막염', '안검염']
}
class ImageClassifier:
    def __init__(self, model_path):
        self.device = 'cpu'
        self.model = efficientnet_b0(weights=EfficientNet_B0_Weights.IMAGENET1K_V1)
        self.model.classifier[1] = nn.Linear(1280, 2)
        self.model.load_state_dict(torch.load(model_path, map_location=self.device))
        self.model.to(self.device)
        self.model.eval()

        self.transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406],
                                 std=[0.229, 0.224, 0.225]),
        ])

    def predict(self, image_bytes):
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        input_tensor = self.transform(image).unsqueeze(0).to(self.device)
        with torch.no_grad():
            outputs = self.model(input_tensor)
            _, predicted = torch.max(outputs, 1)
            label = eye_class_labels[predicted.item()]
            confidence = torch.softmax(outputs, dim=1)[0][predicted.item()].item()
        return label, confidence


@app.post("/api/v1/ai/model/eye")
async def predict_image(
    file: UploadFile = File(...),
    species: str = Form(...),
    description: str = Form(...),
    _: None = Depends(authentication_filter)):

    try:
        if species not in eye_model_names:
            return JSONResponse(content={"error": f"Invalid species: {species}"}, status_code=400)

        image_bytes = await file.read()
        results = []
        high_data = {"disease": None, "label": None, "confidence": 0.0}

        for disease in eye_model_names[species]:
            model_path = f"./model_trains/{species}_{disease}.pth"
            if not os.path.exists(model_path):
                continue
            classifier = ImageClassifier(model_path)
            label, confidence = classifier.predict(image_bytes)
            result = {
                "disease": disease,
                "label": label,
                "confidence": round(confidence, 4)
            }
            results.append(result)

            if label == '유' and confidence > high_data["confidence"]:
                high_data = result

        if high_data["disease"] is None:
            lowest_confidence = min(r["confidence"] for r in results) if results else 0.0
            return JSONResponse(
                content={
                    "statusCode" : 201,
                    "success" : True,
                    "data" : {
                        "diseaseName": "무증상",
                        "confidenceScore": lowest_confidence,
                        "diagnosisResult": "해당 증상에 대한 정보가 존재하지 않거나, 의심 증상이 없습니다.",
                        "severityLevel": "매우 낮음",
                    }
                }, status_code=201
            )
        diagnosisData = get_diagnosis_from_llm(
            high_data["disease"],
            high_data["confidence"],
            description
        )
        return JSONResponse(
            content={
                "statusCode" : 201,
                "success" : True,
                "data" : {
                    "diseaseName": high_data["disease"],
                    "confidenceScore": high_data["confidence"],
                    "diagnosisResult": diagnosisData["diagnosisResult"],
                    "severityLevel": diagnosisData["severityLevel"],
                }
            }, status_code=201
        )
    except Exception as e:
        now = datetime.now(seoul_tz).strftime("%Y-%m-%d %H:%M:%S")
        return JSONResponse(content={
            "code" : "S001",
            "success" : False,
            "message": str(e),
            "time" : now
        }, status_code=500)


@app.post("/api/v1/ai/llm/fetch", response_class=JSONResponse)
async def initial_vector_database(request: Request, _: None = Depends(authentication_filter)):

    embedding = UpstageEmbeddings(model="embedding-query")
    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=1500,
        chunk_overlap=200
    )
    loader = Docx2txtLoader('./data.docx')
    document_list = loader.load_and_split(text_splitter=text_splitter)

    index_name = os.getenv("PINECONE_INDEX_NAME")
    pinecone_api_key = os.environ.get("PINECONE_API_KEY")
    pc = Pinecone(api_key=pinecone_api_key)
    database = PineconeVectorStore.from_documents(document_list, embedding, index_name=index_name)

    return JSONResponse(
        content={
           "statusCode" : 201,
            "success" : True,
            "message" : "Success save vector database."
        }, status_code=201
    )



@app.post("/api/v1/ai/llm/chat", response_class=PlainTextResponse)
async def chat_with_ai(request: Request, _: None = Depends(authentication_filter)):
    payload = await request.json()
    user_message = payload.get("message", "")
    session_id = payload.get("session_id", "default-session")

    chat_history = MongoDBChatMessageHistory(session_id)
    chat_history.add_human_message(user_message)

    def response_stream():
        chain = get_rag_chain()
        dictionary_chain = get_dictionary_chain()
        vet_chain = {"input": dictionary_chain} | chain
        stream = vet_chain.stream(
            {"question": user_message},
            config={"configurable": {"session_id": session_id}},
        )
        for chunk in stream:
            yield chunk

    return StreamingResponse(response_stream(), media_type="text/plain")