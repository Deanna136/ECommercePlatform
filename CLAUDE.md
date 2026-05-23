# E-Commerce Platform - 综合型电商平台

## 项目概述

一个综合型电商平台，支持买家端、卖家端、管理员端三端功能。

## 技术栈

### 后端
- Java 17, Spring Boot 3.x / 2.7.x
- MyBatis ORM, MySQL 8.x
- Redis 6.x+ (Lettuce 连接池)
- JWT (jjwt 0.9.1) 认证鉴权
- 腾讯云 COS 对象存储
- Maven 构建

### 前端
- Vue 3 + Element Plus
- Vue CLI 5.x, Axios
- Node.js 16.x/18.x

### 端口
- 后端: `8080`, 前端: `8081`

## 项目结构

```
backend/                          # Spring Boot 后端
  src/main/java/com/              # Java 源码
vuefront/ecommerceplatform/       # Vue 3 前端
  买家端接口文档.md                # 买家 API 文档
  卖家接口说明.md                  # 卖家 API 文档
  管理员接口说明.md                # 管理员 API 文档
  数据库表结构.md                  # 数据库设计文档
```

## 常用命令

### 后端
```bash
cd backend && mvn spring-boot:run       # 启动后端 (8080)
cd backend && mvn test                   # 运行测试
cd backend && mvn clean package          # 打包
```

### 前端
```bash
cd vuefront/ecommerceplatform && npm run serve    # 启动前端 dev (8081)
cd vuefront/ecommerceplatform && npm run build    # 生产构建
cd vuefront/ecommerceplatform && npm run lint     # 代码检查
```

## 认证方案

三种角色使用不同的 JWT token:
- `admin_token` - 管理员
- `buyer_token` - 买家
- `seller_token` - 卖家

Token 有效期 2 小时 (7200000ms)，放入请求头进行鉴权。

## 数据库连接

- 地址: www.ylxteach.net:3366
- 数据库名: ecommerce_platform
- 驱动: com.mysql.cj.jdbc.Driver

## 约定

- API 文档使用 Knife4j (Swagger)
- 遵循 RESTful 风格
- 远程数据库和 Redis，无需本地启动
