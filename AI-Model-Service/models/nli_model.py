from transformers import pipeline

# Load Natural Language Inference model

nli_pipeline = pipeline(
    "text-classification",
    model="roberta-large-mnli"
)