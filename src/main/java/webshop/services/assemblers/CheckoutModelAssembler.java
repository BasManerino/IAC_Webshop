package webshop.services.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import model.Checkout;
import model.Product;
import webshop.controllers.AccountController;
import webshop.controllers.CheckoutController;
import webshop.controllers.ProductController;

@Component
public class CheckoutModelAssembler implements RepresentationModelAssembler<Checkout, EntityModel<Checkout>> {

	@Override // Het aanmaken van een EntityMode met links naar relaties
	public EntityModel<Checkout> toModel(Checkout checkout) {
		List<Product> products = checkout.giveProducts();// Get alle products van de checkout
		List<Link> linksList = new ArrayList<Link>();// list te invullen met links naar products
		for (Product product : products) {// Links aanmaken voor products en toevoegen aan de lijst
			Link link = linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("products");
			linksList.add(link);
		}

		// Return met links naar checkouts, self checkout, account en products
		return new EntityModel<>(checkout,
				linkTo(methodOn(CheckoutController.class).getCheckout(checkout.getId())).withSelfRel(),
				linkTo(methodOn(CheckoutController.class).getAllCheckouts()).withRel("checkouts"),
				linkTo(methodOn(AccountController.class).getAccount(checkout.giveAccount().getId())).withRel("account"))
						.add(linksList);
	}
}
