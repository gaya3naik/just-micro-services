package com.ms.product.catalog.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.stream.Stream;

@EnableDiscoveryClient
@SpringBootApplication
public class ProductCatalogServiceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductCatalogServiceAppApplication.class, args);
	}

}


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
class Product {

	@Id
	@GeneratedValue
	private Long id;

	private String name;
	private String brand;
	private String category;
	private String label;

	public Product(String name, String category, String brand) {
		this.name = name;
		this.brand = brand;
		this.category = category;
		this.label = brand + name;
	}
}

@RepositoryRestResource
interface ProductRepository extends JpaRepository<Product, Long>{}

@Component
class ProductInitializer implements CommandLineRunner{

	private final ProductRepository productRepository;

	ProductInitializer(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Stream.of("Dabur", "Aashirwaad", "MTR", "Patanjali", "Britannia", "Daawat").forEach(brand->{
			Stream.of("Rice", "Dal", "Sugar", "Tea Powder", "Coffee Powder", "Chill Powder", "Salt").forEach(name->{
				Product foodProduct = new Product(name,  "Grocery", brand);
				productRepository.save(foodProduct);
			});

		});
		Stream.of("Raitu Bazaar", "BB").forEach(brand->{
		Stream.of("Tomatoes", "Green Peas", "Onion", "Potatoes", "Methid", "Palak", "Green Chilles").forEach(name->{
			Product vegProduct = new Product(name,  "Vegetables", brand);
			productRepository.save(vegProduct);
		});
		});

		Stream.of("At Home", "NeelKamal").forEach(brand->{
			Stream.of("Chair", "King Size Bed", "Queen Size Bed", "Sofa", "Dining Table").forEach(name->{
				Product product = new Product(name,  "Furniture", brand);
				productRepository.save(product);
			});
		});

		productRepository.findAll().forEach(System.out::println);
	}
}
