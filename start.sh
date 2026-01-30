#!/bin/bash

echo "🚀 启动 AI Tools Showcase 项目"

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未运行，请先启动 Docker"
    exit 1
fi

# 启动 MySQL 和后端服务
echo "📦 启动服务..."
docker-compose up -d mysql backend

# 等待后端服务启动
echo "⏳ 等待后端服务启动..."
sleep 15

# 启动前端开发服务器
echo "🎨 启动前端开发服务器..."
cd frontend && npm run dev

echo "✅ 项目启动完成！"
echo "🌐 前端地址: http://localhost:3000"
echo "🔧 后端地址: http://localhost:8080"
echo "🗄️ 数据库: localhost:3306"