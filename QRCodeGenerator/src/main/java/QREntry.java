import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.Base64;

public class QREntry {

    public String key;
    public String title;
    public String properties;

    public QREntry(String key){
        String out = AppUtils.jedisServer.get(key);

        if(out != null)
        {
            System.out.println("Gotten value: " + out);
            String[] arr = out.split("&&");
            this.key = key;
            this.title = AppUtils.decode64(arr[0]);
            this.properties = "";
            for(int i=1;i<arr.length;i++)
                properties += AppUtils.decode64(arr[i]) + "\n";
            this.properties = properties.substring(0, Math.max(0,properties.length() - 1));
        }
    }

    public QREntry() {
        this.key = AppUtils.randomKey();
        this.title = "New object";
        this.properties = "";
    }
    
    public String getValue(){
        String[] props = properties.split("\n");

        String ret =  AppUtils.encode64(title) + "&&";

        for(String line : props)
            if(!line.equals(""))
                ret += AppUtils.encode64(line) + "&&";

        ret = ret.substring(0, ret.length() - 2);
        return ret;
    }

    @Override
    public String toString(){
        return title;
    }

    public BufferedImage getQR(int size){
        return AppUtils.generateQRCodeImage(key, size);
    }
}
