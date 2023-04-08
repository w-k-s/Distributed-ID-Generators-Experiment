FROM nginx:1.23-alpine-slim

RUN apk add --upgrade curl

RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf