# VCampus 项目打包指南

## 项目概述

VCampus 是一个基于 JavaFX 的智慧校园管理系统，采用多模块 Maven 项目结构，包含客户端、服务端、数据库和公共模块。

### 项目结构
```
vcampus/
├── vcampus-common/          # 公共模块
├── vcampus-database/        # 数据库模块
├── vcampus-server/          # 服务端模块
├── vcampus-client/          # 客户端模块（JavaFX）
└── pom.xml                  # 父级 POM 文件
```

## 打包方式

### 1. 标准 Maven 打包

#### 1.1 完整项目打包
```bash
# 在项目根目录执行
mvn clean package
```

#### 1.2 单独模块打包
```bash
# 打包客户端
cd vcampus-client
mvn clean package

# 打包服务端
cd vcampus-server
mvn clean package

# 打包数据库模块
cd vcampus-database
mvn clean package

# 打包公共模块
cd vcampus-common
mvn clean package
```

### 2. JavaFX 应用程序打包

#### 2.1 使用 JavaFX Maven Plugin，这个镜像很重要，主要是`javafx:jlink`这个命令
```bash
cd vcampus-client
mvn clean compile package javafx:jlink
```

**生成的文件**:
- `target/vcampus-client/` - JLink 运行时镜像
- `target/vcampus-client-distribution.zip` - 分发包

#### 2.2 可执行 JAR 文件（没卵用）
```bash
cd vcampus-client
mvn clean package
```

**生成的文件**:
- `target/vcampus-client-1.0-SNAPSHOT.jar` - 包含所有依赖的可执行 JAR
- `target/lib/` - 所有依赖库

### 3. 独立可执行文件 (EXE)

#### 3.1 使用提供的批处理脚本（直接双击，用Jpackage实现的）
```bash
# 在项目根目录执行
create-standalone-exe.bat
```

#### 3.2 手动创建 EXE 文件（这就是脚本实际运行的东西）
```bash
cd vcampus-client

# 1. 创建 JLink 运行时镜像
mvn clean compile package javafx:jlink

# 2. 复制 JAR 文件到 lib 目录
copy target\vcampus-client-1.0-SNAPSHOT.jar target\lib\

# 3. 使用 JPackage 创建独立应用程序
jpackage ^
    --type app-image ^
    --name "VCampus-Client" ^
    --app-version "1.0.0" ^
    --vendor "VCampus Team" ^
    --description "VCampus 智慧校园客户端" ^
    --main-jar "vcampus-client-1.0-SNAPSHOT.jar" ^
    --main-class "com.vcampus.client.MainApp" ^
    --input "target\lib" ^
    --runtime-image "target\vcampus-client" ^
    --dest "target\standalone"
```

## 打包配置详解

### 1. 父级 POM 配置

**依赖管理**:
- 统一管理所有模块的依赖版本
- 配置 JavaFX 依赖和分类器
- 设置编译目标和源码版本

**插件管理**:
- `javafx-maven-plugin`: JavaFX 应用程序打包
- `maven-shade-plugin`: 创建可执行 JAR
- `maven-dependency-plugin`: 依赖管理
- `maven-compiler-plugin`: 编译配置

### 2. 客户端模块配置

**JavaFX Maven Plugin 配置**:
```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <configuration>
        <mainClass>com.vcampus.client.MainApp</mainClass>
        <jlinkImageName>vcampus-client</jlinkImageName>
        <jlinkZipName>vcampus-client-distribution</jlinkZipName>
        <launcher>vcampus-client</launcher>
        <noManPages>true</noManPages>
        <noHeaderFiles>true</noHeaderFiles>
    </configuration>
</plugin>
```

**Maven Shade Plugin 配置**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>com.vcampus.client.MainApp</mainClass>
                    </transformer>
                </transformers>
                <finalName>vcampus-client-${project.version}</finalName>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 3. 服务端模块配置

**Maven Shade Plugin 配置**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>com.vcampus.server.MainServer</mainClass>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 打包输出说明

### 1. 标准 Maven 打包输出

**客户端模块**:
- `target/vcampus-client-1.0-SNAPSHOT.jar` - 可执行 JAR
- `target/lib/` - 所有依赖库
- `target/classes/` - 编译后的类文件
- `target/original-vcampus-client-1.0-SNAPSHOT.jar` - 原始 JAR（不含依赖）

**服务端模块**:
- `target/vcampus-server-1.0-SNAPSHOT.jar` - 可执行 JAR
- `target/classes/` - 编译后的类文件

### 2. JavaFX JLink 输出

**JLink 运行时镜像**:
- `target/vcampus-client/` - 包含 Java 运行时和应用程序的完整镜像
- `target/vcampus-client/bin/vcampus-client` - 启动脚本
- `target/vcampus-client-distribution.zip` - 分发包

### 3. JPackage 输出

**独立应用程序**:
- `target/standalone/VCampus-Client/` - 完整的独立应用程序目录
- `target/standalone/VCampus-Client/VCampus-Client.exe` - Windows 可执行文件
- 包含完整的 Java 运行时环境和所有依赖

## 运行方式

### 1. 运行 JAR 文件
```bash
# 客户端（这个运行不了，需要jdk11-）
java -jar vcampus-client/target/vcampus-client-1.0-SNAPSHOT.jar

# 服务端
java -jar vcampus-server/target/vcampus-server-1.0-SNAPSHOT.jar
```

### 2. 运行 JLink 应用程序
```bash
# Windows
vcampus-client/target/vcampus-client/bin/vcampus-client

# Linux/Mac
vcampus-client/target/vcampus-client/bin/vcampus-client
```

### 3. 运行独立 EXE 文件
```bash
# 直接双击运行
target/standalone/VCampus-Client/VCampus-Client.exe
```

## 总结

VCampus 项目提供了多种打包方式，从简单的 JAR 文件到独立的 EXE 应用程序。选择合适的打包方式取决于部署需求和目标环境。建议在开发阶段使用标准 Maven 打包，在部署阶段使用 JPackage 创建独立的可执行文件。
