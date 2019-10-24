module hellofx {
    requires com.youthlin.i18n;
    requires com.almasb.fxgl.all;
    requires logback.classic;
    requires logback.core;
    requires slf4j.api;

    opens com.youthlin.example.javafx to javafx.fxml;
    exports com.youthlin.example.javafx;

}