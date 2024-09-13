import express from 'express'
import cors from "cors";
import bodyParser from "body-parser";
import jwt from "jsonwebtoken";


const app = express();
const port = 3000;

app.use(cors()) 
app.use(bodyParser.json());



const users = [{Id: '1234', password: 'admin1234'}]

const jwtSecretKey = "ejfjdw9xmw1mxwjkc8s30md0eddwkde20jdf0kel3nlk93kidnw"

app.post("/authenticate-user", (req, res)=>{
   
    const {Id, password} = req.body
    const user = (users.find(u => u.Id === Id && u.password === password));

    if(user){

      const token = jwt.sign({ userId: Id }, jwtSecretKey, {
        expiresIn: '1h'
      })
      res.send({
        token: token, 
        success: true,
      })
    }else{
        res.send({message: "login not valid", sucess: false})
    }
})

app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
});