# VCampus 虚拟校园管理系统

## 🚀 快速开始

### **前置提醒**
请务必记住MySQL安装时候设置的用户名和密码




### **1. 数据库初始化**

请先登录MySQL，执行命令：
```bash
SET GLOBAL local_infile = 1;
```


之后打开项目以下配置文件：
- `vcampus-database/src/main/resources/mybatis-config.xml`
- `vcampus-server/src/main/resources/mybatis-config.xml`

在这部分设置：
```xml
<property name="driver" value="com.mysql.cj.jdbc.Driver"/>
<property name="url" value="jdbc:mysql://localhost:3306/vcampus_db?useSSL=false&amp;serverTimezone=UTC"/>
<property name="username" value="root"/>
<property name="password" value="在这里填入自己的密码"/>
```

**重要**：这里的username和password需要和MySQL安装时候设置的用户名和密码保持一致,一般来说用户名是`root`.

### **2. Maven编译**

```bash
mvn clean install
```

### **3. 启动服务端**

```bash
cd vcampus-server
mvn exec:java
```

### **4. 启动客户端**

```bash
cd vcampus-client
mvn javafx:run
```

### **5. 测试登录**

- 用户名：`1234567`（学生）
- 密码：`7654321`

## 项目概述

VCampus 是一个基于 Java 开发的虚拟校园管理系统，采用模块化设计，包含客户端、服务端、公共模块和数据库脚本四个主要部分。系统实现了用户登录、全局通知、网络通信等核心功能，采用类似服务端的简洁架构设计。

## 技术栈

- **基础框架**：Java 17
- **构建工具**：Maven
- **数据库**：MySQL 8.0.33（搭配 HikariCP 连接池）
- **ORM 框架**：MyBatis 3.5.13
- **JSON 处理**：Gson 2.10.1
- **加密工具**：jBCrypt 0.4
- **客户端界面**：JavaFX 20

## 项目结构

