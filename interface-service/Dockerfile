FROM node:carbon
MAINTAINER cssteam
WORKDIR /usr/src/interface-service
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 8080
CMD ["npm", "start"]
