import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import redis.clients.jedis.Jedis;

import javax.print.PrintService;
import java.awt.*;
import java.awt.image.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AppUtils {

    public static Jedis jedisServer = new Jedis("localhost");

    public static ArrayList<QREntry> getAllEntries() {

        //TODO delete
        //jedisServer.flushAll();

        ArrayList<QREntry> ret = new ArrayList<>();
        Set<String> s = jedisServer.keys("*");
        for (String key : s) {
            QREntry next = new QREntry(key);
            ret.add(next);
        }
        return ret;
    }

    public static String MungPass(String pass) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        byte[] data = pass.getBytes();
        m.update(data, 0, data.length);
        BigInteger i = new BigInteger(1, m.digest());
        return String.format("%1$032X", i);
    }

    private static Random random = new Random();

    public static String randomKey() {
        try {
            return MungPass(System.currentTimeMillis() + "" + random.nextInt(5));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return random.nextInt(500) + "ERROR";
    }


    public static BufferedImage generateQRCodeImage(String text, int size) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Image genFxQR(String text, int size){
         return SwingFXUtils.toFXImage(generateQRCodeImage(text, size), null);
    }

    public static void setEntry(QREntry entry) {
        String key = entry.key;
        String value = entry.getValue();
        jedisServer.set(key, value);
        jedisServer.save();

    }

    public static QREntry getEntry(String key) {
        return new QREntry(key);
    }

    private static Base64.Encoder encoder = Base64.getEncoder();
    private static Base64.Decoder decoder = Base64.getDecoder();

    public static String encode64(String in) {
        return new String(encoder.encode(in.getBytes()));
    }

    public static String decode64(String in) {
        return new String(decoder.decode(in.getBytes()));
    }


    public static void printCodes(ArrayList<QREntry> entries) {

        ChoiceDialog<PrintService> dialog = new ChoiceDialog<PrintService>(PrinterJob.lookupPrintServices()[0], PrinterJob.lookupPrintServices());
        dialog.setHeaderText("Choose the printer!");
        dialog.setContentText("Choose a printer from available printers");
        dialog.setTitle("Printer Choice");
        Optional<PrintService> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            confirmPrint(opt.get(), entries);
        }
    }

    private static void confirmPrint(PrintService ps, ArrayList<QREntry> entries) {

        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new Printable() {
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex != 0) {
                    return NO_SUCH_PAGE;
                }

                int x = 0;
                int y = 1;

                for (int i = 0; i < entries.size(); i++) {

                    x++;
                    if (x == 4) {
                        x = 1;
                        y++;
                    }

                    BufferedImage image = entries.get(i).getQR(200);
                    double offsetX = graphics.getFontMetrics().getStringBounds(entries.get(i).title, graphics).getWidth();
                    graphics.drawImage(image, x * 160 - 100, y * 160 - 50, 150, 150, null);
                    graphics.drawString(entries.get(i).title, x * 160 - 100 + (int)offsetX, y * 160 - 40);
                }
                return PAGE_EXISTS;
            }
        });
        try {
            printJob.setPrintService(ps);
            printJob.print();
        } catch (PrinterException e1) {
            e1.printStackTrace();
        }
    }
}
