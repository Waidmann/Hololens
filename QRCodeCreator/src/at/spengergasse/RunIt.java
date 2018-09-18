package at.spengergasse;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RunIt extends Application{

    private Scene scene;
    private Stage stage;
    private SplitPane splitPane;
    private FlowPane leftPropertiesPane;
    private FlowPane rightQRPane;

    private TextArea titleField;
    private TextArea descField;
    private TextArea roomField;
    private TextArea contentField;
    private TextArea addPropField;
    private Button resetButton;

    private ImageView qrViewer;
    private Button printButton;


    public static void main(String[] args){
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        this.stage = primaryStage;
        stage.setTitle("QR Code Generator");

        splitPane = new SplitPane();
        leftPropertiesPane = new FlowPane();
        rightQRPane = new FlowPane();
        splitPane.getItems().add(leftPropertiesPane);
        splitPane.getItems().add(rightQRPane);

        initTextAreas();
        resetButton = new Button();
        resetButton.setText("Reset");
        resetButton.setOnAction(event -> {
            leftPropertiesPane.getChildren().forEach(e->{
                if(e instanceof TextArea)
                    ((TextArea) e).setText("");
            });
        });

        leftPropertiesPane.getChildren().addAll(titleField,descField,roomField,contentField,addPropField, resetButton);
        leftPropertiesPane.setPadding(new Insets(10));
        leftPropertiesPane.setVgap(10);
        leftPropertiesPane.setHgap(100);
        leftPropertiesPane.getChildren().forEach(e->{
            if(e instanceof TextArea)
                ((TextArea)e).textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        updateQrCode();
                    } catch (NoSuchAlgorithmException e1) {
                        e1.printStackTrace();
                    }
                });
        });

        qrViewer = new ImageView();
        printButton = new Button();

        scene = new Scene(splitPane, 800, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        addPropField.setMinHeight(240);

        stage.setScene(scene);

        stage.show();
    }

    private void initTextAreas(){
        titleField = new TextArea();
        titleField.setPromptText("Title...");
        descField = new TextArea();
        descField.setPromptText("Description...");
        roomField = new TextArea();
        roomField.setPromptText("Room ID...");
        contentField = new TextArea();
        contentField.setPromptText("Content...");
        addPropField = new TextArea();
        addPropField.setPromptText("Additional Properties...");
    }

    private void updateQrCode() throws NoSuchAlgorithmException {
        String qrString = titleField.getText() + descField.getText() + roomField.getText() + contentField.getText();
        for(String line : addPropField.getText().split("\n"))
            qrString += line;

        String key = MungPass(qrString);

    }

    public static String MungPass(String pass) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        byte[] data = pass.getBytes();
        m.update(data,0,data.length);
        BigInteger i = new BigInteger(1,m.digest());
        return String.format("%1$032X", i);
    }
}
