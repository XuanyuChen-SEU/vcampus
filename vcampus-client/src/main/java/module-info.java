module vcampus.client {
    // JavaFX依赖
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;
    requires java.logging;
    requires java.net.http;

    requires org.apache.pdfbox;
    requires com.google.gson;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.ooxml.schemas;
    requires org.apache.poi.poi;
    requires org.apache.xmlbeans;

    requires javafx.graphics;
    requires vcampus.common;

    exports com.vcampus.client;
    exports com.vcampus.client.controller;

    // 打开包给JavaFX FXML使用
    opens com.vcampus.client.controller to javafx.fxml;
    opens com.vcampus.client.controller.libraryAdmin to javafx.fxml;
    opens com.vcampus.client.controller.shopAdmin to javafx.fxml;
    opens com.vcampus.client.controller.userAdmin to javafx.fxml;
    opens com.vcampus.client to javafx.fxml;

}