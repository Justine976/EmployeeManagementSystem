import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        SwingNode swingNode = new SwingNode();

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(false);
            mainFrame.getContentPane().remove(mainFrame.mainPanel);
            swingNode.setContent(mainFrame.mainPanel);
        });

        BorderPane root = new BorderPane(swingNode);
        Scene scene = new Scene(root, 1200, 800);

        stage.setTitle("Employee Management System (JavaFX)");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> Platform.exit());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
