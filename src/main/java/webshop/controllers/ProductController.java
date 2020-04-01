package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Product;
import repositories.ProductRepository;
import webshop.services.assemblers.ProductModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.dataCheckers.ProductDataChecker;
import webshop.services.exceptions.*;

@RestController // Het maken van ProductController REST controller
//De path moet met /Product beginnen om die controller te kunnen gebruiken
@RequestMapping(value = "/Product", produces = "application/hal+json")
public class ProductController {

	private final ProductRepository repository;// Voor het communiceren met database
	private final ProductModelAssembler assembler;// Om links te maken van de related objecten
	private final Converter converter;// Om het behalde info als stream te maken
	private final ProductDataChecker dataChecker;// De gegevens van het product te checken

	ProductController(ProductRepository repository, ProductModelAssembler assembler, Converter converter,
			ProductDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping // Get alle products van de database als Collection model
	public CollectionModel<EntityModel<Product>> getAllProducts() {
		List<Product> productsForDiscountCheck = (List<Product>) repository.findAll(); // Converteren naar stream
		for (int i = 0; i < productsForDiscountCheck.size(); i++) {// Reken de discount_price voor alle producten
			Product product = dataChecker.productDiscountCalculator(productsForDiscountCheck.get(i));
			productsForDiscountCheck.set(i, product);// Zet de gewijzigd products in de lijst
		}

		Stream<Product> stream = converter.toStream(repository.findAll());// Converteren naar stream
		List<EntityModel<Product>> products = stream.map(assembler::toModel).collect(Collectors.toList());

		// Returneren met een zelflink
		return new CollectionModel<>(products,
				linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
	}

	@GetMapping("/{id}") // Get een product opbasis van de megegeven product id
	public EntityModel<Product> getProduct(@PathVariable Long id) {
		Product product = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("product", id));
		product = dataChecker.productDiscountCalculator(product);

		return assembler.toModel(product);
	}

	@PostMapping // Een product opslaan in de database, hier moet het product als RequestBody
					// gestuurd
	ResponseEntity<?> saveProduct(@RequestBody Product newProduct) {
		try {
			if (dataChecker.productChecker(newProduct)) {// Check de gegevens van de megegeven product
				Product productToSave = dataChecker.categoriesListChecker(newProduct);
				productToSave.setId(null);// Maak het id null om in de database autogegenereerde id te krijgen en om de
				// conflicten van een bestaande product van dezelfde id te voorkomen

				Product savedProduct = repository.save(productToSave);// sla het product op in de database

				// Maak een entitymodel van het opgeslaagde product met zelflink
				EntityModel<Product> entityModel = new EntityModel<>(savedProduct,
						linkTo(methodOn(ProductController.class).getProduct(savedProduct.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.header("Product Name", savedProduct.getName()).body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			System.out.print(e);
			return ResponseEntity.badRequest()
					.body("Unable to create product: " + newProduct.getId() + "Please check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}") // Een product wijzigen op basis van het id die meegestuurd als PathVariable
	ResponseEntity<?> updateProduct(@RequestBody Product newProduct, @PathVariable Long id) {
		try {
			// Om te voorkomen dat er een nieuwe product gemaakt te worden als die product
			// niet bestaat
			Product roleTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("product", id));
			if (dataChecker.productChecker(newProduct)) {// Check product gegevens

				// Check als het product tot geen category behoort, zet hem in category 'New'
				Product productToUpdate = dataChecker.categoriesListChecker(newProduct);
				productToUpdate.setId(id);// zet de megegeven id
				Product updatedProduct = repository.save(productToUpdate);// Sla de wijzigingen op

				// Maak een entitymodel van het opgeslaagde product met zelflink
				EntityModel<Product> entityModel = new EntityModel<>(updatedProduct,
						linkTo(methodOn(ProductController.class).getProduct(updatedProduct.getId())).withSelfRel());

				// return created als het succesvul is
				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegestuurd bodydata niet juist zijn
				return ResponseEntity.badRequest()
						.body("Some information is not correct.\nPlease fill and check all required informations");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Product with id " + id
					+ " is not found or can't be updated\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@DeleteMapping("/{id}") // Een product verwijderen op basis van zijn id die meegestuurd als PathVriable
							// is
	ResponseEntity<?> deleteProduct(@PathVariable Long id) {
		try {
			// Check of dit product als bestaat
			Product product = repository.findById(id).orElseThrow(() -> new UnableToDeleteException("product", id));
			repository.deleteById(id);// Het product verwijderen

			// return Ok als het product verwijdered is
			return ResponseEntity.ok("Product with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			// Return een badrequest in het geval van fouten
			return ResponseEntity.badRequest()
					.body("Product with id " + id + " is not found or can't be deleted\nPlease check the id");
		}
	}
}