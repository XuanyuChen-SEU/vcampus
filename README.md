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

## 数据库配置
数据库连接信息在 `vcampus-common/src/main/java/com/vcampus/common/util/DbHelper.java` 中配置，默认连接 `localhost:3306/vcampus` 数据库，可根据实际环境修改用户名和密码。