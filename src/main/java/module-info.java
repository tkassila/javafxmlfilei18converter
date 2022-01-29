module com.example.javafxmlfileconverti18 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires org.jfxtras.styles.jmetro;
    requires javafx.web;
//requires org.controlsfx.controls;
//requires impl.jfxtras.styles.jmetro;

    opens com.metait.javafxmlfileI18convert to javafx.fxml;
    exports com.metait.javafxmlfileI18convert;
}