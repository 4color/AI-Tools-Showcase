# ========== 阶段 1：构建前端 ==========
FROM node:18-alpine AS frontend-builder
WORKDIR /app

COPY frontend/package.json frontend/package-lock.json* frontend/pnpm-lock.yaml* ./
RUN npm ci --legacy-peer-deps 2>/dev/null || npm install --legacy-peer-deps

COPY frontend/ .
ENV NODE_ENV=production
RUN npm run build

# ========== 阶段 2：构建后端 ==========
FROM maven:3.8-eclipse-temurin-8-alpine AS backend-builder
WORKDIR /build

COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

COPY backend/src ./src
RUN mvn package -DskipTests -B

# ========== 阶段 3：运行（Nginx + Java 同容器） ==========
FROM eclipse-temurin:8-jre-alpine
RUN apk add --no-cache nginx tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime 2>/dev/null || true

WORKDIR /app

# 后端 jar
COPY --from=backend-builder /build/target/backend-*.jar /app/app.jar

# 前端静态
COPY --from=frontend-builder /app/dist /usr/share/nginx/html

# Nginx 配置（单镜像内代理到 127.0.0.1:8080）
RUN mkdir -p /etc/nginx/conf.d
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY docker/start.sh /app/start.sh
RUN chmod +x /app/start.sh

EXPOSE 80

CMD ["/app/start.sh"]
