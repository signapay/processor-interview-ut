import logging
from fastapi import FastAPI, Depends, HTTPException, Security
from fastapi.security.api_key import APIKeyHeader, APIKey
from app.core.config import settings
from app.api.routes import router as api_router

# Setup logging
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)  # This creates a logger named "app.main"

# Setup FastAPI
app = FastAPI(title=settings.PROJECT_NAME, version=settings.PROJECT_VERSION)
@app.get("/")
async def root():
    return {"message": "Transaction Processor API"}

# Setup API Key
API_KEY_NAME = "X-API-Key"
api_key_header = APIKeyHeader(name=API_KEY_NAME, auto_error=False)
async def get_api_key(api_key_header: str = Security(api_key_header)):
    if api_key_header == settings.API_KEY:
        return api_key_header
    raise HTTPException(status_code=403, detail="Could not validate API Key")

# Include the API router with authentication
app.include_router(
    api_router,
    prefix=settings.API_PREFIX,
    dependencies=[Depends(get_api_key)]
)