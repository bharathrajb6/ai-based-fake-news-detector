import wikipediaapi

wiki = wikipediaapi.Wikipedia(
    language="en",
    user_agent="AI-FakeNewsDetector/1.0 (bharathrajb26@gmail.com)"
)

def get_wikipedia_summary(query: str, max_chars: int = 500):
    """
    Retrieves a summary from Wikipedia for a given query.

    This function fetches the Wikipedia page corresponding to the query and
    returns a summary of the page's content, truncated to a specified maximum
    number of characters.

    Args:
        query: The search term for the Wikipedia page.
        max_chars: The maximum number of characters for the summary.

    Returns:
        The summary of the Wikipedia page as a string, or None if the page
        does not exist.
    """
    page = wiki.page(query)
    if page.exists():
        return page.summary[:max_chars]
    return None