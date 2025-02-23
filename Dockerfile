FROM tomcat:9-jdk17-openjdk-slim
ARG WAR_FILE=target/crypto-wallet-manager.war
# Renomeia para ROOT.war para que a aplicação seja deployada na raiz
COPY ${WAR_FILE} /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
