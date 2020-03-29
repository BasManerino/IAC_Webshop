package webshop.services.datacheckers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import model.Category;
import model.Product;

@Component
public class ProductDataChecker {

	// Als het product aan geen category toegevoegd is, wordt die product in
	// category 'New' toegevoegd
	public Product categoriesListChecker(Product newProduct) {
		int count = 0;
		List<Category> categories = newProduct.giveCategories();
		if (categories != null) {
			for (Category category : categories) {
				if (category.getId() != null) {
					count += 1;
				}
			}
		}
		if (categories == null || count == 0) {
			categories = new ArrayList<Category>();
			Category newCategory = new Category();
			//ID 1 is de ID van de category 'New'
			newCategory.setId((long) 1);
			categories.add(newCategory);
		}
		newProduct.setCategories(categories);
		return newProduct;
	}
}
