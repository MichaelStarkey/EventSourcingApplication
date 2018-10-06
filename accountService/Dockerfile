FROM ubuntu:latest
MAINTAINER cssteam
RUN apt-get update -y
RUN apt-get install -y python3-pip python3-dev build-essential
COPY . /app
WORKDIR /app
RUN pip3 install .
ENV FLASK_APP accountService.run_app
ENV LC_ALL C.UTF-8
ENV LANG C.UTF-8
CMD flask run
