# we start from a Kafka image as the connector is in Kafka distribution
FROM wurstmeister/kafka:2.13-2.7.0

RUN curl https://d1i4a15mxbxib1.cloudfront.net/api/plugins/confluentinc/kafka-connect-elasticsearch/versions/11.1.0/confluentinc-kafka-connect-elasticsearch-11.1.0.zip -L -o /tmp/elastic-connector.zip

RUN mkdir -p /opt/kafka/plugins/ && unzip /tmp/elastic-connector.zip -d /opt/kafka/plugins/

# we replace the default connect-standalone.properties so we can properly resolve to our local Kafka docker development
ADD connect-standalone.properties /opt/kafka/config/

ADD connect-elastic-sink.properties /opt/kafka/config/

ADD wait-for.sh /usr/bin/

# we replace the start command creating a connector instead of starting a kafka broker.
ADD start-kafka.sh /usr/bin/

# permissions
RUN chmod a+x /usr/bin/start-kafka.sh
