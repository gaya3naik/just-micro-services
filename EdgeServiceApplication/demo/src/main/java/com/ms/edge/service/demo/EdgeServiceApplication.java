package com.ms.edge.service.demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.CollectionModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
@EnableFeignClients
@SpringBootApplication
public class EdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeServiceApplication.class, args);
	}

}

@Data
@Getter
@Setter
class Product {
	private String label;
	private String name;
	private String category;

	Product(String label, String name, String category) {
		this.label = label;
		this.name = name;
		this.category = category;
	}
}

@FeignClient("product-catalog-service")
interface ProductClient {
	@GetMapping("/products")
	CollectionModel<Product> fetchProducts();
}

@RestController
class EssentialsApiRestController{
	private final ProductClient productClient;

	public EssentialsApiRestController(ProductClient productClient){
		this.productClient = productClient;
	}

	public Collection<Product> fallback(Throwable exception ){
		return new ArrayList<>();
	}

	@HystrixCommand(fallbackMethod = "fallback", commandProperties =  {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100000")})
	@GetMapping("/essentials")
	@CrossOrigin(origins = "*")
	public Object essentials(){
		List<String> essentials = productClient.fetchProducts().getContent().stream().filter(this::isEssential).map(Product::getLabel)
				.collect(Collectors.toList());
		if(CollectionUtils.isEmpty(essentials)){
			return "Nothing available now. Please check back later!";
		}
		return essentials;
	}

	private boolean isEssential(Product product){
		return product.getCategory().equalsIgnoreCase("Vegetables") || product.getCategory().equalsIgnoreCase("Grocery") || product.getCategory().equalsIgnoreCase("Fruits");
	}
}