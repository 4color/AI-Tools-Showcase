#!/bin/sh
set -e
# 先启动后端（后台），再启动 Nginx（前台）
java -jar /app/app.jar &
exec nginx -g "daemon off;"
