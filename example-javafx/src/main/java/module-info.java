module hellofx {
    //requires static lombok;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.youthlin.example.javafx to javafx.fxml;
    exports com.youthlin.example.javafx;

}