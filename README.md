# <center> VCampus 虚拟校园管理系统

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0.33-green)
![JavaFX](https://img.shields.io/badge/JavaFX-20-purple)
![License](https://img.shields.io/badge/License-MIT-yellow)

**一个功能完整的虚拟校园管理系统，基于Java 17 + JavaFX + MySQL技术栈**

[快速启动](#-快速启动) • [功能特性](#-功能特性) • [技术架构](#-技术架构) • [数据库设计](#-数据库设计) • [开发指南](#-开发指南) • [常见问题](#-常见问题)

</div>

## 📖 项目简介

VCampus是一个基于Java开发的现代化虚拟校园管理系统，采用客户端-服务端架构，为校园提供全方位的数字化管理解决方案。系统支持学生、教师、管理员三种角色，涵盖学籍管理、教务系统、图书馆、校园商店、邮件系统、AI助手等多个业务模块，提供完整的校园数字化服务体验。

本项目为东南大学23级计算机科学与技术专业的大三暑期学校的专业实训内容。

### ✨ 核心特性

- 🎯 **多角色权限管理** - 学生、教师、管理员角色分离
- 🔄 **实时网络通信** - 基于Socket的客户端服务端通信
- 📚 **完整业务模块** - 学籍、课程、图书馆、商店、邮件系统
- 🎨 **现代化UI** - 基于JavaFX的响应式界面设计
- 🛡️ **安全可靠** - 密码加密、数据校验、异常处理
- 📦 **模块化架构** - Maven多模块项目，便于维护和扩展

## 📋 环境要求

- **JDK 17+** - Java开发环境
- **Maven 3.6+** - 项目构建工具
- **MySQL 8.0+** - 数据库服务
- **JavaFX SDK 21.0.8** - 已包含在项目中
- **IntelliJ IDEA** - 推荐IDE（支持JavaFX）

## ⚙️ 环境配置

### 1. 数据库配置

**配置MySQL本地文件导入权限：**

**Windows系统**：编辑 `C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`
**Mac/Linux系统**：编辑 `/etc/mysql/my.cnf` 或 `/usr/local/etc/my.cnf`

在 `[mysqld]` 区块添加：

```ini
[mysqld]
local_infile = 1
```

**重启MySQL服务：**

```bash
# Windows
net stop mysql80
net start mysql80

# 或者使用服务管理器
services.msc
# 找到MySQL80服务，右键重启

# Linux/Mac
sudo systemctl restart mysql
# 或者
sudo service mysql restart
```

验证配置：

```sql
SHOW GLOBAL VARIABLES LIKE 'local_infile';
-- 输出应为：local_infile | ON
```

**配置数据库连接：**

编辑 `vcampus-database/src/main/resources/mybatis-config.xml`：

```xml
<property name="driver" value="com.mysql.cj.jdbc.Driver"/>
<property name="url" value="jdbc:mysql://localhost:3306/?serverTimezone=UTC&allowLoadLocalInfile=true"/>
<property name="username" value="root"/>
<property name="password" value="你的MySQL密码"/>
```

如果服务器启动出现bug，可以试试自行在MySQL创建名为`vcampus_db`的库。

### 2. 网络配置

**默认网络配置：**

**服务端配置：**
- **监听端口**: 9090
- **最大连接数**: 50
- **连接超时**: 5秒
- **读取超时**: 10秒

**客户端配置：**
- **默认主机**: localhost
- **默认端口**: 9090
- **连接超时**: 5秒
- **读取超时**: 10秒

**修改网络配置（如需要）：**

**修改服务端端口：**
在 `vcampus-server/src/main/java/com/vcampus/server/net/SocketServer.java` 中：
```java
public class SocketServer {
    private static final int PORT = 9090; // 修改为其他端口，如8080
    // ...
}
```

**修改客户端连接配置：**
在 `vcampus-client/src/main/java/com/vcampus/client/net/SocketClient.java` 中：
```java
public class SocketClient {
    private static final String DEFAULT_HOST = "localhost"; // 修改为服务器IP
    private static final int DEFAULT_PORT = 9090; // 修改为对应端口
    // ...
}
```

### 3. 项目构建

```bash
# 克隆项目
git clone <repository-url>
cd vcampus

# 编译项目
mvn clean install
```

## 🚀 快速启动

### ⚡ JAR包启动（推荐）

**前提条件：**
- ✅ MySQL已配置并启动
- ✅ 数据库连接配置已完成
- ✅ 项目已编译打包

**完整启动流程：**

**步骤1：复制JAR包到项目根目录**
```bash
# 复制客户端JAR包到项目根目录（与start-vcampus.bat和javafx-sdk在同一目录）
copy vcampus-client\target\vcampus-client-1.0-SNAPSHOT.jar vcampus-client-1.0-SNAPSHOT.jar

# 复制服务端JAR包到项目根目录
copy vcampus-server\target\vcampus-server-1.0-SNAPSHOT.jar vcampus-server-1.0-SNAPSHOT.jar
```

**📁 文件结构说明：**
确保以下文件在同一目录下：
- `vcampus-client-1.0-SNAPSHOT.jar` - 客户端JAR包
- `vcampus-server-1.0-SNAPSHOT.jar` - 服务端JAR包
- `start-vcampus.bat` - 启动脚本
- `javafx-sdk-21.0.8/` - JavaFX SDK目录

**步骤2：启动服务端**
```bash
# 使用JAR包启动服务端
java -jar vcampus-server-1.0-SNAPSHOT.jar
```

**步骤3：启动客户端**
```bash
# 方式1：使用批处理脚本（推荐）
start-vcampus.bat

# 方式2：直接运行JAR包
java --module-path "javafx-sdk-21.0.8/lib" --add-modules javafx.controls,javafx.fxml,javafx.web -jar vcampus-client-1.0-SNAPSHOT.jar
```

**脚本作用说明：**
- 自动检测JAR文件和JavaFX SDK路径
- 配置JavaFX模块路径和依赖模块
- 解决JDK高版本JavaFX兼容性问题
- 提供友好的错误提示和暂停功能
- **注意：仅启动客户端，服务端需要单独启动**

### ⚠️ JavaFX模块化说明

**重要提示：JDK 11+版本变化**
- **JDK 8**: JavaFX包含在JDK中，可直接使用
- **JDK 11+**: JavaFX从JDK中移除，需要单独安装和配置
- **解决方案**: 项目已包含JavaFX SDK 21.0.8，通过模块路径方式加载

**为什么需要模块化配置：**
```bash
# JDK 11+需要显式指定JavaFX模块
java --module-path "javafx-sdk-21.0.8/lib" --add-modules javafx.controls,javafx.fxml,javafx.web -jar vcampus-client-1.0-SNAPSHOT.jar
```

**start-vcampus.bat的作用：**
- 自动配置JavaFX模块路径
- 添加必要的JavaFX模块依赖
- 简化启动流程，避免手动配置
- 提供错误检查和友好提示

### ⚙️ 开发模式启动

**适用于开发调试，需要Maven环境**

**步骤1：启动服务端**
```bash
cd vcampus-server
mvn exec:java
```

等待服务端启动完成，看到类似以下输出：
```
服务端启动成功，监听端口: 9090
等待客户端连接...
```

**步骤2：启动客户端**
```bash
# 
cd vcampus-client
mvn javafx:run
```

**启动顺序说明：**
1. 先启动服务端（vcampus-server）
2. 等待服务端完全启动
3. 再启动客户端（vcampus-client）
4. 客户端会自动连接到服务端

### 🧪 测试登录

| 角色   | 用户名      | 密码        | 说明         |
| ------ | ----------- | ----------- | ------------ |
| 学生   | `1234567` | `7654321` | 学生端功能   |
| 教师   | `2345678` | `8765432` | 教师端功能   |
| 管理员 | `3456789` | `9876543` | 用户管理员端 |
| 管理员 | `4567890` | `0987654` | 学籍管理员端 |
| 管理员 | `5678901` | `1098765` | 教务管理员端 |
| 管理员 | `6789012` | `2109876` | 商店管理员端 |
| 管理员 | `7890123` | `3210987` | 图书管理员端 |
| 管理员 | `8901234` | `4321098` | 邮件管理员端 |

## 🎯 功能特性

### 🏠 主页系统
- **欢迎界面** - 系统介绍和功能概览
- **功能卡片** - 6大核心模块可视化展示
- **角色识别** - 根据用户ID自动识别角色权限

### 👥 用户管理
- **多角色登录** - 学生、教师、管理员角色自动识别
- **密码管理** - 忘记密码申请、密码重置、修改密码
- **用户管理** - 创建用户、删除用户、搜索用户（管理员功能）
- **权限控制** - 不同角色访问不同功能模块

### 🛒 校园商店
- **商品浏览** - 查看商品、搜索商品、商品详情
- **订单管理** - 创建订单、支付、查看订单历史
- **收藏功能** - 收藏商品、管理收藏夹
- **余额管理** - 查看余额、充值功能
- **管理员功能** - 商品管理、订单管理、收藏管理

### 📚 图书馆系统
- **图书借阅** - 借书、还书、续借功能
- **借阅记录** - 查看借阅历史和状态
- **在线阅读** - PDF文件在线阅读服务
- **学术搜索** - 图书搜索和学术资源查找
- **AI助手** - 智能问答和学习辅助
- **管理员功能** - 图书管理、借阅记录管理

### 📋 学籍管理
- **学生信息** - 查看和修改个人信息
- **状态申请** - 学籍状态调整申请
- **申请管理** - 管理员审核学籍申请
- **成绩查询** - 个人成绩查看服务

### 🎓 教务系统
- **选课系统** - 浏览课程、选课、退课
- **课表管理** - 个人课表查看和管理
- **课程卡片** - 课程信息可视化展示
- **教学班管理** - 教学班选择和查看
- **管理员功能** - 课程管理、教学班管理、选课管理

### 📧 邮件系统
- **邮件收发** - 发送邮件、接收邮件
- **草稿管理** - 保存草稿、编辑草稿
- **邮件管理** - 删除、标记已读/未读、批量操作
- **搜索功能** - 按关键词搜索邮件
- **管理员功能** - 全站邮件管理和统计

### 🤖 AI助手
- **智能问答** - 基于AI的学习辅助功能
- **聊天界面** - 友好的对话式交互
- **学习支持** - 提供学习建议和帮助

## 🏗️ 技术架构

### 📦 模块结构

```
vcampus/
├── vcampus-common/          # 公共模块
│   ├── dto/                 # 数据传输对象 (Message, User, Course, Book等)
│   ├── dao/                 # 数据访问接口 (IUserDao, ICourseDao等)
│   ├── enums/               # 枚举类型 (ActionType, Role等)
│   └── util/                # 工具类 (加密、JSON处理等)
├── vcampus-database/         # 数据库模块
│   ├── mapper/              # MyBatis映射器 (UserMapper, CourseMapper等)
│   ├── service/             # 数据库服务 (DBService, SqlFileExecutor)
│   └── resources/
│       ├── db/              # CSV数据文件
│       ├── db_img/          # 图片资源
│       └── db_pdf/          # PDF文件
├── vcampus-server/          # 服务端模块
│   ├── controller/          # 控制器层 (UserController, CourseController等)
│   ├── service/             # 业务服务层 (UserService, CourseService等)
│   ├── dao/impl/            # 数据访问实现 (UserDao, CourseDao等)
│   └── net/                 # 网络通信 (SocketServer)
└── vcampus-client/          # 客户端模块
    ├── controller/          # 界面控制器 (LoginController, MainController等)
    ├── service/             # 客户端服务 (LoginService, CourseService等)
    ├── net/                 # 网络通信 (SocketClient)
    ├── session/             # 会话管理 (UserSession)
    └── resources/
        ├── fxml/            # FXML界面文件
        │   ├── LoginView.fxml
        │   ├── MainView.fxml (学生端)
        │   ├── MainTeacherView.fxml (教师端)
        │   ├── AdminView.fxml (管理员端)
        │   ├── WelcomeView.fxml (主页)
        │   ├── academic/     # 教务系统界面
        │   ├── admin/        # 管理员界面
        │   ├── email/        # 邮件系统界面
        │   ├── library/      # 图书馆界面
        │   ├── store/        # 商店界面
        │   └── studentrecord/ # 学籍管理界面
        ├── css/              # 样式文件
        └── images/           # 图片资源
```

### 🔄 网络通信架构

```mermaid
graph TB
    A[客户端UI] --> B[Controller]
    B --> C[Service]
    C --> D[SocketClient]
    D --> E[Socket连接]
    E --> F[SocketServer]
    F --> G[MessageController]
    G --> H[业务Controller]
    H --> I[业务Service]
    I --> J[DAO实现]
    J --> K[MySQL数据库]
  
    K --> J
    J --> I
    I --> H
    H --> G
    G --> F
    F --> E
    E --> D
    D --> C
    C --> B
    B --> A
```

### 🛠️ 技术栈

#### 后端技术

- **Java 17** - 核心开发语言
- **Maven** - 项目构建和依赖管理
- **MyBatis 3.5.13** - ORM框架
- **MySQL 8.0.33** - 关系型数据库
- **jBCrypt 0.4** - 密码加密

#### 前端技术

- **JavaFX 20** - 桌面应用框架
- **FXML** - 界面布局定义
- **CSS** - 样式设计

#### 网络通信

- **Socket** - 网络通信
- **ObjectInputStream/ObjectOutputStream** - 对象序列化传输
- **多线程** - 连接池和工作池

## 📊 数据库设计

### 核心表结构

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `tb_user` | 用户基础信息 | userId, password, balance |
| `tb_student` | 学生详细信息 | userId, name, major, class |
| `tb_student_leave_application` | 学生请假申请 | applicationId, userId, reason, status |
| `tb_password_reset_application` | 密码重置申请 | applicationId, userId, newPassword, status |
| `tb_courses` | 课程信息 | courseId, courseName, credits |
| `tb_class_sessions` | 教学班信息 | sessionId, courseId, teacherId |
| `tb_course_selections` | 选课记录 | userId, sessionId, selectTime |
| `tb_book` | 图书信息 | bookId, title, author, isbn |
| `tb_borrow_log` | 借阅记录 | userId, bookId, borrowTime, returnTime |
| `tb_product` | 商品信息 | productId, name, price, stock |
| `tb_order` | 订单信息 | orderId, userId, productId, quantity |
| `tb_favorite` | 收藏记录 | userId, productId, addTime |
| `tb_balance` | 余额记录 | userId, balance, updateTime |
| `tb_email` | 邮件信息 | emailId, senderId, receiverId, content |

## 🔧 开发指南

### 📝 开发规范

1. **分层架构** - Controller → Service → DAO → Database
2. **接口设计** - DAO层使用接口，便于测试和替换
3. **异常处理** - 每层都有完善的异常处理机制
4. **资源管理** - 使用try-with-resources确保资源释放
5. **编码规范** - UTF-8编码，统一的命名规范

### 🚀 添加新功能

1. **定义ActionType** - 在 `ActionType`枚举中添加新的操作类型
2. **创建DTO** - 在 `vcampus-common/dto`中定义数据传输对象
3. **实现服务端** - 创建Controller、Service、DAO实现
4. **实现客户端** - 创建Controller、Service、FXML界面
5. **注册路由** - 在MessageController中添加消息路由

### 🧪 测试

**单元测试：**

```bash
mvn test
```

**功能测试：**

- 登录功能测试
- 网络通信测试
- 数据库操作测试



## ❓ 常见问题

### 🔧 环境问题

**Q: 中文乱码怎么办？**
A: 确保控制台编码为UTF-8：`chcp 65001`

**Q: 9090端口被占用？**
A: 查找并终止占用进程：

```bash
netstat -ano | findstr :9090
taskkill /PID <进程ID> /F
```

**Q: 数据库连接失败？**
A: 检查MySQL服务是否启动，用户名密码是否正确

### 🐛 开发问题

**Q: 如何调试网络通信？**
A: 查看控制台输出的消息日志，检查Message对象内容

**Q: 如何添加新的业务模块？**
A: 参考现有模块的实现，按照分层架构添加代码


### 🌐 网络问题

**Q: 客户端连接服务端失败？**
A: 检查服务端是否启动，端口9090是否被占用，防火墙是否阻止连接

**Q: 启动顺序有什么要求？**
A: 必须先启动服务端（vcampus-server），等待服务端完全启动后再启动客户端（vcampus-client）

**Q: 如何确认服务端已经启动？**
A: 服务端启动成功后会显示"服务端启动成功，监听端口: 9090"和"等待客户端连接..."等信息

**Q: 如何修改服务端端口？**
A: 修改`SocketServer.java`中的`PORT`常量，同时修改客户端的`DEFAULT_PORT`

**Q: 网络连接超时怎么办？**
A: 可以增加`CONNECTION_TIMEOUT`和`READ_TIMEOUT`的值，或检查网络延迟

**Q: 如何部署到远程服务器？**
A: 修改客户端的`DEFAULT_HOST`为服务器IP地址，确保防火墙开放对应端口

<div align="center">
Made with ❤️ by VCampus Team
</div>
