package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane; // <-- layout container
import javafx.stage.Stage;

public class testFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create a layout and add the label to it
        StackPane root = new StackPane();
        root.getChildren().add(new Label("JavaFX"));

        // Create a scene with the layout
        Scene scene = new Scene(root, 400, 200);

        primaryStage.setTitle("My First JavaFX App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
