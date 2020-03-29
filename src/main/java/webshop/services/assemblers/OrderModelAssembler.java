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

	@Override
	public EntityModel<Order> toModel(Order order) {
		List<Product> products = order.giveProducts();
		List<Link> linksList = new ArrayList<Link>();
		for (Product product : products) {
			Link link = linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("products");
			linksList.add(link);
		}

		return new EntityModel<>(order, linkTo(methodOn(OrderController.class).getOrder(order.getId())).withSelfRel(),
				linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"),
				linkTo(methodOn(AccountController.class).getAccount(order.giveAccount().getId())).withRel("account"))
						.add(linksList);
	}

}
