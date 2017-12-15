FROM 10.3.98.241/library/java:8-alpine
MAINTAINER xuyf "xuyf@huntkey.net"
ENV TIME_ZONE Asia/Shanghai
RUN apk add --no-cache tzdata && echo "${TIME_ZONE}" > /etc/timezone && ln -sf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime
VOLUME /tmp
ADD rx-eurake-server-02-1.0-SNAPSHOT.jar app.jar
EXPOSE 1111
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--eureka.instance.hostname=0.0.0.0","--eureka.client.serviceUrl.defaultZone=http://${RX_EURAKE_SERVER_01_SERVICE_SERVICE_HOST}:${RX_EURAKE_SERVER_01_SERVICE_SERVICE_PORT}/eureka/,http://${RX_EURAKE_SERVER_03_SERVICE_SERVICE_HOST}:${RX_EURAKE_SERVER_03_SERVICE_SERVICE_PORT}/eureka/"]