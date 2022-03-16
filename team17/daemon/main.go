package main

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/eclipse/paho.golang/autopaho"
	"github.com/eclipse/paho.golang/paho"
	"log"
	"net"
	"net/url"
	"os"
	"os/exec"
	"os/signal"
	"regexp"
	"runtime"
	"strconv"
	"strings"
	"sync"
	"syscall"
	"time"
)

func main() {
	var system = runtime.GOOS
	var name = ""
	var args = ""
	var cmd = ""
	var topic = "daemon/metrics"
	var macaddress = ""
	var macos = false
	var windows = false

	// Will evaluate the underlying System on which the daemon is running.
	// Depending on the underlying OS a command for fetching the current CPU load is issued.
	// MacOs and Linux implementations are working.
	// Windows not yet properly implemented as the command needs System rights.

	switch system {
	case "android":
		fmt.Println("android not yet supported for 'IoT Device Tracker' daemon :(")
		break
	case "darwin":
		name = "bash"
		args = "-c"
		cmd = "ps -A -o %cpu | awk '{s+=$1} END {print s}'"
		macos = true
		mac, err := getMacAddress()
		if err != nil {
			log.Fatal(err)
		}
		macaddress = mac[0]
		break
	case "ios":
		fmt.Println("iOS not yet supported for 'IoT Device Tracker' daemon :(")
		break
	case "linux":
		name = "bash"
		args = "-c"
		cmd = "top -b -n2 -p 1 | fgrep \"Cpu(s)\" | tail -1 | awk -F'id,' -v prefix=\"$prefix\" '{ split($1, vs, \",\"); v=vs[length(vs)]; sub(\"%\", \"\", v); printf \"%s%.1f\\n\", prefix, 100 - v }'"
		mac, err := getMacAddress()
		if err != nil {
			log.Fatal(err)
		}
		macaddress = mac[0]
		break
	case "windows":
		name = "Get-WmiObject"
		args = "Win32_Processor | Measure-Object -Property LoadPercentage -Average | Select Average"
		cmd = ""
		windows = true
		mac, err := getMacAddress()
		if err != nil {
			log.Fatal(err)
		}
		macaddress = mac[0]
		break
	default:
		fmt.Println("env not in list of supported os/architectures")
	}

	log.Println("Daemon successfully launched...")

	// Loads MQTT-client configuration
	cfg, err := loadConfig()
	if err != nil {
		panic(err)
	}

	// Loads configuration for Eclipse Paho client.
	// Connection is considered successful, if client receives MQTT connack package.
	// Basic console logging is configured to show disconnects while transmitting MQTT packages.

	pahoCfg := autopaho.ClientConfig{
		BrokerUrls:        []*url.URL{cfg.serverURL},
		KeepAlive:         cfg.keepAlive,
		ConnectRetryDelay: cfg.connectRetryDelay,
		OnConnectionUp: func(manager *autopaho.ConnectionManager, connack *paho.Connack) {
			fmt.Println("MQTT connection up...")
		},
		OnConnectError: func(err error) {
			fmt.Printf("error whilst attempting connection: %s\n", err)
		},
		Debug: paho.NOOPLogger{},
		ClientConfig: paho.ClientConfig{
			ClientID: "daemon-" + cfg.username,
			OnServerDisconnect: func(d *paho.Disconnect) {
				if d.Properties != nil {
					fmt.Printf("server requested disconnect: %s\n", d.Properties.ReasonString)
				} else {
					fmt.Printf("server requested disconnect; reason code: %d\n", d.ReasonCode)
				}
			},
			OnClientError: func(err error) {
				fmt.Printf("server requested disconnect: %s\n", err)
			},
		},
	}

	pahoCfg.SetUsernamePassword(cfg.username, []byte(cfg.password))

	// Changes logging level if debug is enabled in config
	if cfg.debugEnabled {
		pahoCfg.Debug = logger{prefix: "autoPaho"}
		pahoCfg.PahoDebug = logger{prefix: "paho"}
	}

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// Connect to the broker - this will return immediately after initiating the connection process
	cm, err := autopaho.NewConnection(ctx, pahoCfg)
	if err != nil {
		panic(err)
	}

	wg := sync.WaitGroup{}

	// Start off a goRoutine that publishes messages
	wg.Add(1)
	go func() {
		defer wg.Done()

		type Metrics struct {
			Os      string  `json:"os"`
			Mac     string  `json:"mac"`
			CpuLoad float64 `json:"cpu_load"`
		}

		for {

			// AwaitConnection will return immediately if connection is up.
			// Adding this call stops publication whilst connection is unavailable.

			err = cm.AwaitConnection(ctx)
			// In case of context cancellation
			if err != nil {
				fmt.Printf("publisher done (AwaitConnection: %s)\n", err)
				return
			}

			// Receives ouput of bash command to receive cpu_load
			output, err := exec.Command(name, args, cmd).Output()
			temp := string(output)
			var out = ""

			// Processes output depending on underlying OS, as outputs vary
			if macos {
				var numCpu = runtime.NumCPU()
				var cores = float64(numCpu)
				temp = strings.Trim(temp, "\n")
				floatOutput, err := strconv.ParseFloat(temp, 64)
				if err != nil {
					log.Fatal(err)
				}
				temp2 := floatOutput / cores
				out = fmt.Sprint(temp2)
			}
			if windows {
				re := regexp.MustCompile(`\d+`)
				out = string(re.Find([]byte(temp)))
			} else {
				out = string(output)
				out = strings.Trim(out, "\n")
			}
			if err != nil {
				log.Fatal(err)
			}

			// Converts cpu_load string into floating point number with 64 bit precision
			cpuLoad, err := strconv.ParseFloat(out, 64)

			// Creates a JSON message format with parameters: os, mac, cpuload
			msg, err := json.Marshal(Metrics{Os: system, Mac: macaddress, CpuLoad: cpuLoad})

			fmt.Printf("msg: %s\n", string(msg))
			if err != nil {
				panic(err)
			}

			// Publish will block so we run it in a goRoutine
			go func(msg []byte) {
				pr, err := cm.Publish(ctx, &paho.Publish{
					QoS:     1,
					Topic:   topic,
					Payload: msg,
				})
				if err != nil {
					fmt.Printf("error publishing: %s\n", err)
				} else if pr.ReasonCode != 0 && pr.ReasonCode != 16 { // 16 = Server received message but there are no subscribers
					fmt.Printf("reason code %d received\n", pr.ReasonCode)
				}

				fmt.Printf("sent message: %s\n", msg)
			}(msg)

			select {
			case <-time.After(cfg.delayBetweenMessages):
			case <-ctx.Done():
				fmt.Println("publisher done")
				return
			}
		}
	}()

	// Wait for a signal before exiting
	sig := make(chan os.Signal, 1)
	signal.Notify(sig, os.Interrupt)
	signal.Notify(sig, syscall.SIGTERM)

	<-sig
	fmt.Println("signal caught - exiting")
	cancel()

	wg.Wait()
	fmt.Println("shutdown complete")
}

// Returns a list of device interfaces.
// Usually the interface with index 0 includes the local MAC-address.

func getMacAddress() ([]string, error) {
	interfaces, err := net.Interfaces()
	if err != nil {
		log.Fatal(err)
	}
	var interfaceList []string
	for _, singleInterface := range interfaces {
		out := singleInterface.HardwareAddr.String()
		if out != "" {
			interfaceList = append(interfaceList, out)
		}
	}
	return interfaceList, nil
}
