# 🚀 AI Tools Showcase 快速启动

## 📁 核心文件

| 文件名 | 功能 | 说明 |
|-------|------|------|
| `start-all.bat` | 🚀 一键启动 | **主要使用这个** |
| `start-backend.bat` | 🔧 仅启动后端 | 单独启动后端服务 |
| `stop-services.bat` | 🛑 停止服务 | 停止所有开发服务 |
| `check-environment.bat` | 🔍 环境检查 | 检查Java/Maven/Node.js环境 |
| `start-all.ps1` | 💻 PowerShell版 | 备选启动方式 |

## ⚡ 快速开始

### 1. 环境检查
```bash
check-environment.bat
```

### 2. 一键启动
```bash
start-all.bat
```

### 3. 停止服务
```bash
stop-services.bat
```

## 🎯 环境要求

- **Java 17+** 
- **Maven 3.9.8** (路径: C:\dev\apache-maven-3.9.8)
- **Node.js 18+**
- **MySQL 8.0+** (已有)

## ⚙️ Maven 配置

Maven 路径已配置为：`C:\dev\apache-maven-3.9.8`

如需修改，编辑 `maven-config.txt` 文件或批处理文件中的 `MAVEN_HOME` 变量。

## 🌐 访问地址

启动成功后：
- **前端**: http://localhost:3000
- **后端**: http://localhost:8080

## 📋 数据库

确保数据库 `ai_tools_showcase` 已存在，或参考 `backend/src/main/resources/data.sql` 创建示例数据。

## 🔧 配置文件

- **后端配置**: `backend/src/main/resources/application.yml`
- **前端配置**: `frontend/vite.config.ts`

---

**有问题？** 所有脚本都使用 GBK 编码，确保命令不会乱码。