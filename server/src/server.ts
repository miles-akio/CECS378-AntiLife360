import express, { Express, Request, Response } from "express";
import dotenv from "dotenv";

dotenv.config();
const PORT = process.env.PORT;

const app: Express = express();

app.get("/", (req: Request, res: Response) => {
    res.send("Hello from EXPRESS + TS!!!!!!");
});

app.listen(PORT, () => {
    console.log(`Now listening on port ${PORT}`);
});