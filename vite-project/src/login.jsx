import {useState} from 'react'
import { useNavigate } from 'react-router-dom';
import axios from 'axios'



export default function Login(){

    const navigate = useNavigate();

    const [loginData, setLoginData] =  useState({
        Id: '',
        password: ''
    })

    function handleChange(e){
        const {name, value} = e.target;
        console.log(value);
        setLoginData(prevData => {
            return{
                ...prevData,
                [name]: value
            }
        })
    }

    const navigateToMain = () => {
        navigate("/main");
    }
   

    async function handleSubmit(e){
    
        e.preventDefault();
       
        try {
           const result = await axios.post('http://localhost:3000/authenticate-user', {
            Id: loginData.Id,
            password: loginData.password
           });

           if(result.data.success){
            localStorage.setItem('token', result.data.token);
            navigateToMain();
           } else {
            console.log("could not get token");
           }
        } catch (error) {
            console.log("could not validate login", error.message);
        
        }
    }



    return(
        <div className='flex justify-center items-center bg-background h-screen'>
            <form className='flex flex-col w-1/4 h-1/2 bg-white p-4 rounded-lg' onSubmit={handleSubmit}> 
                <input className='form--input'
                    type='text'
                    placeholder='ManagerId'
                    name='Id'
                    value={loginData.Id}
                    onChange={handleChange}
                />
                <input className='form--input'
                    type='password'
                    placeholder='password'
                    name='password'
                    value={loginData.password}
                    onChange={handleChange}
                />
                <div className='flex justify-center'>
                    <button className='border-2 rounded-md hover:bg-blue-700 py-2 bg-button w-1/2'>Submit</button>
                </div>
            </form>
        </div>
    )
}