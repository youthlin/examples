module hellofx {
    requires com.youthlin.i18n;
    requires com.almasb.fxgl.all;

    opens com.youthlin.example.javafx to javafx.fxml;
    exports com.youthlin.example.javafx;

}