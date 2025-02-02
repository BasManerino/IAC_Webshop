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
import model.Product;
import webshop.controllers.CategoryController;
import webshop.controllers.ProductController;

@Component
public class CategoryModelAssembler implements RepresentationModelAssembler<Category, EntityModel<Category>> {

	@Override // Het aanmaken van een EntityMode met links naar relaties
	public EntityModel<Category> toModel(Category category) {
		List<Product> products = category.giveProducts();// Get alle products van de category
		List<Link> linksList = new ArrayList<Link>();// list te invullen met links naar products
		for (Product product : products) {// Links aanmaken voor products en toevoegen aan de lijst
			Link link = linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("products");
			linksList.add(link);
		}

		// Return met links naar categories, self category en products
		return new EntityModel<>(category,
				linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withSelfRel(),
				linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories")).add(linksList);
	}

}
