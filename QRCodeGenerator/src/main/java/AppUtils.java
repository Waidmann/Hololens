import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import redis.clients.jedis.Jedis;

import java.awt.*;
import java.awt.image.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AppUtils {

    public static Jedis jedisServer = new Jedis("localhost");

    public static ArrayList<QREntry> getAllEntries(){

        //TODO delete
        //jedisServer.flushAll();

        ArrayList<QREntry> ret = new ArrayList<>();
        Set<String> s = jedisServer.keys("*");
        for(String key : s){
            QREntry next = new QREntry(key);
            ret.add(next);
        }
        return ret;
    }

    public static String MungPass(String pass) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        byte[] data = pass.getBytes();
        m.update(data,0,data.length);
        BigInteger i = new BigInteger(1,m.digest());
        return String.format("%1$032X", i);
    }

    private static Random random = new Random();

    public static String randomKey(){
        try {
            return MungPass(System.currentTimeMillis() + "" + random.nextInt(5));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return random.nextInt(500) + "ERROR";
    }


    public static BufferedImage generateQRCodeImage(String text){
        try {
            int width = 150;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, width);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setEntry(QREntry entry) {
        String key = entry.key;
        String value = entry.getValue();
        jedisServer.set(key, value);
        jedisServer.save();

    }

    public static QREntry getEntry(String key){
        return new QREntry(key);
    }

    private static Base64.Encoder encoder = Base64.getEncoder();
    private static Base64.Decoder decoder = Base64.getDecoder();

    public static String encode64(String in){
        return new String(encoder.encode(in.getBytes()));
    }

    public static String decode64(String in){
        return new String(decoder.decode(in.getBytes()));
    }
}
