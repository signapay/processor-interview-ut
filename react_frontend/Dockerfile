# Use an official Node.js runtime as a parent image
FROM node:14-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code into the container
COPY . .

# Build the React app for production
RUN npm run build

# Serve the production build using a simple web server
RUN npm install -g serve

# Expose port 3000 to access the frontend
EXPOSE 3000

# Serve the build folder using the `serve` command
CMD ["serve", "-s", "build"]
