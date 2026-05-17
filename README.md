# 综合型电商平台 · 项目配置与开发
## 一、技术栈概览

### 后端

| 层次 | 技术 | 版本要求 |
|------|------|---------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.x / 2.7.x |
| ORM | MyBatis |  |
| 数据库 | MySQL | 8.x |
| 缓存 | Redis | 6.x+ |
| 认证 | JWT（jjwt） | 0.9.1 |
| 对象存储 | 腾讯云 COS | cos_api:5.6.80 / cos-sts-java:3.0.8 |
| 构建工具 | Maven | 3.6.x+ |
| API 文档	| Knife4j |	3.0.2 |

### 前端

| 层次 | 技术 | 版本要求 |
|------|------|---------|
| 框架 | Vue 3 | 3.x |
| UI 组件库 | Element Plus | 2.x |
| 构建工具 | Vue CLI（@vue/cli-service） | 5.x |
| 包管理器 | npm 或 yarn | npm 8+ / yarn 1.x |
| 运行环境 | Node.js | 16.x 或 18.x（推荐） |
| HTTP 客户端 | Axios | 1.x |

### 端口规划

| 服务 | 端口 | 说明 |
|------|------|------|
| 后端 Spring Boot | `8080` | API 服务 |
| 前端 Vue Dev Server | `8081` | 开发调试页面 |

---

## 二、推荐开发平台

| 前端 | vscode |
|------|------|
| 后端 | idea |

## 三、其他

### **1.Mysql数据库信息**

驱动：com.mysql.cj.jdbc.Driver

地址：www.ylxteach.net

端口：3366

数据库名：ecommerce_platform

用户名：test

密码：123456

连接URL：
jdbc:mysql://www.ylxteach.net:3366/ecommerce_platform?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8

### **2.redis信息**

地址：121.4.24.200

端口：6379

密码：123456

使用库：0

超时时间：5000ms

连接池：lettuce 连接池

### **3.Tencent cos腾讯云cos信息**

地域：ap-chengdu （成都）

存储桶名称：e-commerce-platform-1332476381

访问域名（基础URL）：
https://e-commerce-platform-1332476381.cos.ap-chengdu.myqcloud.com/

秘钥：secretId / secretKey （配置文件中未填写具体值，开发前需要填写，具体是什么发群里了）

### **4.JWT 令牌（登录时，会把admin_token/buyer_token/seller_token存入请求头中；请求时，需要从请求头中读出token进行鉴权）**

管理员令牌名：admin_token

买家令牌名：buyer_token

卖家令牌名：seller_token

过期时间：7200000ms = 2小时
