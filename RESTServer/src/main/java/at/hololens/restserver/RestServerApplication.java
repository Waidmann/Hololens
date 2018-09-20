package at.hololens.restserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Base64;
import java.util.Set;

@SpringBootApplication @RestController
public class RestServerApplication {

    private static RestServerApplication application;

	public static Jedis jedisServer;

    @Value("classpath:exampleReturn.json")
    private Resource defaultReturn;

    @Value("classpath:exampleReturn.json")
    private Resource dbOfflineReturn;

    @Value("classpath:exampleReturn.json")
    private Resource wrongQRReturn;

    @Value("classpath:replaceReturn.json")
    private Resource replaceable;


	public static void main(String[] args) {

        jedisServer = new Jedis("localhost");

        Set<String> arr = jedisServer.keys("*");
        for(String ent : arr)
            System.out.println(ent);

        SpringApplication.run(RestServerApplication.class, args);
	}


	@RequestMapping(value = "/objects/{key}")
	public String RestReturn(@PathVariable("key") String qrCode){

        String returnOfTheRedi = readResource(defaultReturn);

	    //tries to get the qrCodes value or if it doesnt respond sets the response to DB offline
        try {
            returnOfTheRedi =  toJson(qrCode);
        }catch(JedisConnectionException e){
            returnOfTheRedi = readResource(dbOfflineReturn);
        }

	    //if the qrCode is not in the database
        if(returnOfTheRedi == null)
            returnOfTheRedi = readResource(wrongQRReturn);

		return returnOfTheRedi;
	}

	private String readResource(Resource r){
        byte[] ret = new byte[0];
        try {
            ret = r.getInputStream().readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(ret);
    }

    private static Base64.Decoder decoder = Base64.getDecoder();

    public static String decode64(String in){
        return new String(decoder.decode(in.getBytes()));
    }

    public String toJson(String key){
        String out = jedisServer.get(key);

        String[] arr = out.split("&&");

        String ret = readResource(replaceable);
        ret = ret.replace("<title>", decode64(arr[0]));

        String propArray = "";
        for(int i = 1; i<arr.length;i++)
        {
            propArray += "\"" + decode64(arr[i]) + "\",\n";
        }
        propArray = propArray.substring(0, Math.max(0, propArray.length() - 2));
        ret = ret.replace("<addProps>", propArray);
        return ret;
    }
}
