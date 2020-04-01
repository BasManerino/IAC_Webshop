package webshop.services.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import model.Cart;
import model.Product;
import webshop.controllers.AccountController;
import webshop.controllers.CartController;
import webshop.controllers.ProductController;

@Component
public class CartModelAssembler implements RepresentationModelAssembler<Cart, EntityModel<Cart>> {

	@Override // Het aanmaken van een EntityMode met links naar relaties
	public EntityModel<Cart> toModel(Cart cart) {
		List<Product> products = cart.giveProducts();// Get alle products van de cart
		List<Link> linksList = new ArrayList<Link>();// list te invullen met links naar products
		for (Product product : products) {// Links aanmaken voor products en toevoegen aan de lijst
			Link link = linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("products");
			linksList.add(link);
		}

		// Return met links naar carts, self cart, account en products
		return new EntityModel<>(cart, linkTo(methodOn(CartController.class).getCart(cart.getId())).withSelfRel(),
				linkTo(methodOn(CartController.class).getAllCarts()).withRel("carts"),
				linkTo(methodOn(AccountController.class).getAccount(cart.giveAccount().getId())).withRel("account"))
						.add(linksList);
	}
}
