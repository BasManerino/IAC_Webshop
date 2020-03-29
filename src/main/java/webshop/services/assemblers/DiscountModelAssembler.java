package webshop.services.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import model.Discount;
import model.Product;
import webshop.controllers.DiscountController;
import webshop.controllers.ProductController;

@Component
public class DiscountModelAssembler implements RepresentationModelAssembler<Discount, EntityModel<Discount>> {

	@Override
	public EntityModel<Discount> toModel(Discount discount) {
		List<Product> products = discount.giveProducts();
		List<Link> linksList = new ArrayList<Link>();
		for (Product product : products) {
			Link link = linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("products");
			linksList.add(link);
		}

		return new EntityModel<>(discount,
				linkTo(methodOn(DiscountController.class).getDiscount(discount.getId())).withSelfRel(),
				linkTo(methodOn(DiscountController.class).getAllDiscounts()).withRel("discounts")).add(linksList);
	}

}