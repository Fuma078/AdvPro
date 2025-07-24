module se233.chapter2 {
   requires javafx.fxml;
   requires org.apache.commons.io;
   requires org.json;

   opens se233.chapter2 tojavafx.fxml;
   exports se233.chapter2;
}