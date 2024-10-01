/*
As a middleware I am just checking a token sent from the frontend 
as there is not login functionality as when a user login we can 
create a JWT token and save it in an HTTP-only cookie for session mangement
*/
const verifyApiKey = (req, res, next) => {
    const apiKey = req.headers['api-key'];
  
    const expectedApiKey = '0DvG1LKgEs7Y0RBX';
  
    if (!apiKey || apiKey !== expectedApiKey) {
      return res.status(403).json({ message: 'Forbidden: Invalid API Key' });
    }
  
    next();
};
  
export { verifyApiKey };
  