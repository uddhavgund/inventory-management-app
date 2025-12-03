# Step 1: Build Frontend
FROM node:18-alpine AS build
WORKDIR /app

# --- ADD THIS ---
# Accept VITE_API_URL at build time
ARG VITE_API_URL
ENV VITE_API_URL=${VITE_API_URL}
# -----------------

COPY package.json package-lock.json* ./
RUN npm install

COPY . .

# Build with correct backend URL
RUN npm run build

# Step 2: Serve using Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
