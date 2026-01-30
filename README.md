# 🤖 AI Tools Showcase

前后端分离的 AI 工具展示网站，支持工具展示、教程学习、提示词、API 集合、全站搜索与首页指标展示等功能。

## 🛠️ 技术栈

- **前端**: Vue 3 + Vite + TypeScript + Element Plus + Tailwind CSS + Pinia
- **后端**: Spring Boot 2.x + MyBatis-Plus + MySQL + JWT + Flyway
- **配置**: YAML 多环境配置（dev / test / prod）

## ✨ 功能概览

| 模块 | 说明 |
|------|------|
| **AI 工具** | 工具列表、分类筛选、关键词/价格筛选、详情、评论、点赞、我的工具 |
| **教程** | 教程列表、难度/分类筛选、详情、评论、我的教程 |
| **提示词** | 提示词列表、分类/标签、详情、封面/效果图、使用说明 |
| **API** | API 列表、分类、模型管理、详情 |
| **搜索** | 首页搜索框 → 全站搜索结果页，可跳转工具/教程/提示词/API 详情 |
| **首页** | Hero、搜索、热门标签、4 项指标（工具数/教程数/用户数/评分）、热门工具、CTA |
| **用户** | 注册、登录、个人中心、收藏与点赞 |
| **管理后台** | 工具/教程/提示词/API/用户管理、仪表盘统计（需 ADMIN 角色） |

## 🚀 快速启动

### 环境要求

- Java 8+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

### 一键启动

- **Windows**：在项目根目录执行 `start-all.bat`（会先后启动后端与前端，需本机已配置好 Java、Maven、Node 路径）。
- **Linux / macOS**：若存在 `start.sh`，可执行 `./start.sh`；否则请使用下方手动启动。

### 手动启动

1. 确保 MySQL 已启动并已创建数据库（如 `ai_tools_showcase`），Flyway 会在首次启动时自动执行迁移。
2. 启动后端与前端：

```bash
# 后端（端口 8080）
cd backend && mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 前端（新开终端，端口 3000）
cd frontend && npm install && npm run dev
```

## 🐳 Docker 启动（单镜像）

需已安装 Docker 与 Docker Compose。**一个镜像**内包含前端（Nginx 静态 + 代理）+ 后端（Spring Boot），与 MySQL 共 2 个服务。

```bash
# 构建并启动（MySQL + app 单镜像）
docker-compose up -d --build

# 查看日志
docker-compose logs -f app

# 停止
docker-compose down
```

- **访问**: http://localhost:3000（Nginx 80 映射到 3000，容器内 `/api` 代理到本机 Java 8080）
- 可选：复制 `.env.example` 为 `.env` 修改 `APP_PORT`、MySQL 密码等

## 🌐 访问地址

- **本地开发**：前端 http://localhost:3000，后端 http://localhost:8080（前端通过 Vite 代理 `/api` → 后端）
- **Docker**：单镜像 app（Nginx + Java），对外 http://localhost:3000，容器内 Nginx 代理 `/api` 到 Java

## 📂 项目结构

```
ai-tools-showcase/
├── frontend/                 # Vue 3 前端
│   ├── src/api/              # 接口封装（tool、tutorial、prompt、search、home 等）
│   ├── src/views/            # 页面（Home、Tools、Tutorials、Prompts、APIs、SearchResults、admin）
│   └── package.json
├── backend/                  # Spring Boot 后端
│   ├── src/main/java/        # Controller、Service、Mapper、Entity
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/     # Flyway 脚本（V1__init.sql 等）
├── Dockerfile                # 单镜像：前端构建 + 后端构建 + Nginx+Java 运行
├── docker/
│   ├── nginx.conf            # 单容器内 Nginx 配置（/api → 127.0.0.1:8080）
│   └── start.sh              # 启动 Java 与 Nginx
├── docker-compose.yml        # MySQL + app（单镜像）
├── start-all.bat             # 一键启动（Windows，若存在）
├── start.sh                  # 一键启动（Linux/macOS，若存在）
└── README.md
```

## ⚙️ 配置说明

- **后端**: `backend/src/main/resources/application.yml`
  - 数据库：MySQL 连接、用户名、密码
  - JWT：密钥与过期时间
  - 文件上传：目录与 URL 前缀
- **前端**: `frontend/public/env.development.js` / `env.production.js`（API 地址等）
- **环境**: 通过 `spring.profiles.active` 切换 dev / test / prod

## 📌 其他说明

- **首页指标**：4 项数据（AI 工具数、教程数、活跃用户、用户评分）由接口 `GET /stats` 提供。
- **全站搜索**：基于 `search_index` 表（工具/教程/提示词/API 的标题与简介），各栏目保存时自动同步索引。
- **管理后台**：需登录且角色为 `ADMIN`，否则会跳转登录页。
- **数据库**：默认连接 `application.yml` 中配置的 MySQL，首次启动由 Flyway 自动建表与迁移。