```
vcampus/
├── pom.xml                             # 父POM文件，统一管理依赖版本和模块
│
├── vcampus-common/                     # 公共模块（客户端与服务端共享）
│   ├── pom.xml                         # 公共模块依赖配置
│   └── src/main/java/com/vcampus/common/
│       ├── dto/                        # 数据传输对象（DTO）
│       │   ├── Message.java            # 网络通信消息对象
│       │   ├── User.java               # 用户信息对象
│       │   ├── Course.java             # 课程信息对象
│       │   ├── Book.java               # 图书信息对象
│       │   ├── Product.java            # 商品信息对象
│       │   └── Order.java              # 订单信息对象
│       │
│       ├── dao/                        # 数据访问接口（DAO）
│       │   ├── UserDAO.java            # 用户数据访问接口
│       │   ├── CourseDAO.java          # 课程数据访问接口
│       │   ├── BookDAO.java            # 图书数据访问接口
│       │   └── OrderDAO.java           # 订单数据访问接口
│       │
│       ├── util/                       # 通用工具类
│       │   ├── DbHelper.java           # 数据库连接工具（基于HikariCP）
│       │   ├── JsonUtil.java           # JSON序列化/反序列化工具
│       │   ├── EncryptUtil.java        # 密码加密工具（基于jBCrypt）
│       │   ├── ValidatorUtil.java      # 数据校验工具
│       │   └── DateUtil.java           # 日期时间处理工具
│       │
│       └── enums/                      # 枚举类型定义
│           ├── Role.java               # 用户角色枚举
│           └── ActionType.java         # 动作类型枚举
│
├── vcampus-server/                     # 服务端模块
│   ├── pom.xml                         # 服务端依赖配置
│   └── src/main/java/com/vcampus/server/
│       ├── MainServer.java             # 服务端程序入口
│       │
│       ├── net/                        # 网络通信相关
│       │   ├── IMessageServerSrv.java  # 服务端消息处理接口
│       │   ├── ClientHandler.java      # 客户端连接处理器
│       │   └── ServerSocketManager.java # 服务端Socket管理
│       │
│       ├── dao/impl/                   # DAO接口实现类
│       │   ├── UserDAOImpl.java        # 用户数据访问实现
│       │   ├── CourseDAOImpl.java      # 课程数据访问实现
│       │   ├── BookDAOImpl.java        # 图书数据访问实现
│       │   └── OrderDAOImpl.java       # 订单数据访问实现
│       │
│       ├── service/                    # 业务逻辑服务层
│       │   ├── UserService.java        # 用户服务
│       │   ├── CourseService.java      # 课程服务
│       │   ├── BookService.java        # 图书服务
│       │   └── StoreService.java       # 商店服务
│       │
│       └── controller/                 # 控制器层（处理客户端请求）
│           ├── UserController.java     # 用户相关请求处理
│           ├── StudentStatusController.java # 学生状态请求处理
│           ├── BooksController.java    # 图书相关请求处理
│           └── OrderController.java    # 订单相关请求处理
│
├── vcampus-client/                     # 客户端模块
│   ├── pom.xml                         # 客户端依赖配置（含JavaFX）
│   ├── src/main/java/com/vcampus/client/
│   │   ├── MainApp.java                # JavaFX应用启动类
│   │   │
│   │   ├── net/                        # 客户端网络通信
│   │   │   ├── IMessageClientSrv.java  # 客户端消息处理接口
│   │   │   └── SocketClient.java       # 客户端Socket连接（异步接收）
│   │   │
│   │   ├── service/                    # 客户端业务逻辑
│   │   │   ├── UserClientSrv.java      # 用户相关客户端服务
│   │   │   ├── StudentStatusClient.java # 学生状态客户端服务
│   │   │   ├── LibraryClient.java      # 图书馆客户端服务
│   │   │   └── StoreClient.java        # 商店客户端服务
│   │   │
│   │   └── controller/                 # JavaFX界面控制器
│   │       ├── LoginController.java    # 登录界面控制器
│   │       ├── MainController.java     # 主界面控制器
│   │       ├── StudentStatusController.java # 学生状态界面控制器
│   │       ├── CourseController.java   # 课程界面控制器
│   │       ├── LibraryController.java  # 图书馆界面控制器
│   │       └── StoreController.java    # 商店界面控制器
│   │
│   └── src/main/resources/
│       ├── fxml/                       # FXML界面文件
│       │   ├── login.fxml              # 登录界面
│       │   ├── main.fxml               # 主界面
│       │   ├── student_status.fxml     # 学生状态界面
│       │   ├── course.fxml             # 课程界面
│       │   ├── library.fxml            # 图书馆界面
│       │   └── store.fxml              # 商店界面
│       └── css/                        # 样式表
│           └── style.css               # 通用样式
│
└── vcampus-database/                   # 数据库脚本模块
    ├── pom.xml                         # 数据库模块配置
    └── src/main/resources/db/
        ├── schema.sql                  # 数据库表结构脚本
        └── data.sql                    # 初始数据脚本
```

## 模块说明

1. **vcampus-common**：公共模块，包含客户端和服务端共享的DTO、DAO接口、工具类等，减少代码冗余
2. **vcampus-server**：服务端模块，负责处理客户端请求、业务逻辑处理和数据访问
3. **vcampus-client**：客户端模块，基于JavaFX实现图形用户界面，负责与用户交互和服务端通信
4. **vcampus-database**：数据库模块，包含数据库表结构和初始数据脚本

## 开发环境配置

- JDK 17
- Maven 3.6+
- MySQL 8.0.33
- IntelliJ IDEA（推荐）或其他Java IDE

## 本项目Maven简介

- 本项目采用Maven进行依赖管理和项目构建
- 父工程 `vcampus` 管理子模块 `vcampus-common`、`vcampus-server`、`vcampus-client`、`vcampus-database`
- 每个子模块有自己的 `pom.xml` 配置文件，声明依赖和插件

### 父工程（只挑重点，有的没复制）

这部分是基础配置，其他子模块都继承自这个父工程
重点看groupId、artifactId，注意packaging务必pom，原因请询问ai

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.vcampus</groupId>
    <artifactId>vcampus</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
