module org.task.task {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.task.task to javafx.fxml;
    exports org.task.task;
}