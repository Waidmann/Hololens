import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartApplication extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("QRGen.fxml"));

        Scene scene = new Scene(root, 600, 360);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png")));
        stage.setTitle("QR Creator GUI");
        stage.setScene(scene);
        stage.show();
    }
}
