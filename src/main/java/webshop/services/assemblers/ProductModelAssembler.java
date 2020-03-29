package webshop.services.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import model.Category;
import model.Discount;
import model.Order;
import model.Product;
import webshop.controllers.CategoryController;
import webshop.controllers.DiscountController;
import webshop.controllers.OrderController;
import webshop.controllers.ProductController;

@Component
public class ProductModelAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>> {

	@Override
	public EntityModel<Product> toModel(Product product) {
		List<Category> categories = product.giveCategories();
		List<Link> categoriesLinksList = new ArrayList<Link>();
		for (Category category : categories) {
			Link link = linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withRel("categories");
			categoriesLinksList.add(link);
		}

		List<Discount> discounts = product.giveDiscounts();
		List<Link> discountsLinksList = new ArrayList<Link>();
		for (Discount discount : discounts) {
			Link link = linkTo(methodOn(DiscountController.class).getDiscount(discount.getId())).withRel("discounts");
			discountsLinksList.add(link);
		}

		List<Order> orders = product.giveOrders();
		List<Link> ordersLinksList = new ArrayList<Link>();
		for (Order order : orders) {
			Link link = linkTo(methodOn(OrderController.class).getOrder(order.getId())).withRel("orders");
			ordersLinksList.add(link);
		}

		return new EntityModel<>(product,
				linkTo(methodOn(ProductController.class).getProduct(product.getId())).withSelfRel(),
				linkTo(methodOn(ProductController.class).getAllProducts()).withRel("products")).add(categoriesLinksList)
						.add(discountsLinksList).add(ordersLinksList);
	}
}
