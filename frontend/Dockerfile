FROM node:22-alpine AS builder

# Ajout des résolutions DNS dans l'image (pour éviter les erreurs EAI_AGAIN)
RUN echo "104.16.0.34 registry.npmjs.org" >> /etc/hosts && \
    echo "155.102.195.196 registry.npmmirror.com" >> /etc/hosts && \
    echo "155.102.195.191 cdn.npmmirror.com" >> /etc/hosts

# Configuration du registre npm vers le miroir chinois
RUN npm config set registry https://registry.npmmirror.com

WORKDIR /app
COPY package*.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]