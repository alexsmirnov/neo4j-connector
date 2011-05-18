#!/bin/sh
if [ "X${GLASSFISH_HOME}" != "X" ]; then
ASADM=${GLASSFISH_HOME}/bin/asadmin
else
ASADM=asadmin
fi
$ASADM "$@" <<-EOF
# create neo4j resource connection
deploy --force target/neo4j-connector-0.1-SNAPSHOT.rar
create-connector-connection-pool --raname=neo4j-connector-0.1-SNAPSHOT --connectiondefinition=com.netoprise.neo4j.connection.Neo4JConnectionFactory  --property=xa=true:dir=\$\{com.sun.aas.instanceRoot\}/lib/databases/neo4j Neo4jPool
create-connector-resource --poolname=Neo4jPool /eis/Neo4j
EOF
