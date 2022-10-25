#/bin/sh

REALM=test
CONTAINER_ID=$(docker ps -q --filter ancestor=quay.io/keycloak/keycloak:15.0.2)

docker exec -e JDBC_PARAMS='?useSSL=false'  -ti $CONTAINER_ID  /opt/jboss/keycloak/bin/standalone.sh -Djboss.socket.binding.port-offset=102 -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.realmName=rapurc -Dkeycloak.migration.usersExportStrategy=REALM_FILE -Dkeycloak.migration.file=/tmp/my_realm.json
docker cp $CONTAINER_ID:/tmp/my_realm.json /tmp/my_realm.json
cp /tmp/my_realm.json src/test/resources/kc.json
