# Getting Started


## Create docker network
* docker network create -d bridge dupas_net

### database container

* docker run -v /opt/backup:/backup -p 3306:3306 --detach --name dupasdb  --restart unless-stopped --env MARIADB_ROOT_PASSWORD=BigSecret! --network dupas_net  mariadb:latest



### aplication.properties
* check sql connect string and change servername to dupasdb
* check game.image.repository and change it to /images/uploads (cfr the backend container build below)
* change spring profile if required


### Create database

* create database dupas CHARACTER SET utf8 COLLATE utf8_general_ci;
* CREATE USER 'dupas'@localhost IDENTIFIED BY 'dupas';
* CREATE USER 'dupas'@'%' IDENTIFIED BY 'dupas';
* GRANT ALL PRIVILEGES ON dupas.* TO dupas@localhost;
* GRANT ALL PRIVILEGES ON dupas.* TO dupas@'%';
* flush privileges;


### backend container
* git pull
* check the application.properties
* ./mvnw package -Dmaven.test.skip=true
* docker build  -t desijb/dupasbackend .
* docker run -v /opt/images:/images  -d -p 127.0.0.1:8080:8080 --restart unless-stopped --add-host=host.docker.internal:host-gateway --network dupas_net --name backend  desijb/dupasbackend
