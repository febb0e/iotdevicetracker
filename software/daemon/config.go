package main

import (
	"log"
	"net/url"
	"os"
	"time"
)

type config struct {
	// Bool: enable debug mode
	debugEnabled bool
	// MQTT server URL
	serverURL *url.URL
	// credentials for server connection
	username string
	password string

	// Seconds between keepalive packets
	keepAlive uint16
	// Period between connection attempts
	connectRetryDelay time.Duration
	// Period between publishing messages
	delayBetweenMessages time.Duration
}

// Loads the MQTT Broker URL from configured ENV variables.
// Grabs device MAC-address as username to provide a unique identifier.
// Creates MQTT-client default config.

func loadConfig() (config, error) {
	serverURL, err := url.Parse(os.Getenv("MQTT_URL"))
	if err != nil {
		panic(err)
	}

	mac, err := getMacAddress()
	if err != nil {
		log.Fatal(err)
	}

	macaddress := mac[0]

	return config{
		debugEnabled:         true,
		serverURL:            serverURL,
		username:             macaddress,
		password:             os.Getenv("MQTT_PASSWORD"),
		keepAlive:            30,
		connectRetryDelay:    time.Second * 10,
		delayBetweenMessages: time.Second,
	}, nil
}
