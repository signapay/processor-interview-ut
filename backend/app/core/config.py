import os
from pathlib import Path
from dotenv import load_dotenv
from pydantic_settings import BaseSettings

# Get the project root directory
PROJECT_ROOT = Path(__file__).parent.parent.parent

# Load the .env file
load_dotenv(PROJECT_ROOT / ".env")

class Settings(BaseSettings):
    PROJECT_NAME: str = "Transaction Processor"
    PROJECT_VERSION: str = "1.0.0"
    API_PREFIX: str = "/api"
    DEBUG: bool = False
    API_KEY: str = os.getenv("API_KEY")

    class ConfigDict:
        env_file = str(PROJECT_ROOT / ".env")

settings = Settings()
