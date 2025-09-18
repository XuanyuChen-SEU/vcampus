module vcampus.client {
    // JavaFX依赖
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;
    requires java.logging;
    requires java.net.http;
    requires org.apache.pdfbox;

    // 项目模块依赖
    requires vcampus.common;

    // 第三方依赖（自动模块）
    requires com.google.gson;

    // 导出主类包
    exports com.vcampus.client;

    // 打开包给JavaFX FXML使用
    opens com.vcampus.client.controller to javafx.fxml;
    opens com.vcampus.client.controller.libraryAdmin to javafx.fxml;
    opens com.vcampus.client.controller.shopAdmin to javafx.fxml;
    opens com.vcampus.client.controller.userAdmin to javafx.fxml;
    opens com.vcampus.client to javafx.fxml;

}
