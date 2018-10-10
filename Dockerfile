FROM tomcat:8.5.34-jre8-alpine

ADD target/containerbank.war /usr/local/tomcat/webapps/

