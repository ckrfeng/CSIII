package PowerGrid;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class PlayerSetupPanel {
    public interface Listener {
        void onContinue(List<String> names);
    }

    private PlayerSetupPanel() {
    }

    public static VBox create(int playerCount, Listener listener) {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #161616;");

        List<TextField> nameFields = new ArrayList<>();
        List<Button> readyButtons = new ArrayList<>();
        Button continueButton = new Button("Continue");
        continueButton.setDisable(true);

        for (int i = 0; i < playerCount; i++) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER);
            TextField tf = new TextField("Player " + (i + 1));
            tf.setPrefWidth(220);

            Button ready = new Button("Ready");
            ready.setOnAction(e -> {
                boolean toReady = "Ready".equals(ready.getText());
                ready.setText(toReady ? "Unready" : "Ready");
                tf.setDisable(toReady);
                boolean allReady = readyButtons.stream().allMatch(b -> "Unready".equals(b.getText()));
                continueButton.setDisable(!allReady);
            });

            readyButtons.add(ready);
            nameFields.add(tf);
            row.getChildren().addAll(tf, ready);
            content.getChildren().add(row);
        }

        continueButton.setOnAction(e -> {
            List<String> names = new ArrayList<>();
            for (TextField tf : nameFields) {
                names.add(tf.getText().isBlank() ? "Player" : tf.getText().trim());
            }
            listener.onContinue(names);
        });
        content.getChildren().add(continueButton);
        return content;
    }
}
