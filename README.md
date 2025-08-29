# vcampus 项目说明

## 项目概述
vcampus 是一个基于 Java 开发的校园综合管理系统，采用模块化设计，包含客户端、服务端、公共模块和数据库脚本四个主要部分，实现校园内各类信息的管理与交互。

## 技术栈
- **基础框架**：Java 17
- **构建工具**：Maven
- **数据库**：MySQL 8.0.33（搭配 HikariCP 连接池）
- **ORM 框架**：MyBatis 3.5.13
- **日志框架**：SLF4J 2.0.9 + Logback 1.4.11
- **JSON 处理**：Gson 2.10.1
- **加密工具**：jBCrypt 0.4
- **客户端界面**：JavaFX 20

## 项目结构

```
vcampus/
│── pom.xml                             # 父POM文件，统一管理依赖版本和模块
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
│   ├── pom.xml                         # 服务端依赖配置（依赖vcampus-common）
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
│   │   │   └── SocketClient.java       # 客户端Socket连接
│   │   │
│   │   ├── service/                    # 客户端业务逻辑
│   │   │   ├── UserClientSrv.java      # 用户相关客户端服务
│   │   │   ├── StudentStatusClient.java # 学生状态客户端服务
│   │   │   ├── LibraryClient.java      # 图书馆客户端服务
│   │   │   └── StoreClient.java        # 商店客户端服务
│   │   │
│   │   └── ui/                         # JavaFX界面控制器
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
properties表明了一些全局变量，通过`${变量名}`引用
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
这里以`vcampus-common`为例
继承一定要和父工程的groupId、artifactId，version一致
```
    <parent>
        <groupId>com.vcampus</groupId>
        <artifactId>vcampus</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
