import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.embed.swing.SwingFXUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class QRGenHandler implements Initializable {

    @FXML
    public TextField titleField;
    @FXML
    public TextArea  propertiesTextArea;
    @FXML
    public ImageView qrCodeViewer;
    @FXML
    public Label qrCodeID;
    @FXML
    public ListView<QREntry> qrListView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        qrListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        qrListView.getItems().addAll(AppUtils.getAllEntries());
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
    protected void listViewMouseClick(){
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
        selected.properties = propertiesTextArea.getText();
        selected.title = titleField.getText();
        AppUtils.setEntry(selected);
        qrListView.refresh();
    }

    public void deletePopUp(){

    }

    public void printPopup(){

    }

    public void selectEntry(QREntry entry){
        titleField.setText(entry.title);
        propertiesTextArea.setText(entry.properties);
        qrCodeID.setText(entry.key);
        qrCodeViewer.setImage(SwingFXUtils.toFXImage(AppUtils.generateQRCodeImage(entry.key), null));
    }
}
