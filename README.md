rhq-alerts
==========

rhq-msg
-------

- cd <rhq-msg> ; mvn clean install
- cd <rhq-msg-broker-wf-extension> ; mvn -Dorg.rhq.next.wildfly.home=/opt/tests/wildfly-8.2.0.Final.rhq-msg wildfly-extension:deploy
- cd <rhq-msg-ra-wf-module> ; mvn -Pdev -Dorg.rhq.next.wildfly.home=/opt/tests/wildfly-8.2.0.Final.rhq-msg install

rhq-alerts
----------

- cd <rhq-alerts> ; mvn clean install -Pdev -Dorg.rhq.next.wildfly.home=/opt/tests/wildfly-8.2.0.Final.rhq-msg

Tests
-----

- Send metrics to the bus

TIMESTAMP="$(($(date +%s%N)/1000000))" ; curl -X POST -H "Content-Type: application/json" --data "{\"metrics\" : [ { \"id\": \"Metric-01\", \"timestamp\" : $TIMESTAMP, \"value\" : 0.1} ]}" http://localhost:8080/rest/message/MetricsTopic
TIMESTAMP="$(($(date +%s%N)/1000000))" ; curl -X POST -H "Content-Type: application/json" --data "{\"metrics\" : [ { \"id\": \"Metric-01\", \"timestamp\" : $TIMESTAMP, \"value\" : 25.2}, { \"id\": \"Metric-02\", \"timestamp\" : $TIMESTAMP, \"value\" : 16.2} ]}" http://localhost:8080/rest/message/MetricsTopic

- Check alerts using REST api

curl http://localhost:8080/rhq-alerts/api/alerts

- Check alerts UI

http://localhost:8080/rhq-alerts