# Stream and analyse Auth Events

## Quickstart
```
sudo docker-compose -f docker-compose.flink.yml -f docker-compose.kafka.yml -f docker-compose.storage.yml up --build
```

## Pipleline
![pipeline](EnrichAuthenticationEventPipline.png "pipeline")

## Flink

### Cluster
https://ci.apache.org/projects/flink/flink-docs-stable/docs/deployment/resource-providers/standalone/docker/#flink-with-docker-compose

Flink runs as Application Cluster

Ui: http://localhost:8081 

### Application
Created Flink application skeleton with
```
mvn archetype:generate \
    -DarchetypeGroupId=org.apache.flink \
    -DarchetypeArtifactId=flink-walkthrough-datastream-java \
    -DarchetypeVersion=1.13.0 \
    -DgroupId=de.denniskniep \
    -DartifactId=authEventsAnalyse \
    -Dversion=0.1 \
    -Dpackage=autheventsanalyse \
    -DinteractiveMode=false
```

## Kafka
https://docs.confluent.io/platform/current/kafka/introduction.html
https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html

Ui: http://localhost:9021

Debugging with KafkaCat
https://docs.confluent.io/platform/current/app-development/kafkacat-usage.html


## Monitoring
With Grafana & Prometheus

Grafana Ui: http://localhost:3000 (admin:admin)
Prometheus Ui: http://localhost:9090

Kafka Monitoring: 
https://www.confluent.io/blog/monitor-kafka-clusters-with-prometheus-grafana-and-confluent/#prometheus


## ML Training with JUPYTER
```
cd jupyter-ml-training
sudo docker-compose up --build
```

To open the Ui look into the console output and open the url with the token

