# VCampus 客户端登录界面

这是一个简洁美观的JavaFX登录界面，具有以下特点：

## 功能特性

- 🎨 **美观的界面设计**：采用渐变背景和现代化UI设计
- 📝 **完整的登录表单**：包含用户名、密码和角色选择
- ✅ **输入验证**：确保用户输入的有效性
- 🎯 **演示账号**：提供测试用的登录账号
- 🔄 **界面切换**：登录成功后自动跳转到主界面

## 演示账号

系统提供了以下演示账号用于测试：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 学生 | student | 123456 |
| 教师 | teacher | 123456 |

## 界面预览

登录界面包含以下元素：
- 系统标题
- 用户名输入框
- 密码输入框
- 角色选择下拉框
- 登录按钮
- 状态信息显示
- 演示账号提示

## 技术栈

- **JavaFX**：用户界面框架
- **FXML**：界面布局文件
- **CSS**：样式美化
- **Maven**：项目构建工具

## 运行方式

1. 确保已安装Java 8或更高版本
2. 确保已安装Maven
3. 在项目根目录执行：
   ```bash
   mvn clean compile
   mvn exec:java -pl vcampus-client -Dexec.mainClass="com.vcampus.client.MainApp"
   ```

## 文件结构

```
vcampus-client/
├── src/main/java/com/vcampus/client/
│   ├── MainApp.java              # 主应用程序类
│   └── controller/
│       └── LoginController.java  # 登录控制器
├── src/main/resources/
│   ├── fxml/
│   │   └── LoginView.fxml        # 登录界面布局
│   └── css/
│       └── styles.css            # 样式文件
```

## 自定义说明

- 修改 `LoginController.java` 中的 `validateLogin()` 方法来实现真实的登录验证
- 调整 `styles.css` 中的样式来改变界面外观
- 在 `LoginView.fxml` 中添加或修改界面元素

## 注意事项

- 当前版本为演示版本，登录验证为本地模拟
- 实际使用时需要连接真实的用户数据库或认证服务
- 可以根据需要添加更多功能，如注册、密码重置等
