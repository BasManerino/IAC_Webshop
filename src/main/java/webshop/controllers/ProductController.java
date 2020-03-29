package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Product;
import repositories.ProductRepository;
import webshop.services.assemblers.ProductModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.exceptions.RequestNotFoundException;
import webshop.services.exceptions.UnableToUpdateException;

@RestController
@RequestMapping(value = "/Product", produces = "application/hal+json")
public class ProductController {

	private final ProductRepository repository;
	private final ProductModelAssembler assembler;
	private final Converter converter;

	ProductController(ProductRepository repository, ProductModelAssembler assembler, Converter converter) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
	}

	@GetMapping
	public CollectionModel<EntityModel<Product>> getAllProducts() {
		Stream<Product> stream = converter.toStream(repository.findAll());
		List<EntityModel<Product>> products = stream.map(assembler::toModel).collect(Collectors.toList());

		return new CollectionModel<>(products,
				linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
	}

	@GetMapping("/{id}")
	public EntityModel<Product> getProduct(@PathVariable Long id) {
		Product product = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("product", id));

		return assembler.toModel(product);
	}

	@PostMapping
	ResponseEntity<?> saveProduct(@RequestBody Product newProduct) {
		try {
			Product productToSave = newProduct;
			productToSave.setId(null);
			Product savedProduct = repository.save(productToSave);

			EntityModel<Product> entityModel = new EntityModel<>(savedProduct,
					linkTo(methodOn(ProductController.class).getProduct(savedProduct.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Unable to create product: " + newProduct.getId());
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}")
	ResponseEntity<?> updateProduct(@RequestBody Product newProduct, @PathVariable Long id) {
		try {
			// This made to prevent make a new product if there's no product with such id
			Product roleTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("product", id));

			Product productToUpdate = newProduct;
			productToUpdate.setId(id);
			Product updatedProduct = repository.save(productToUpdate);

			EntityModel<Product> entityModel = new EntityModel<>(updatedProduct,
					linkTo(methodOn(ProductController.class).getProduct(updatedProduct.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Product with id " + id + " is not found or can't be updated");
		}
	}

	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteOrder(@PathVariable Long id) {
		try {
			repository.deleteById(id);

			return ResponseEntity.ok("Product with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Product with id " + id + " is not found or can't be deleted");
		}
	}
}