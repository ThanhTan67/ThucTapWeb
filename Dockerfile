FROM tomcat:9.0-jdk17
# Copy file WAR vào thư mục webapps của Tomcat
COPY target/ThucTapWeb.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]
