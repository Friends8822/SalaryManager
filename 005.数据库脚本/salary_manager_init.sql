-- ============================================
-- 财务管家 (SalaryManager) 数据库初始化脚本
-- 版本: V1.1
-- 日期: 2026-06-23
-- 兼容: MySQL 8.0+ / MariaDB 10.5+
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS salary_manager
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE salary_manager;

-- ============================================
-- 1. 用户表 (sys_user)
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user (
    id            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    username      VARCHAR(32)     NOT NULL                 COMMENT '用户名 (登录账号)',
    password_hash VARCHAR(128)    NOT NULL                 COMMENT 'SHA-256 密码哈希',
    nickname      VARCHAR(32)     DEFAULT NULL             COMMENT '昵称',
    avatar_url    VARCHAR(256)    DEFAULT NULL             COMMENT '头像URL',
    status        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 1=正常, 0=禁用',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';


-- ============================================
-- 2. 分类表 (biz_category)
-- ============================================
CREATE TABLE IF NOT EXISTS biz_category (
    id            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    user_id       BIGINT          DEFAULT NULL             COMMENT '所属用户ID (NULL=系统预设, 非NULL=用户自定义)',
    name          VARCHAR(16)     NOT NULL                 COMMENT '分类名称 (≤6个字)',
    icon          VARCHAR(8)      NOT NULL DEFAULT '📦'    COMMENT 'Emoji图标',
    color         VARCHAR(7)      NOT NULL DEFAULT '#B2BEC3' COMMENT '颜色值 (#RRGGBB)',
    is_preset     TINYINT         NOT NULL DEFAULT 0       COMMENT '是否预设: 1=预设(不可删), 0=自定义',
    sort_order    INT             NOT NULL DEFAULT 0       COMMENT '排序权重 (越小越靠前)',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted_at    DATETIME        DEFAULT NULL             COMMENT '软删除时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_is_preset (is_preset),
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支出分类表';


-- ============================================
-- 3. 消费记录表 (biz_expense)
-- ============================================
CREATE TABLE IF NOT EXISTS biz_expense (
    id            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    user_id       BIGINT          NOT NULL                 COMMENT '所属用户ID',
    category_id   BIGINT          NOT NULL                 COMMENT '分类ID',
    amount        DECIMAL(10,2)   NOT NULL                 COMMENT '金额 (正数)',
    note          VARCHAR(200)    DEFAULT ''               COMMENT '备注',
    expense_date  DATE            NOT NULL                 COMMENT '消费日期 (YYYY-MM-DD)',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at    DATETIME        DEFAULT NULL             COMMENT '软删除时间',
    PRIMARY KEY (id),
    KEY idx_user_date (user_id, expense_date),
    KEY idx_category (category_id),
    KEY idx_user_category_date (user_id, category_id, expense_date),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES biz_category(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费记录表';


-- ============================================
-- 4. 预算表 (biz_budget)
-- ============================================
CREATE TABLE IF NOT EXISTS biz_budget (
    id            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    user_id       BIGINT          NOT NULL                 COMMENT '所属用户ID',
    budget_month  VARCHAR(7)      NOT NULL                 COMMENT '预算月份 (YYYY-MM)',
    category_id   BIGINT          DEFAULT NULL             COMMENT '分类ID (NULL=月度总预算)',
    amount        DECIMAL(10,2)   NOT NULL                 COMMENT '预算金额',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_month_category (user_id, budget_month, category_id),
    KEY idx_user_month (user_id, budget_month),
    CONSTRAINT fk_budget_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES biz_category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算表';


-- ============================================
-- 5. 种子数据：预设8个支出分类
-- ============================================
INSERT INTO biz_category (id, user_id, name, icon, color, is_preset, sort_order) VALUES
(1, NULL, '餐饮', '🍚', '#7EC850', 1, 1),
(2, NULL, '交通', '🚗', '#4ECDC4', 1, 2),
(3, NULL, '购物', '🛒', '#FFD93D', 1, 3),
(4, NULL, '居住', '🏠', '#6C5CE7', 1, 4),
(5, NULL, '娱乐', '🎬', '#A8E6CF', 1, 5),
(6, NULL, '医疗', '💊', '#FF8B94', 1, 6),
(7, NULL, '教育', '📚', '#74B9FF', 1, 7),
(8, NULL, '其他', '📦', '#B2BEC3', 1, 8);


-- ============================================
-- 6. 常用统计查询 (视图)
-- ============================================

-- 6.1 月度分类支出汇总
CREATE OR REPLACE VIEW v_monthly_category_summary AS
SELECT
    e.user_id,
    DATE_FORMAT(e.expense_date, '%Y-%m') AS expense_month,
    c.id   AS category_id,
    c.name AS category_name,
    c.icon AS category_icon,
    c.color AS category_color,
    COUNT(*)        AS record_count,
    SUM(e.amount)   AS total_amount,
    ROUND(SUM(e.amount) / (
        SELECT SUM(e2.amount)
        FROM biz_expense e2
        WHERE e2.user_id = e.user_id
          AND DATE_FORMAT(e2.expense_date, '%Y-%m') = DATE_FORMAT(e.expense_date, '%Y-%m')
          AND e2.deleted_at IS NULL
    ) * 100, 1) AS percentage
FROM biz_expense e
JOIN biz_category c ON c.id = e.category_id
WHERE e.deleted_at IS NULL
GROUP BY e.user_id, expense_month, c.id, c.name, c.icon, c.color
ORDER BY e.user_id, expense_month, total_amount DESC;


-- 6.2 用户月度概览
CREATE OR REPLACE VIEW v_monthly_overview AS
SELECT
    e.user_id,
    DATE_FORMAT(e.expense_date, '%Y-%m') AS expense_month,
    SUM(e.amount)                         AS total_expense,
    COUNT(*)                              AS record_count,
    ROUND(AVG(e.amount), 2)              AS avg_per_record,
    COUNT(DISTINCT e.expense_date)        AS active_days,
    ROUND(SUM(e.amount) / COUNT(DISTINCT e.expense_date), 2) AS daily_avg
FROM biz_expense e
WHERE e.deleted_at IS NULL
GROUP BY e.user_id, expense_month;


-- 6.3 预算执行情况
CREATE OR REPLACE VIEW v_budget_progress AS
SELECT
    b.user_id,
    b.budget_month,
    b.amount                                         AS budget_amount,
    COALESCE(SUM(e.amount), 0)                       AS spent_amount,
    b.amount - COALESCE(SUM(e.amount), 0)            AS remaining,
    ROUND(COALESCE(SUM(e.amount), 0) / b.amount * 100, 1) AS progress_pct,
    CASE
        WHEN COALESCE(SUM(e.amount), 0) >= b.amount THEN 'OVER'
        WHEN COALESCE(SUM(e.amount), 0) >= b.amount * 0.8 THEN 'WARN'
        ELSE 'NORMAL'
    END AS status
FROM biz_budget b
LEFT JOIN biz_expense e
    ON e.user_id = b.user_id
    AND DATE_FORMAT(e.expense_date, '%Y-%m') = b.budget_month
    AND e.deleted_at IS NULL
    AND (b.category_id IS NULL OR e.category_id = b.category_id)
WHERE b.category_id IS NULL  -- 仅统计总预算
GROUP BY b.user_id, b.budget_month, b.amount;


-- ============================================
-- 7. 测试数据 (开发调试用，生产环境请删除)
-- ============================================

-- 7.1 创建测试用户 (密码均为 '123456' 的SHA-256哈希)
-- INSERT INTO sys_user (username, password_hash, nickname) VALUES
-- ('demo', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '演示用户');

-- 7.2 测试消费记录 (用户ID=1, 2026年6月)
-- INSERT INTO biz_expense (user_id, category_id, amount, note, expense_date) VALUES
-- (1, 1, 35.00,  '午餐',     '2026-06-15'),
-- (1, 2, 6.00,   '地铁',     '2026-06-15'),
-- (1, 3, 128.50, '超市采购',  '2026-06-14'),
-- (1, 1, 18.00,  '咖啡',     '2026-06-14'),
-- (1, 4, 199.00, '话费',     '2026-06-13'),
-- (1, 1, 42.00,  '晚餐',     '2026-06-12'),
-- (1, 5, 80.00,  '电影',     '2026-06-12'),
-- (1, 2, 25.00,  '打车',     '2026-06-11'),
-- (1, 1, 28.00,  '午餐',     '2026-06-10'),
-- (1, 3, 256.00, '网购',     '2026-06-09'),
-- (1, 6, 68.00,  '买药',     '2026-06-08'),
-- (1, 1, 32.00,  '午餐',     '2026-06-07'),
-- (1, 7, 199.00, '线上课程',  '2026-06-06'),
-- (1, 2, 12.00,  '地铁',     '2026-06-05'),
-- (1, 1, 45.00,  '火锅',     '2026-06-05');

-- 7.3 测试预算
-- INSERT INTO biz_budget (user_id, budget_month, category_id, amount) VALUES
-- (1, '2026-06', NULL, 5000.00);


-- ============================================
-- 完成
-- ============================================
-- 表关系:
--   sys_user (1) ──→ (N) biz_expense
--   sys_user (1) ──→ (N) biz_budget
--   sys_user (1) ──→ (N) biz_category (自定义分类)
--   biz_category (1) ──→ (N) biz_expense
--
-- 统计查询建议:
--   高频查询(按月+用户)已建立联合索引 idx_user_date
--   分类筛选已建立复合索引 idx_user_category_date
--   预算唯一性由 uk_user_month_category 保证
-- ============================================
