import { Router } from "express";
import { uploadTransactions, resetTransactions, getAccounts, getCollections, getBadTransactions } from "../controller/transactionController.js";
import { verifyApiKey } from "../middleware/apiKeyMiddleware.js";
import multer from "multer";

const router = Router();
const upload = multer();

//post routes
router.post('/upload-transactions', verifyApiKey, upload.single('file'), uploadTransactions);

//delete routes
router.delete('/clear-transactions', verifyApiKey, resetTransactions);

//get routes
router.get('/get-accounts', verifyApiKey, getAccounts);
router.get('/get-collections', verifyApiKey, getCollections);
router.get('/get-bad-transactions', verifyApiKey, getBadTransactions);


export default router;
