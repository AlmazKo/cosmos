FROM node:17-slim
COPY web /app/
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
#COPY package.json ./
RUN cat package.json
RUN npm install
EXPOSE 1234
CMD ["npm", "run", "watch"]
