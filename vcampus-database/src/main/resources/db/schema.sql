-- 创建数据库
CREATE DATABASE IF NOT EXISTS vcampus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vcampus;

-- 如果已经存在 user 表，则删除
DROP TABLE IF EXISTS user;

-- 用户表
CREATE TABLE user (
    id CHAR(7) PRIMARY KEY,                       -- 用户ID，定长7位（如学号、工号）
    pwd VARCHAR(255) NOT NULL,                    -- 哈希后的密码（使用 jBCrypt 存储）
    role TINYINT NOT NULL DEFAULT 1,              -- 用户角色（默认学生=1）
    CONSTRAINT uq_user_id UNIQUE (id),            -- 唯一约束
    CONSTRAINT chk_role CHECK (role IN (1,2,3,4,5,6))  -- 角色必须在1~6范围内
);
