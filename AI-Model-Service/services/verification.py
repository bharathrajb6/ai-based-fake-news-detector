# verification.py

import wikipedia
from transformers import pipeline

# Load the Natural Language Inference model
nli_pipeline = pipeline("text-classification", model="roberta-large-mnli")

def fetch_wikipedia_summary(entity: str, sentences: int = 2) -> str | None:
    """Fetch a short summary for the given entity from Wikipedia."""
    try:
        return wikipedia.summary(entity, sentences=sentences)
    except Exception as e:
        print(f"[WARN] Wikipedia lookup failed for '{entity}': {e}")
        return None

def verify_claim(entity: str):
    try:
        summary = wikipedia.summary(entity, sentences=1)
        return "Real", summary
    except wikipedia.exceptions.DisambiguationError as e:
        return "Unknown", f"Multiple possible meanings: {e.options[:3]}"
    except wikipedia.exceptions.PageError:
        return "Fake", "No matching Wikipedia page found"
    except Exception as e:
        return "Unknown", f"Error: {str(e)}"