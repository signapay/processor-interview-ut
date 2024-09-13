import { Route, Routes, Navigate, BrowserRouter as Router} from 'react-router-dom';
import {useState} from 'react'
import Login from './login'
import MainContent from './main-content';


function App() {

  const [token, setToken] = useState(localStorage.getItem('token'))
  
  return(
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        {token ? <Route path="/main" element={<MainContent />} /> : <Route path="/" element={<Login />} />}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </Router>
  )
 
}

export default App
