#!/bin/bash

echo "启动交通信号控制系统服务端..."

java -jar traffic-signal-server/target/traffic-signal-server-1.0.0.jar \
  --spring.profiles.active=dev \
  --server.port=8080 \
  --tcp.server.port=9999