FROM maven:3.8.2-openjdk-11 AS builder

WORKDIR /build/authEventsAnalyse
COPY ./authEventsAnalyse ./  
RUN mvn clean install 


FROM flink:1.13.0-scala_2.11

COPY --from=builder /build/authEventsAnalyse/target/authEventsAnalyse-0.1.jar /opt/flink/usrlib/authEventsAnalyse-0.1.jar