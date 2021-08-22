#!/bin/bash -e

# connector start command here.
exec wait-for.sh --timeout=1000 elasticsearch:9200 -- /opt/kafka/bin/connect-standalone.sh /opt/kafka/config/connect-standalone.properties /opt/kafka/config/connect-elastic-sink.properties
