FROM tomcat:8.5.34-jre8-alpine

RUN rm -rf /usr/local/tomcat/webapps/*

ADD target/containerbank.war /usr/local/tomcat/webapps/ROOT.war

