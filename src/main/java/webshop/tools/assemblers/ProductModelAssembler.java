package webshop.tools.assemblers;

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

	@Override // Het aanmaken van een EntityMode met links naar relaties
	public EntityModel<Product> toModel(Product product) {
		List<Category> categories = product.giveCategories();// Get alle categories van het product
		List<Link> categoriesLinksList = new ArrayList<Link>();// list te invullen met links naar categories
		for (Category category : categories) { // Links aanmaken voor categories en toevoegen aan de lijst
			Link link = linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withRel("categories");
			categoriesLinksList.add(link);
		}

		List<Discount> discounts = product.giveDiscounts();// Get alle discount van het product
		List<Link> discountsLinksList = new ArrayList<Link>();// list te invullen met links naar discounts
		for (Discount discount : discounts) { // Links aanmaken voor discounts en toevoegen aan de lijst
			Link link = linkTo(methodOn(DiscountController.class).getDiscount(discount.getId())).withRel("discounts");
			discountsLinksList.add(link);
		}

		List<Order> orders = product.giveOrders();// Get alle orders van het product
		List<Link> ordersLinksList = new ArrayList<Link>();// list te invullen met links naar orders
		for (Order order : orders) { // Links aanmaken voor orders en toevoegen aan de lijst
			Link link = linkTo(methodOn(OrderController.class).getOrder(order.getId())).withRel("orders");
			ordersLinksList.add(link);
		}

		// Return met links naar products, self product, categories, discounts en orders
		return new EntityModel<>(product,
				linkTo(methodOn(ProductController.class).getProduct(product.getId())).withSelfRel(),
				linkTo(methodOn(ProductController.class).getAllProducts()).withRel("products")).add(categoriesLinksList)
						.add(discountsLinksList).add(ordersLinksList);
	}
}