```

modules表明了子模块

```
    <modules>
        <module>vcampus-common</module>
        <module>vcampus-server</module>
        <module>vcampus-client</module>
        <module>vcampus-database</module>
    </modules>
```

properties表明了一些全局变量，通过 `${变量名}`引用

```
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
```

dependencyManagement表明了依赖管理，子模块可以引用这里的依赖，这里只保留一个作为示例

```
    <dependencyManagement>
        <dependencies>
            <!-- JavaFX -->
            <dependency>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-controls</artifactId>
                <version>${javafx.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 子模块

这里以 `vcampus-common`为例
继承一定要和父工程的groupId、artifactId，version一致

```
    <parent>
        <groupId>com.vcampus</groupId>
        <artifactId>vcampus</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
```

这里artifactId自己起名，如果 `vcampus-server`需要用到这个 `common`模块就需要加入

```
<dependency>
            <groupId>com.vcampus</groupId>
            <artifactId>vcampus-common</artifactId>
            <version>1.0-SNAPSHOT</version>
</dependency>
```

```
    <artifactId>vcampus-common</artifactId>
    <dependencies>
        <!-- MyBatis -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
        </dependency>
    </dependencies>
</project>
```

本项目如果想增加maven依赖，需要在父工程的dependencyManagement中添加，子模块就可以引用了

## 测试

单元测试：使用 JUnit 5 编写测试用例，验证业务逻辑的正确性。
具体方法是在IDEA安装JUnit 5 Mockito Code Generator插件，按 `alt+insert`，对你要测试的Java文件点击测试生成即可。
JUnit测试的语法上网查询，当然ai生成就可以。
当然你是付费版不用这个插件，右键应该是自带的，点击Generate Test即可。
整体测试是在命令行输入 `mvn test`，不妨试试。

## 核心功能

### **网络通信架构**

- **同步发送**：客户端发送消息后立即返回结果
- **异步接收**：客户端持续监听服务器消息，支持全局通知
- **消息路由**：MessageController根据ActionType自动路由到对应处理器

### **登录功能**

- 用户名密码验证
- 登录状态管理
- 错误提示和成功跳转

### **全局通知系统**

- 支持服务器发送全局通知
- 支持系统广播
- 支持紧急通知
- 自动弹出对话框显示

### **架构特点**

- **简化设计**：去除了复杂的双线程、消息队列等设计
- **类似服务端**：客户端架构风格与服务端保持一致
- **易于扩展**：添加新功能只需在MessageController中添加case分支

## 测试

### **单元测试**

- 使用 JUnit 5 编写测试用例
- 在IDEA中右键Java文件 → Generate Test
- 运行测试：`mvn test`

### **功能测试**

- **登录测试**：验证用户名密码验证功能
- **全局信号测试**：运行 `TestGlobalSignal` 类测试全局通知接收
- **网络通信测试**：验证客户端与服务端的消息传输

## 已实现功能

### **✅ 已完成**

- **项目架构**：Maven多模块项目结构
- **网络通信**：基于Socket的客户端服务端通信
- **登录功能**：用户名密码验证
- **全局通知**：支持服务器发送全局信号
- **消息路由**：MessageController自动路由消息
- **UI界面**：JavaFX登录界面

### **🔄 当前状态**

- 客户端网络连接已简化，采用类似服务端的架构
- 支持异步接收全局信号
- 登录功能完整可用
- 编码问题已解决（UTF-8）

### **📋 待扩展功能**

- 学籍管理
- 课程管理
- 图书管理
- 商店功能
- 更多业务模块

## 常见问题

### **编码问题**

如果遇到中文乱码，请确保：

1. 控制台编码设置为UTF-8：`chcp 65001`
2. Maven配置中包含编码参数：`-Dfile.encoding=UTF-8`
3. 项目文件编码为UTF-8

### **端口占用**

如果9090端口被占用：

```bash
netstat -ano | findstr :9090
taskkill /PID <进程ID> /F
```

### **构建问题**

确保先编译再运行：

```bash
mvn compile
mvn exec:java
```

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request
