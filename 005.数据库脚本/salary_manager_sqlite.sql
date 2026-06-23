-- ============================================
-- 财务管家 (SalaryManager) SQLite 数据库脚本
-- 版本: V1.1
-- 兼容: SQLite 3.35+
-- 用途: 移动端本地存储 / 嵌入式数据库
-- ============================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    username      TEXT    NOT NULL UNIQUE,
    password_hash TEXT    NOT NULL,
    nickname      TEXT,
    created_at    TEXT    NOT NULL DEFAULT (datetime('now'))
);

-- 分类表
CREATE TABLE IF NOT EXISTS biz_category (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER DEFAULT NULL              REFERENCES sys_user(id) ON DELETE CASCADE,
    name          TEXT    NOT NULL,
    icon          TEXT    NOT NULL DEFAULT '📦',
    color         TEXT    NOT NULL DEFAULT '#B2BEC3',
    is_preset     INTEGER NOT NULL DEFAULT 0,
    sort_order    INTEGER NOT NULL DEFAULT 0,
    created_at    TEXT    NOT NULL DEFAULT (datetime('now')),
    deleted_at    TEXT    DEFAULT NULL
);

-- 消费记录表
CREATE TABLE IF NOT EXISTS biz_expense (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER NOT NULL                 REFERENCES sys_user(id) ON DELETE CASCADE,
    category_id   INTEGER NOT NULL                 REFERENCES biz_category(id) ON DELETE RESTRICT,
    amount        REAL    NOT NULL CHECK (amount > 0),
    note          TEXT    DEFAULT '',
    expense_date  TEXT    NOT NULL,                  -- YYYY-MM-DD
    created_at    TEXT    NOT NULL DEFAULT (datetime('now')),
    updated_at    TEXT    NOT NULL DEFAULT (datetime('now')),
    deleted_at    TEXT    DEFAULT NULL
);

-- 预算表
CREATE TABLE IF NOT EXISTS biz_budget (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER NOT NULL                 REFERENCES sys_user(id) ON DELETE CASCADE,
    budget_month  TEXT    NOT NULL,                  -- YYYY-MM
    category_id   INTEGER DEFAULT NULL             REFERENCES biz_category(id) ON DELETE SET NULL,
    amount        REAL    NOT NULL CHECK (amount > 0),
    created_at    TEXT    NOT NULL DEFAULT (datetime('now')),
    updated_at    TEXT    NOT NULL DEFAULT (datetime('now')),
    UNIQUE(user_id, budget_month, category_id)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_expense_user_date     ON biz_expense(user_id, expense_date);
CREATE INDEX IF NOT EXISTS idx_expense_category      ON biz_expense(category_id);
CREATE INDEX IF NOT EXISTS idx_expense_user_cat_date ON biz_expense(user_id, category_id, expense_date);
CREATE INDEX IF NOT EXISTS idx_budget_user_month     ON biz_budget(user_id, budget_month);

-- 预设分类种子数据
INSERT OR IGNORE INTO biz_category (id, user_id, name, icon, color, is_preset, sort_order) VALUES
(1, NULL, '餐饮', '🍚', '#7EC850', 1, 1),
(2, NULL, '交通', '🚗', '#4ECDC4', 1, 2),
(3, NULL, '购物', '🛒', '#FFD93D', 1, 3),
(4, NULL, '居住', '🏠', '#6C5CE7', 1, 4),
(5, NULL, '娱乐', '🎬', '#A8E6CF', 1, 5),
(6, NULL, '医疗', '💊', '#FF8B94', 1, 6),
(7, NULL, '教育', '📚', '#74B9FF', 1, 7),
(8, NULL, '其他', '📦', '#B2BEC3', 1, 8);
