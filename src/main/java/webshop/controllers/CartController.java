package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.*;
import repositories.CartRepository;
import webshop.services.assemblers.CartModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.dataCheckers.CartDataChecker;
import webshop.services.exceptions.*;

@RestController // Het maken van CartController REST controller
//De path moet met /Cart beginnen om die controller te kunnen gebruiken
@RequestMapping(value = "/Cart", produces = "application/hal+json")
public class CartController {

	private final CartRepository repository; // Voor het communiceren met database
	private final CartModelAssembler assembler; // Om links te maken van de related objecten
	private final Converter converter; // Om het behalde info als stream te maken
	private final CartDataChecker dataChecker; // De gegevens van het cart te checken

	public CartController(CartRepository repository, CartModelAssembler assembler, Converter converter,
			CartDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping // Get alle carts van de database als Collection model
	public CollectionModel<EntityModel<Cart>> getAllCarts() {
		Stream<Cart> stream = converter.toStream(repository.findAll()); // Converteren naar stream
		List<EntityModel<Cart>> carts = stream.map(assembler::toModel).collect(Collectors.toList());

		// Returneren met een zelflink
		return new CollectionModel<>(carts, linkTo(methodOn(CartController.class).getAllCarts()).withSelfRel());
	}

	@GetMapping("/{id}") // Get een cart opbasis van de megegeven cart id
	public EntityModel<Cart> getCart(@PathVariable Long id) {
		Cart cart = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("cart", id));

		return assembler.toModel(cart);
	}

	// Een cart opslaan in de database, hier moet de cart als RequestBody gestuurd
	// De annotatie is verwijderd om geen manual cart te kunnen maken
	// Dit moet automatisch aangemaakt bij het aanmaken van een account
	ResponseEntity<?> saveCart(@RequestBody Cart newCart) {
		try {
			Cart savedCart = repository.save(newCart); // sla de cart op in de database

			// Maak een entitymodel van het opgeslaagde cart met zelflink
			EntityModel<Cart> entityModel = new EntityModel<>(savedCart,
					linkTo(methodOn(CartController.class).getCart(savedCart.getId())).withSelfRel());

			// return created als het succesvul is
			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest()
					.body("Unable to create cart: " + newCart.getId() + "\nPlease check all required informations");
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}") // Een cart wijzigen op basis van het id die meegestuurd als PathVariable
	ResponseEntity<?> updateCart(@RequestBody Cart newCart, @PathVariable long id) {
		try {
			// This made to prevent make a new cart if there's no cart with such id
			Cart cartTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("cart", id));

			// Check de beschikbaarheid van de megegeven producten die in de cart gezet
			// moeten worden
			Cart cartToUpdate = dataChecker.checkProductsAvailablity(newCart);
			cartToUpdate.setId(id);// zet de megegeven id
			Cart updatedCart = repository.save(cartToUpdate); // Sla de wijzigingen op

			// Maak een entitymodel van de opgeslaagde cart met zelflink
			EntityModel<Cart> entityModel = new EntityModel<>(updatedCart,
					linkTo(methodOn(CartController.class).getCart(updatedCart.getId())).withSelfRel());

			// return created als het succesvul is
			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body(
					"Cart with id " + id + " is not found or can't be updated\nPlease check all required informations");
		}
	}

	// Een cart verwijderen, de annotatie is niet gezet omdat de cart mag verwijderd
	// worden met het account
	ResponseEntity<?> deleteCart(@PathVariable Long id) {
		try {
			repository.deleteById(id);

			return ResponseEntity.ok("Cart with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest()
					.body("Cart with id " + id + " is not found or can't be deleted\nPlease check the id");
		}
	}

	@PutMapping("/{cartId}/Product/{productId}") // Een product toevoegen aan de cart
	ResponseEntity<?> addProductToCart(@PathVariable long cartId, @PathVariable long productId) {
		try {
			// Check de beschikbaarheid van het product
			if (dataChecker.checkProductAvailablity(productId)) {
				Cart cartToUpdate = repository.findById(cartId) // check of er een cart met zo'n id is
						.orElseThrow(() -> new UnableToUpdateException("cart", cartId));
				List<Product> products = cartToUpdate.giveProducts(); // krijg alle producten lijst
				Product product = new Product(); // een nieuwe product aanmaken
				product.setId(productId); // set de meegegeven product id in product
				products.add(product); // voeg het product aan de producten lijst
				cartToUpdate.setProducts(products); // set het lijst in de cart
				Cart updatedCart = repository.save(cartToUpdate); // update de cart

				// Maak een entitymodel van de opgeslaagde cart met zelflink
				EntityModel<Cart> entityModel = new EntityModel<>(updatedCart,
						linkTo(methodOn(CartController.class).getCart(updatedCart.getId())).withSelfRel());

				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegegeven product niet beschikbaar is
				return ResponseEntity.badRequest().body("This product is not more available");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Cart or product is not found or can't be updated");
		}
	}

	@DeleteMapping("/{cartId}/Product/{productId}") // verwijder een product met productId van cart met cartId
	ResponseEntity<?> removeProductFromCart(@PathVariable long cartId, @PathVariable long productId) {
		try {// check of er een cart met zo'n id is
			Cart cartToUpdate = repository.findById(cartId)
					.orElseThrow(() -> new UnableToUpdateException("cart", cartId));
			List<Product> productsIncart = cartToUpdate.giveProducts(); // Krijg producten lijst van het cart
			List<Product> productsToSave = new ArrayList<Product>(); // Maak een nieuwe cart voor het opslaan van de
																		// resterende producten
			boolean exists = false;
			for (Product product : productsIncart) { // Het product één keer verwijderen
				if (product.getId() == productId && !exists) { // !extsts om alleen maar één het product verwijderen
					exists = true;
				} else {
					productsToSave.add(product); // Sla in de productsToSave lijst
				}
			}
			if (exists) { // Check of het product was al in het cart
				cartToUpdate.setProducts(productsToSave); // Zet het productsToSave lijst in cart
				Cart updatedCart = repository.save(cartToUpdate); // wijzig het cart

				// Maak een entitymodel van de opgeslaagde cart met zelflink
				EntityModel<Cart> entityModel = new EntityModel<>(updatedCart,
						linkTo(methodOn(CartController.class).getCart(updatedCart.getId())).withSelfRel());

				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {// return badrequest als de meegegeven product niet in de cart is
				return ResponseEntity.badRequest()
						.body("Product with id " + productId + " doesn't exist in cart with id " + cartId);
			}

		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest()
					.body("Cart with id " + cartId + " is not found or can't be updated" + "\nPlease check the id");
		}
	}
}
