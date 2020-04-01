package webshop.services.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import model.Order;
import model.Product;
import webshop.controllers.AccountController;
import webshop.controllers.OrderController;
import webshop.controllers.ProductController;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

	@Override // Het aanmaken van een EntityMode met links naar relaties
	public EntityModel<Order> toModel(Order order) {
		List<Product> products = order.giveProducts();// Get alle products van de order
		List<Link> linksList = new ArrayList<Link>();// list te invullen met links naar products
		for (Product product : products) {// Links aanmaken voor products en toevoegen aan de lijst
			Link link = linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("products");
			linksList.add(link);
		}

		// Return met links naar orders, self order, account en products
		return new EntityModel<>(order, linkTo(methodOn(OrderController.class).getOrder(order.getId())).withSelfRel(),
				linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"),
				linkTo(methodOn(AccountController.class).getAccount(order.giveAccount().getId())).withRel("account"))
						.add(linksList);
	}

}
