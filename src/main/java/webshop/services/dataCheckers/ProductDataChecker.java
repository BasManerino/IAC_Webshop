package webshop.services.dataCheckers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;
import model.Category;
import model.Discount;
import model.Product;
import repositories.ProductRepository;
import webshop.services.exceptions.RequestNotFoundException;

@Component
public class ProductDataChecker {
	
	private final ProductRepository repository;
	
	ProductDataChecker(ProductRepository repository) {
		this.repository = repository;
	}

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
			// ID 1 is de ID van de category 'New'
			newCategory.setId((long) 1);
			categories.add(newCategory);
		}
		newProduct.setCategories(categories);
		return newProduct;
	}

	public Product discountChecker(Product product) {
		List<Discount> discounts = product.giveDiscounts();
		if (discounts != null) {
			Date now = new Date();
			for (Discount discount : discounts) {
				if (discount != null) {
					if (discount.getFrom_date().before(now) && discount.getUntil_date().after(now)) {
						double normalPrice = product.getNormal_price();
						int percentage = discount.getPercentage();
						String discountPrice = String.format("%.2f",
								(normalPrice - ((normalPrice * percentage) / 100)));
						product.setDiscount_price(discountPrice);
					}
				}
			}
		}

		return product;
	}
	
	public List<Product> availablityChecker(Iterable<Long> productsToCheck) {
		List<Product> availableProducts = new ArrayList<Product>();
		Iterable<Product> products = repository.findAllById(productsToCheck);
		
		for (Product product : products) {
			System.out.print(product.getId());
			if (product.isAvailable()) {
				availableProducts.add(product);
			}
		}
		
		return availableProducts;
	}
	
	public boolean availablityChecker(Long productId) {
		Product product = repository.findById(productId).orElseThrow(() -> new RequestNotFoundException("product", productId));
		
		return product.isAvailable();
	}
}
