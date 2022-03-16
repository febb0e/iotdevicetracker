# Daemon
## Recommended IDE
GoLand: https://www.jetbrains.com/de-de/go/ 

## Setup your Dev environment
1. Install [GoLang 1.17](https://golang.org/dl/)
2. Install [GoLand](https://www.jetbrains.com/de-de/go/)
3. Configure GOROOT and GOPATH for [GoLand](https://www.jetbrains.com/help/go/configuring-goroot-and-gopath.html)
4. Clone the `daemon` Repo
5. Open the Repo in your IDE and issue `go get` to install all dependencies
   1. You can update all dependencies to current versions with `go get -u`
   2. Clean up dependencies with `go mod tidy`
6. You are ready to go :)

## Build the daemon 
1. Change into `./daemon` directory
2. Use below commands to build daemon e.g `env GOOS=linux go build .`
3. The daemon binary will be placed in the repo folder
4. Copy daemon to target device e.g using scp or ssh

## Create Device and start Daemon
1. Visit Webservice e.g. www.example.com
2. Create Device
3. Enter MAC-Address 
   1. e.g on Linux issue `ip addr`
   2. Use MAC-Address from wlp2s0 interface
4. Copy and save device token
5. Start daemon and write metrics to Webservice with: 
```shell
MQTT_URL="wss://mosquitto.example.com" MQTT_PASSWORD="<Your_Password>" ./daemon
```
6. Metrics are sent to the Webservice if the device shows output like:
````shell
2022/02/11 21:12:15 Daemon successfully launched...
MQTT connection up...
msg: {"os":"linux","mac":"aa:bb:cc:dd:ee:ff","cpu_load":15.7}
````

## Setup and example config for Raspberry Pi 
1. Install an [OS](https://www.raspberrypi.com/software/operating-systems/) on Raspberry SD-Card
   1. We recommend Raspberry Pi OS Lite
2. Build the daemon for Raspberry on your dev machine `env GOOS=linux GOARCH=arm GOARM=7 go build .`
3. Configure Internet Access and SSH on the Raspberry Pi
4. Copy daemon from your dev machine to Raspberry Pi by issuing `scp pi@192.168.1.1:/home/path/to/daemon/daemon /home/pi` (use Raspberrys local IP)
5. Daemon-binary should be placed into `/home/pi` folder on the Raspberry
6. Create a service e.g. `iotdt-daemon.service` on the Raspberry with `nano /etc/systemd/system/iotdt-daemon.service`

Below is an example service config:
```shell
[Unit]
Description=IOT Device Tracker daemon
After=network.target

[Service]
Type=simple
Restart=always
RestartSec=5
User=pi
Group=pi
ExecStart=/home/pi/daemon
Environment="MQTT_URL=wss://mosquitto.example.com"
Environment="MQTT_PASSWORD=<Your_Password>"

[Install]
```
Now the daemon on the Raspberry Pi will be launched on startup of the Raspberry Pi.

## Important commands
```shell
# Build for current OS and ARCH
go build .

# Build for specific OS and ARCH
env GOOS=os GOARCH=arch go build .

# Build and run
Run: go run .
```

## Useful links
* GoLang Docs: https://golang.org/doc/