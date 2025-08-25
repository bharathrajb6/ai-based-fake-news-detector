from kafka import KafkaConsumer, KafkaProducer
import json
from services.verification import verify_claim


def safe_deserializer(m):
    if not m:
        return None
    try:
        return json.loads(m.decode("utf-8"))
    except Exception as e:
        print(f"⚠️ Skipping invalid message: {m} | Error: {e}")
        return None


producer = KafkaProducer(
    bootstrap_servers="localhost:9092",
    value_serializer=lambda v: json.dumps(v).encode("utf-8")
)

consumer = KafkaConsumer(
    "claims",
    bootstrap_servers="localhost:9092",
    auto_offset_reset="earliest",
    group_id="java-service",
    value_deserializer=safe_deserializer
)

print("📡 Python service listening for claims...")

for message in consumer:
    data = message.value
    claim_test = data.get("claim")
    username = data.get("username")

    # Run fact check
    result, evidence = verify_claim(claim_test)

    response = {
        "claim": claim_test,
        "result": result,
        "evidence": evidence,
        "username":username
    }

    # Send back to claims-verified
    producer.send("claims-verified", response)
    print(f"Sent verified claim: {response}")