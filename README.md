# Stream and analyse Auth Events

## Quickstart
```
sudo docker-compose up
```


## Flink

### Cluster
https://ci.apache.org/projects/flink/flink-docs-stable/docs/deployment/resource-providers/standalone/docker/#flink-with-docker-compose

Flink runs as Session Cluster (see docker-compose.yaml)

Ui: http://localhost:8081 

### Application
Create Flink application skeleton
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





