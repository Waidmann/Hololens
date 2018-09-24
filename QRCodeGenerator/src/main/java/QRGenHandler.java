import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class QRGenHandler implements Initializable {

    @FXML
    public TextField titleField;
    @FXML
    public TextArea  propertiesTextArea;
    @FXML
    public ImageView qrCodeViewer;
    @FXML
    public TextField qrCodeID;
    @FXML
    public ListView<QREntry> qrListView;
    @FXML
    public StackPane topLevelPane;
    @FXML
    public VBox qrCodeHolder;

    public ImageView qrCodeEnlarged;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        qrListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        qrListView.getItems().addAll(AppUtils.getAllEntries());

        qrCodeID.setEditable(false);

        qrCodeEnlarged = new ImageView();
        qrCodeEnlarged.setTranslateX(-125);
        qrCodeEnlarged.setTranslateY(-15);

        topLevelPane.getChildren().add(qrCodeEnlarged);
        qrCodeEnlarged.toBack();

        qrCodeHolder.setOnMouseMoved(e->{
            if(qrListView.getSelectionModel().getSelectedItem() != null) {
                if (qrCodeViewer.getBoundsInParent().contains(new Point2D(e.getX(), e.getY()))) {
                    qrCodeEnlarged.setImage(AppUtils.genFxQR(qrListView.getSelectionModel().getSelectedItem().key, 250));
                    qrCodeEnlarged.toFront();
                    qrCodeEnlarged.setOnMouseExited(exitEvent->{
                        qrCodeEnlarged.toBack();
                        qrCodeEnlarged.setImage(null);
                    });
                }else{
                    qrCodeEnlarged.toBack();
                    qrCodeEnlarged.setImage(null);
                }
            }
        });
    }

    @FXML
    protected void listViewKeyPressHandle(KeyEvent e) {
        //DELETE, ENTER
        if(e.getCode() == KeyCode.DELETE)
            deletePopUp();
        else if(e.getCode() == KeyCode.ENTER)
            printPopup();
    }

    @FXML
    protected void listViewMouseClick()
    {
        if(qrListView.getSelectionModel().getSelectedItem() != null)
            selectEntry(qrListView.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void addNewQR(){
        qrListView.getItems().add(new QREntry());
    }

    @FXML
    protected void printSelectedItems(){
        printPopup();
    }

    @FXML
    protected void textChangedGenQR(){

        QREntry selected = qrListView.getSelectionModel().getSelectedItem();
        if(selected != null) {
            selected.properties = propertiesTextArea.getText();
            selected.title = titleField.getText();
            AppUtils.setEntry(selected);
            qrListView.refresh();
        }
    }

    public void deletePopUp(){
        ArrayList<QREntry> entries = new ArrayList<QREntry>(qrListView.getSelectionModel().getSelectedItems());
        entries.forEach(e->{
            AppUtils.jedisServer.del(e.key);
            qrListView.getItems().remove(e);
        });
        titleField.setText("");
        propertiesTextArea.setText("");
    }

    public void printPopup(){
        AppUtils.printCodes(new ArrayList<QREntry>(qrListView.getSelectionModel().getSelectedItems()));
    }


    public void selectEntry(QREntry entry){
        titleField.setText(entry.title);
        propertiesTextArea.setText(entry.properties);
        qrCodeID.setText(entry.key);
        qrCodeViewer.setImage(SwingFXUtils.toFXImage(AppUtils.generateQRCodeImage(entry.key, 100), null));
    }
}
