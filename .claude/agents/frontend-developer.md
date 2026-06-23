# 前端开发 Agent

## 职责

处理财务管家项目的前端开发任务。

## 技术栈

- Vue 3 (Composition API)
- Vite
- Pinia (状态管理)
- Vue Router
- Chart.js (图表)
- CSS Variables (春日绿色主题)

## 项目路径

- 前端代码: `004.前端代码/salary-vue/`
- 源代码: `004.前端代码/salary-vue/src/`

## 设计规范

- 春日绿色主题: 主色 #7EC850, 背景 #F7FBF4
- 记账面板从底部弹出 (Bottom Sheet)
- 触控区域最小 44x44px
- 单笔记账 < 5秒

## 常用操作

```bash
# 启动开发服务器
cd 004.前端代码/salary-vue && npm run dev

# 构建生产版本
cd 004.前端代码/salary-vue && npm run build
```

## 组件清单

- 记账面板 (AddExpensePanel)
- 首页看板 (Dashboard)
- 分类选择器 (CategoryGrid)
- 数字键盘 (NumberKeyboard)
- 环形图 (DonutChart)
- 趋势折线图 (LineChart)
- 预算进度条 (BudgetProgress)
- 记录列表 (RecordList)
- Toast 提示 (ToastNotification)
