package main

import (
	"encoding/json"
	"fmt"
	"math/rand"
	"os"
	"strconv"
	"time"

	"github.com/confluentinc/confluent-kafka-go/kafka"
)

const (
	broker = "kafka1:9092"
)

var (
	lastTime int64 = 1546300800
)

func main() {

	p, err := kafka.NewProducer(&kafka.ConfigMap{"bootstrap.servers": broker})

	if err != nil {
		fmt.Printf("Failed to create producer: %s\n", err)
		os.Exit(1)
	}

	fmt.Printf("Created Producer %v\n", p)

	topic := "AuthenticationEvents"

	for {
		delay := time.Duration(randomInt(0, 250)) * time.Millisecond
		time.Sleep(delay)
		deliveryChan := make(chan kafka.Event)

		authEvent := AuthenticationEvent{
			randomUser(),
			randomIp(),
			randomSuccess(),
			incrementTime(delay),
		}

		//fmt.Printf("Created message %v\n", authEvent)

		authEventAsJson, _ := json.Marshal(&authEvent)

		err = p.Produce(&kafka.Message{
			TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
			Key:            []byte(authEvent.Username),
			Value:          []byte(authEventAsJson),
			//Headers:        []kafka.Header{{Key: "myTestHeader", Value: []byte("header values are binary")}},
		}, deliveryChan)

		if err != nil {
			fmt.Printf("Failed to produce message: %s\n", err)
			close(deliveryChan)
			continue
		}

		e := <-deliveryChan
		m := e.(*kafka.Message)

		if m.TopicPartition.Error != nil {
			fmt.Printf("Delivery failed: %v\n", m.TopicPartition.Error)
		} else {
			fmt.Printf("Delivered message %v to topic %s partition:%d at offset %v\n", authEvent, *m.TopicPartition.Topic, m.TopicPartition.Partition, m.TopicPartition.Offset)
		}

		close(deliveryChan)
	}
}

func randomIp() string {
	ips := []string{
		"250.49.72.219",
		"85.159.138.249",
		"6.150.225.82",
		"201.7.245.52",
		"20.96.105.183",
		"103.153.95.117",
		"77.6.154.125",
		"63.91.46.43",
		"85.83.94.78",
		"11.242.222.195",
		"226.48.11.121",
		"6.119.135.223",
		"83.144.109.111",
		"251.36.237.93",
		"247.133.226.190",
	}
	idx := randomInt(0, len(ips)-1)
	return ips[idx]
}

func randomUser() string {
	return "User_" + strconv.Itoa(randomInt(1, 1000))
}

func randomSuccess() bool {
	return randomInt(0, 10) == 0
}

func incrementTime(delay time.Duration) int64 {
	lastTime = lastTime + int64(delay.Milliseconds())
	return lastTime
}

func randomInt(min int, max int) int {
	return rand.Intn(max-min) + min
}
