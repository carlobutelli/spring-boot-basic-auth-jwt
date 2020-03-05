FROM gradle:6.1.1-jdk13

MAINTAINER Carlo Butelli <carlo.butelli@gmail.com>

ADD . /code
WORKDIR /code

COPY --chown=gradle:gradle . /code

RUN gradle clean build --no-daemon

EXPOSE 8080

RUN chmod 755 /code/docker-entrypoint.sh
ENTRYPOINT ["/code/docker-entrypoint.sh"]
CMD ["run"]
