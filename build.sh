#!/bin/bash

echo "开始构建 GA/T 1049.2 项目..."

# 清理之前的构建
mvn clean

# 构建公共模块
echo "构建公共模块..."
cd ga-t1049-common
mvn clean install
cd ..

# 构建适配器公共模块
echo "构建适配器公共模块..."
cd device-adapters/adapter-common
mvn clean install
cd ../..

# 构建所有适配器
echo "构建设备适配器..."
cd device-adapters
mvn clean install
cd ..

# 构建服务端
echo "构建服务端..."
cd traffic-signal-server
mvn clean package
cd ..

# 构建客户端
echo "构建客户端..."
cd traffic-signal-client
mvn clean package
cd ..

echo "构建完成！"
echo "服务端JAR: traffic-signal-server/target/traffic-signal-server-1.0.0.jar"
echo "客户端JAR: traffic-signal-client/target/traffic-signal-client-1.0.0.jar"
echo "适配器JARs: device-adapters/*/target/*.jar"