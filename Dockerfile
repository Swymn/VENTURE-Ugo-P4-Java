FROM mysql:8.0.17

ENV MYSQL_ROOT_PASSWORD=rootroot

COPY ./resources/Data.sql /docker-entrypoint-initdb.d/

EXPOSE 3306