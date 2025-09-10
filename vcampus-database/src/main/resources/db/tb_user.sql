/*
 Navicat Premium Dump SQL

 Source Server         : Mylocal
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : vcampus_db

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 07/09/2025 18:11:35
*/
USE vcampus_db;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `userId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID（7位数字字符串）',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（7位数字字符串）',
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('1234567', '7654321');
INSERT INTO `tb_user` VALUES ('2345678', '8765432');
INSERT INTO `tb_user` VALUES ('3456789', '9876543');
INSERT INTO `tb_user` VALUES ('4567890', '0987654');
INSERT INTO `tb_user` VALUES ('5678901', '1098765');

SET FOREIGN_KEY_CHECKS = 1;
