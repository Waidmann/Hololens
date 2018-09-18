package at.hololens.restserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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


	public static void main(String[] args) {

        jedisServer = new Jedis("localhost");
        SpringApplication.run(RestServerApplication.class, args);
	}


	@RequestMapping("/request")
	public String RestReturn(@RequestParam("key") String qrCode){

        String returnOfTheRedi = readResource(defaultReturn);

	    //tries to get the qrCodes value or if it doesnt respond sets the response to DB offline
        try {
            returnOfTheRedi = jedisServer.get(qrCode);
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
}
