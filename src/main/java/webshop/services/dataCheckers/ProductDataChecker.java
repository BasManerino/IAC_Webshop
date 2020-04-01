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
	private final DiscountDataChecker discountDataChecker;
	
	ProductDataChecker(ProductRepository repository, DiscountDataChecker discountDataChecker) {
		this.repository = repository;
		this.discountDataChecker = discountDataChecker;
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
	
	public List<Product> availablityCheckerListIds(Iterable<Long> productsToCheck) {
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
	
	public boolean availablityCheckerListProducts(Iterable<Product> productsToCheck) {
		List<Long> productsIds = new ArrayList<Long>();

		for (Product product : productsToCheck) {
			productsIds.add(product.getId());
		}
		
		Iterable<Product> products = repository.findAllById(productsIds);
		
		for (Product product : products) {
			if (!product.isAvailable()) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean availablityCheckerProductId(Long productId) {
		try {
			Product product = repository.findById(productId).orElseThrow(() -> new RequestNotFoundException("product", productId));
			
			return product.isAvailable();
		} catch (RuntimeException e) {
			return false;
		}
	}
	
	public boolean checkProductsOfferCode(Iterable<Product> productsToCheck, Long offerCode) {
		Discount discountToCheck = new Discount();
		discountToCheck.setId(offerCode);
		List<Long> productsIds = new ArrayList<Long>();

		for (Product product : productsToCheck) {
			productsIds.add(product.getId());
		}
		
		Iterable<Product> products = repository.findAllById(productsIds);
		
		for (Product product : products) {
			List <Discount> discounts = product.giveDiscounts();
			for (Discount discount : discounts) {
				if (discount.getId() == offerCode) {
					return discountDataChecker.discountChecker(discountToCheck);
				}
			}
		}
		
		return false;
	}
}
