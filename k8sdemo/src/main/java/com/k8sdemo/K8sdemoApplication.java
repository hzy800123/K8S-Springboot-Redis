package com.k8sdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@RestController
public class K8sdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(K8sdemoApplication.class, args);
	}


	@RequestMapping("/api/v1/test")
	public Map<String, String> testAPI(){
		Map<String, String> map = new HashMap<>(1);
		map.put("Hello", "k8s-springboot-redis");

		System.out.println("接口访问时间: " + LocalDateTime.now());

		return map;
	}

	/**************************redis测试*****************************/

	@Value("${redisIP}")
	String redisIP;

	@Value("${redisPort}")
	String redisPort;

	@Value("${redisPassword}")
	String redisPassword;

	@GetMapping("/redis")
	public String redis(@RequestParam(defaultValue = "a") String inputKey) {
		System.out.println("redisIP is - " + redisIP);
		System.out.println("redisPort is - " + redisPort);
		System.out.println("Integer.parseInt(redisPort) is - " + Integer.parseInt(redisPort));
		System.out.println("redisPassword is - " + redisPassword);

		Jedis jedis=new Jedis(redisIP, Integer.parseInt(redisPort));
		jedis.auth(redisPassword);
		System.out.println("Start to connect to Redis...");
		System.out.println(jedis.ping());
		String result = jedis.get(inputKey);

		if(result == null) {
			jedis.setnx(inputKey, "123");
			System.out.println("Input Key " + inputKey + " is not found. Set " + inputKey + " = 123");
		}
		result = jedis.get(inputKey);
		System.out.println("Result of " + inputKey + " = " + result);
		System.out.println(" ----------------------------- ");

		return "Connect Redis and get key is OK ! Result of " + inputKey + " = " + result;
	}

	@PostMapping("/redis/set")
	public String setKey(@RequestParam String inputKey, @RequestParam String inputValue) {
		Jedis jedis=new Jedis(redisIP, Integer.parseInt(redisPort));
		jedis.auth(redisPassword);

		jedis.set(inputKey, inputValue);
		String result = jedis.get(inputKey);
		System.out.println("Set " + inputKey + " = " + inputValue);
		System.out.println("Result of " + inputKey + " = " + result);
		System.out.println(" ----------------------------- ");

		return "Connect Redis and set key is OK ! Result of " + inputKey + " = " + result;
	}

}
