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
public class CartModelAssembler implements RepresentationModelAssembler<Cart, EntityModel<Cart>>{
	
	@Override
	public EntityModel<Cart> toModel(Cart cart) {
		List<Product> products = cart.giveProducts();
		List<Link> linksList = new ArrayList<Link>();
		for (Product product : products) {
			Link link = linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("products");
			linksList.add(link);
		}
		
		return new EntityModel<>(cart, linkTo(methodOn(CartController.class).getCart(cart.getId())).withSelfRel(),
				linkTo(methodOn(CartController.class).getAllCarts()).withRel("carts"),
				linkTo(methodOn(AccountController.class).getAccount(cart.giveAccount().getId())).withRel("account")).add(linksList);
	}
}
