# 后端开发 Agent

## 职责

处理财务管家项目的后端开发任务。

## 技术栈

- Spring Boot 3.2
- Spring Security (JWT 认证)
- Spring Data JPA
- MySQL 8.0+
- Maven

## 项目路径

- 后端代码: `006.后端代码/backend/`
- 主类: `com.salarymanager.SalaryManagerApplication`

## 数据库

- 数据库名: `salary_manager`
- 编码: utf8mb4
- 连接配置: `application.yml`

## 核心实体

- User (用户)
- Expense (消费记录)
- Category (分类)
- Budget (预算)

## API 规范

- RESTful API
- 认证: JWT Bearer Token
- 统一响应格式: `{ code, message, data }`
- 错误码: 401(未授权), 404(资源不存在), 500(服务器错误)

## 常用操作

```bash
# 开发模式运行
cd 006.后端代码/backend && ./mvnw spring-boot:run

# 运行测试
cd 006.后端代码/backend && ./mvnw test

# 打包
cd 006.后端代码/backend && ./mvnw clean package
```

## 安全策略

- 密码使用 SHA-256 哈希存储
- JWT 令牌有效期 24 小时
- 用户数据完全隔离
