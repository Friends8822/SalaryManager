# 财务管家 (SalaryManager) — 项目指南

## 项目背景

财务管家是一款个人记账工具，核心理念是「3秒记一笔，一张图看懂你的钱去哪了」。

## 技术架构

```
前端: Vue 3 + Vite + Pinia + Chart.js
后端: Spring Boot 3.2 + Spring Security + JPA
数据库: MySQL
认证: JWT
```

## 快速启动

### 前端
```bash
cd 004.前端代码/salary-vue
npm install
npm run dev
```

### 后端
```bash
cd 006.后端代码/backend
./mvnw spring-boot:run
```

需要 MySQL 8.0+，创建 `salary_manager` 数据库。

## 项目特点

- 春日绿色主题（护眼、消除焦虑）
- 极简交互（< 5秒记账）
- 8个预设分类
- 多用户数据隔离
- 离线优先

## 约束

- 无后端时纯本地存储可用（V1.0）
- V1.1 需要 MySQL 数据库
