from services.retrieval import get_wikipedia_summary
from services.verification import verify_claim


def check_fact(claim: str):
    """
    Checks a given factual claim by retrieving evidence and verifying it.

    This function takes a claim, extracts the subject, retrieves a summary from
    Wikipedia as evidence, and then uses a Natural Language Inference (NLI)
    model to verify the claim against the evidence.

    Args:
        claim: The factual claim to be checked.

    Returns:
        The result of the verification from the NLI model.
    """
    # Extract main subject for search (naive: first 3 words)
    subject = " ".join(claim.split()[:3])
    
    # Step 1: Retrieve evidence
    evidence = get_wikipedia_summary(subject)
    
    # Step 2: Verify with NLI
    result = verify_claim(claim, evidence)
    
    return result