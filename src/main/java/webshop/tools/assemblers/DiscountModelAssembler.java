package webshop.tools.assemblers;

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

	@Override // Het aanmaken van een EntityMode met links naar relaties
	public EntityModel<Discount> toModel(Discount discount) {
		List<Product> products = discount.giveProducts();// Get alle products van de discount
		List<Link> linksList = new ArrayList<Link>();// list te invullen met links naar products
		for (Product product : products) {// Links aanmaken voor products en toevoegen aan de lijst
			Link link = linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("products");
			linksList.add(link);
		}

		// Return met links naar discounts, self discount en products
		return new EntityModel<>(discount,
				linkTo(methodOn(DiscountController.class).getDiscount(discount.getId())).withSelfRel(),
				linkTo(methodOn(DiscountController.class).getAllDiscounts()).withRel("discounts")).add(linksList);
	}

}