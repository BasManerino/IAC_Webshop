package webshop.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import model.Cart;
import model.Product;
import repositories.CartRepository;
import webshop.services.assemblers.CartModelAssembler;
import webshop.services.converters.Converter;
import webshop.services.dataCheckers.CartDataChecker;
import webshop.services.exceptions.*;

@RestController
@RequestMapping(value = "/Cart", produces = "application/hal+json")
public class CartController {

	private final CartRepository repository;
	private final CartModelAssembler assembler;
	private final Converter converter;
	private final CartDataChecker dataChecker;

	public CartController(CartRepository repository, CartModelAssembler assembler, Converter converter,
			CartDataChecker dataChecker) {
		this.repository = repository;
		this.assembler = assembler;
		this.converter = converter;
		this.dataChecker = dataChecker;
	}

	@GetMapping
	public CollectionModel<EntityModel<Cart>> getAllCarts() {
		Stream<Cart> stream = converter.toStream(repository.findAll());
		List<EntityModel<Cart>> carts = stream.map(assembler::toModel).collect(Collectors.toList());

		return new CollectionModel<>(carts, linkTo(methodOn(CartController.class).getAllCarts()).withSelfRel());
	}

	@GetMapping("/{id}")
	public EntityModel<Cart> getCart(@PathVariable Long id) {
		Cart cart = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("cart", id));

		return assembler.toModel(cart);
	}

	ResponseEntity<?> saveCart(@RequestBody Cart newCart) {
		try {
			Cart savedCart = repository.save(newCart);

			EntityModel<Cart> entityModel = new EntityModel<>(savedCart,
					linkTo(methodOn(CartController.class).getCart(savedCart.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Unable to create cart: " + newCart.getId());
		}
	}

	@SuppressWarnings("unused")
	@PutMapping("/{id}")
	ResponseEntity<?> updateCart(@RequestBody Cart newCart, @PathVariable long id) {
		try {
			// This made to prevent make a new cart if there's no cart with such id
			Cart cartTest = repository.findById(id).orElseThrow(() -> new UnableToUpdateException("cart", id));

			Cart cartToUpdate = dataChecker.checkProductsAvailablity(newCart);
			cartToUpdate.setId(id);
			Cart updatedCart = repository.save(cartToUpdate);

			EntityModel<Cart> entityModel = new EntityModel<>(updatedCart,
					linkTo(methodOn(CartController.class).getCart(updatedCart.getId())).withSelfRel());

			return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
					.body(entityModel);
		} catch (URISyntaxException | RuntimeException e) {
			System.out.print(e);
			return ResponseEntity.badRequest().body("Cart with id " + id + " is not found or can't be updated");
		}
	}

	ResponseEntity<?> deleteCart(@PathVariable Long id) {
		try {
			repository.deleteById(id);

			return ResponseEntity.ok("Cart with id: " + id + " is deleted");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Cart with id " + id + " is not found or can't be deleted");
		}
	}

	@PutMapping("/{cartId}/Product/{productId}")
	ResponseEntity<?> addProductToCart(@PathVariable long cartId, @PathVariable long productId) {
		try {
			Boolean available = dataChecker.checkProductAvailablity(productId);
			if (available) {
				Cart cartToUpdate = repository.findById(cartId)
						.orElseThrow(() -> new UnableToUpdateException("cart", cartId));
				List<Product> products = cartToUpdate.giveProducts();
				Product product = new Product();
				product.setId(productId);
				products.add(product);
				Cart updatedCart = repository.save(cartToUpdate);

				EntityModel<Cart> entityModel = new EntityModel<>(updatedCart,
						linkTo(methodOn(CartController.class).getCart(updatedCart.getId())).withSelfRel());

				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {
				return ResponseEntity.badRequest().body("This product is not more available");
			}
		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("This cart or product is not found or can't be updated");
		}
	}

	@DeleteMapping("/{cartId}/Product/{productId}")
	ResponseEntity<?> removeProductFromCart(@PathVariable long cartId, @PathVariable long productId) {
		try {
			Cart cartToUpdate = repository.findById(cartId)
					.orElseThrow(() -> new UnableToUpdateException("cart", cartId));
			List<Product> productsIncart = cartToUpdate.giveProducts();
			List<Product> productsToSave = new ArrayList<Product>();
			boolean exists = false;
			for (Product product : productsIncart) {
				if (product.getId() == productId && !exists) { // Also to prevent double deleting
					exists = true;
				} else {
					productsToSave.add(product);
				}
			}
			if (exists) {
				cartToUpdate.setProducts(productsToSave);
				Cart updatedCart = repository.save(cartToUpdate);

				EntityModel<Cart> entityModel = new EntityModel<>(updatedCart,
						linkTo(methodOn(CartController.class).getCart(updatedCart.getId())).withSelfRel());

				return ResponseEntity.created(new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
						.body(entityModel);
			} else {
				return ResponseEntity.badRequest()
						.body("Product with id " + productId + " doesn't exist in cart with id " + cartId);
			}

		} catch (URISyntaxException | RuntimeException e) {
			return ResponseEntity.badRequest().body("Cart with id " + cartId + " is not found or can't be updated");
		}
	}
}
