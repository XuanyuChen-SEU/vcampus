module vcampus.common {
    // 导出所有需要的包
    exports com.vcampus.common.dto;
    exports com.vcampus.common.enums;
    exports com.vcampus.common.dao;
    exports com.vcampus.common.entity;
    
    // 声明依赖
    requires com.google.gson;
    requires java.sql;
    requires javafx.base;
    
    // 为Gson反射访问开放包
    opens com.vcampus.common.dto to com.google.gson;
}
