#export EZYFOX_SERVER_HOME=
mvn -pl . clean install
mvn -pl QworldServer-common -Pexport clean install
mvn -pl QworldServer-app-api -Pexport clean install
mvn -pl QworldServer-app-entry -Pexport clean install
mvn -pl QworldServer-plugin -Pexport clean install
cp QworldServer-zone-settings.xml $EZYFOX_SERVER_HOME/settings/zones/
