#!/bin/bash

echo "启动交通信号控制系统客户端..."

java -jar traffic-signal-client/target/traffic-signal-client-1.0.0.jar \
  --spring.profiles.active=dev \
  --server.port=8081 \
  --tcp.client.server-host=localhost \
  --tcp.client.server-port=9999 \
  --client.cross-id=110100001