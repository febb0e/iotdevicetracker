# IoT Device Tracker

## Setup your dev environment
Install the latest version of [Docker Desktop](https://www.docker.com/get-started).
See the README.md files of the individual components for additional requirements.
## Docker compose
IoT Device Tracker needs a Postgres database and an InfluxDB database. Both can be launched in an isolated environment using `docker compose`.
Below, you find the credentials for the locally deployed development databases, as well as a list of useful commands.

### Postgres
* Adress: 127.0.0.1:5432
* Username: iotdt-dev
* Password: iotdt-dev

### InfluxDB
* Adress: 127.0.0.1:8086
* Username: iotdt-dev
* Password: iotdt-dev
* Org: thkoeln
* Bucket: iotdt

### Important commands
```sh
# Launch containers in background (you should stop these when no longer working on this application to not waste system resources)
docker compose up -d

# Stop containers
docker compose stop

# Completely remove containers and networks
docker compose down

# Like docker compose down, but also pernanently deletes all volumes
docker compose down -v
```
More information: https://docs.docker.com/compose/

##  Gitlab CI/CD Variablen
Variables allow you to configure the used Sonarqube Instance.
You have to adjust following variables for Sonarqube:

- `SONAR_HOST_URL`
- `SONAR_TOKEN`

Cypress can be configured via a Key:

- `CYPRESS_KEY`

Other Variables are utilized for publishing the Docker-Images to Docker-Hub and do not need to be changed usually.