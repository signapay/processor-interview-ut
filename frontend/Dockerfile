# Stage 1: Base development environment
FROM node:22.9.0 AS dev

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and install dependencies
COPY package.json ./
RUN npm install

# Copy all source code files
COPY . .

# Expose the port for development (3000)
EXPOSE 3000

# Start the React development server with hot-reloading enabled
CMD ["npm", "start"]

# Stage 2: Production build
FROM node:22.9.0 AS build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 3: Serve the React app using Nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

# Stage 1: Build the React App using Node.js 22
# FROM node:22.9.0 AS build

# Set the working directory inside the container
# WORKDIR /app

# Copy package.json and install dependencies
# COPY package.json ./
# RUN npm install

# Copy all source code files and build the React app
# COPY . .
# RUN npm run build

# Stage 2: Serve the React App using Nginx
# FROM nginx:alpine

# Copy the build files from the previous stage to Nginx's HTML folder
# COPY --from=build /app/build /usr/share/nginx/html

# Expose port 80 for the Nginx server
# EXPOSE 80

# Start Nginx server
# CMD ["nginx", "-g", "daemon off;"]
