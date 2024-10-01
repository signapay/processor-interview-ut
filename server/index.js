import cors from "cors";
import dotenv from "dotenv";
import express from "express";
import bodyParser from "body-parser";
import cookieParser from "cookie-parser";
import transactionRoute from "./api/routes/transactionRoute.js"; 

dotenv.config();
const app = express()
const PORT = 5021

// CORS configuration
const corsOptions = {
    origin: 'http://localhost:5173',
    credentials: true,
    optionsSuccessStatus: 200
};

//Middleware
app.use(cookieParser());
app.use(cors(corsOptions));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

//Routes
app.use("/api/routes/transactions", transactionRoute);


app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
})
