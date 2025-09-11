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

 Date: 09/09/2025 01:38:34
*/
USE vcampus_db;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_student
-- ----------------------------
DROP TABLE IF EXISTS `tb_student`;
CREATE TABLE `tb_student`  (
  `userId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID（主键）',
  `studentId` char(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号（8位数字）',
  `cardId` char(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '一卡通号（9位数字）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学院',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `grade` int NULL DEFAULT NULL COMMENT '年级（如 2023）',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `native_place` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '籍贯',
  `politics_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '政治面貌',
  `student_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学籍状态',
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生信息表（以userId为主键）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_student
-- ----------------------------
INSERT INTO `tb_student` VALUES ('1234567', '20210001', '123456789', '张三', '男', '计算机学院', '计算机科学与技术', 2021, '2003-09-01', '北京市', '共青团员', '在读');
INSERT INTO `tb_student` VALUES ('2345678', '20210002', '987654321', '李四', '女', '电子工程学院', '电子信息工程', 2021, '2003-10-15', '上海市', '共青团员', '在读');
INSERT INTO `tb_student` VALUES ('3456789', '20220001', '112233445', '王五', '男', '数学学院', '数学与应用数学', 2022, '2004-05-20', '广州市', '群众', '在读');
INSERT INTO `tb_student` VALUES ('4567890', '20230001', '556677889', '赵六', '女', '文学院', '汉语言文学', 2023, '2005-02-10', '成都市', '共青团员', '在读');

SET FOREIGN_KEY_CHECKS = 1;
