# 🤖 AI Tools Showcase

前后端分离的 AI 工具展示网站，支持工具展示、教程学习、API 集合等功能。

## 🛠️ 技术栈

- **前端**: Vue3 + Vite + TypeScript + Element Plus
- **后端**: Spring Boot + MySQL + JWT
- **配置**: YAML 多环境配置

## 🚀 快速启动

### 环境要求
- Java 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

### 一键启动
```bash
start-all.bat
```

### 其他命令
```bash
check-environment.bat    # 环境检查
start-backend.bat        # 仅启动后端
stop-services.bat        # 停止服务
```

## 🌐 访问地址

- **前端**: http://localhost:3000
- **后端**: http://localhost:8080

## 📂 项目结构

```
ai-tools-showcase/
├── frontend/          # Vue3 前端
├── backend/           # Spring Boot 后端
├── start-all.bat      # 一键启动 ⭐
└── README.md         # 项目说明
```

## ⚙️ 配置说明

- **后端配置**: `backend/src/main/resources/application.yml`
- **支持环境**: default/dev/test/prod
- **数据库**: MySQL (已有环境) / H2 (测试)