```
这里artifactId自己起名，如果`vcampus-server`需要用到这个`common`模块就需要加入
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
具体方法是在IDEA安装JUnit 5 Mockito Code Generator插件，按`alt+insert`，对你要测试的Java文件点击测试生成即可。
JUnit测试的语法上网查询，当然ai生成就可以。
当然你是付费版不用这个插件，右键应该是自带的，点击Generate Test即可。
整体测试是在命令行输入`mvn test`，不妨试试。

## 开发流程
从0开始开发 `vcampus` 项目，按照**“基础层→核心层→交互层→应用层”**的顺序逐步推进，确保每个阶段的成果可验证、可复用。


### **项目初始化与基础环境搭建**
#### 1. 项目结构搭建
- ✅创建 Maven 多模块项目（父工程 `vcampus` + 4个子模块：`vcampus-common`、`vcampus-server`、`vcampus-client`、`vcampus-database`）
- ✅配置父 `pom.xml`，统一管理依赖版本（JDK 17、MySQL 驱动、MyBatis、JavaFX、Lombok、jBCrypt 等）
- ✅每个子模块配置独立 `pom.xml`，声明模块间依赖（如 `vcampus-server` 依赖 `vcampus-common`）

#### 2. 数据库设计与初始化
- 设计核心表结构（参考 `vcampus-database` 模块的 `schema.sql`）：
  - `user`（用户表：id、username、password、role_code 等）
  - `course`（课程表：id、name、teacher、credit 等）
  - `book`（图书表：id、name、author、stock 等）
  - `product`（商品表：id、name、price、stock 等）
  - `order`（订单表：id、user_id、product_id、time 等）
- 编写 `schema.sql`（建表语句）和 `data.sql`（初始测试数据，如管理员账号）
- 本地安装 MySQL，创建数据库 `vcampus`，执行初始化脚本验证表结构


### **公共模块开发（`vcampus-common`）**
#### 1. 枚举类（`enums`）
- ✅开发 `Role.java`（用户角色：STUDENT/TEACHER/ADMIN，包含 code 和 desc）
- 开发 `ActionType.java`（网络通信动作：LOGIN/QUERY_COURSE/BORROW_BOOK 等）

#### 2. 工具类（`util`）
- `DbHelper.java`：封装数据库连接池（HikariCP），提供获取连接的方法
- `EncryptUtil.java`：基于 jBCrypt 实现密码加密（`hashPassword`）和验证（`checkPassword`）
- `JsonUtil.java`：基于 Gson 实现对象与 JSON 字符串的转换（用于网络传输）
- `ValidatorUtil.java`：封装通用校验（如用户ID长度、密码格式等）

#### 3. 数据传输对象（`dto`）
- 开发核心 DTO（实现 `Serializable` 接口，支持网络传输）：
  - `Message.java`（通信消息载体：action、data、status、message）
  - ✅`User.java`（用户信息：userId、password、role 等，关联 `Role` 枚举）
  - `Course.java`、`Book.java`、`Product.java`、`Order.java`（按业务字段定义）

#### 4. DAO 接口（`dao`）
- 定义各实体的 DAO 接口（仅声明方法，不实现）：
  - `UserDAO.java`：`findByUsername`、`insert`、`update` 等
  - `CourseDAO.java`、`BookDAO.java`、`OrderDAO.java`（按 CRUD 需求定义方法）


### **服务端开发（`vcampus-server`）**
#### 1. DAO 实现类（`dao/impl`）
- 基于 MyBatis 实现 `UserDAOImpl`、`CourseDAOImpl` 等：
  - 编写 MyBatis 映射文件（`UserMapper.xml` 等），实现 SQL 与方法的映射
  - 测试数据访问方法（如通过 `UserDAOImpl.findByUsername` 查询用户）

#### 2. 服务层（`service`）
- 开发业务逻辑服务：
  - `UserService.java`：封装登录校验（密码加密验证）、用户信息修改等
  - `CourseService.java`：选课、退课、查询课程等逻辑
  - `BookService.java`：图书查询、借阅、续借等逻辑
  - `StoreService.java`：商品浏览、下单、订单查询等逻辑
- 服务层依赖 DAO 层，通过接口调用数据访问方法

#### 3. 控制器层（`controller`）
- 开发控制器，处理客户端请求（依赖服务层）：
  - `UserController.java`：处理登录请求（调用 `UserService` 验证）
  - `CourseController.java`：处理选课请求（调用 `CourseService`）
  - 每个控制器方法接收 `Message` 对象，处理后返回结果 `Message`

#### 4. 网络通信模块（`net`）
- `ServerSocketManager.java`：启动服务端 Socket，监听指定端口（如 8888）
- `ClientHandler.java`：多线程处理客户端连接（每个客户端一个线程）：
  - 接收客户端发送的 `Message` 对象（通过 `ObjectInputStream`）
  - 根据 `Message.action` 分发到对应控制器（如 `ActionType.LOGIN` → `UserController`）
  - 将控制器返回的结果 `Message` 发送给客户端（通过 `ObjectOutputStream`）
- `MainServer.java`：服务端入口，初始化并启动服务


### **客户端开发（`vcampus-client`）**
#### 1. 网络通信模块（`net`）
- `SocketClient.java`：与服务端建立 Socket 连接，提供发送/接收 `Message` 的方法
  - 封装 `sendMessage`（发送请求）和 `receiveMessage`（接收响应）
  - 处理断线重连逻辑

#### 2. 客户端服务层（`service`）
- 开发客户端业务服务（调用 `SocketClient` 与服务端通信）：
  - `UserClientSrv.java`：登录（组装 `LOGIN` 类型的 `Message` 发送给服务端）
  - `LibraryClient.java`：查询图书、借阅图书等（封装对应 `Message`）
  - 接收服务端响应，解析结果并返回给 UI 层

#### 3. UI 界面与控制器（`ui` + `fxml`）
- 基于 JavaFX 开发界面：
  - 编写 FXML 文件（`login.fxml`、`main.fxml` 等），设计界面布局
  - 开发控制器类（`LoginController.java` 等）：
    - 绑定 FXML 控件（输入框、按钮等）
    - 按钮点击事件调用客户端服务层（如登录按钮 → `UserClientSrv.login`）
    - 接收服务端响应，更新界面（如登录成功跳转主界面）
- 实现主题切换（可选，通过 CSS 控制 `style.css`）

#### 4. 客户端入口（`MainApp.java`）
- 初始化 JavaFX 应用，加载登录界面
- 管理全局状态（如当前登录用户信息）


### **集成测试与优化**
#### 1. 模块联调
- 启动服务端 → 启动客户端 → 测试核心流程：
  - 登录功能（用户名密码验证、角色解析）
  - 选课/图书借阅/商店下单等核心业务
  - 异常场景测试（如密码错误、网络中断）

#### 2. 问题修复与优化
- 修复通信超时、数据解析错误等问题
- 优化 UI 交互（如加载动画、错误提示）
- 完善日志（通过 `@Slf4j` 打印关键流程日志，便于排查问题）

#### 3. 文档完善
- 补充代码注释（尤其是核心方法、异常处理）
- 完善 `README.md`，包含部署步骤、核心功能说明