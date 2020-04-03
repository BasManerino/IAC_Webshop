package webshop.tools.datacheckers;

import org.springframework.stereotype.Component;
import model.Category;
import repositories.CategoryRepository;
import webshop.tools.exceptions.RequestNotFoundException;

@Component // Deze klasse is verantwoordelijk voor checks
public class CategoryDataChecker {

	private final CategoryRepository repository;

	CategoryDataChecker(CategoryRepository repository) {
		this.repository = repository;
	}

	// Check alle gegevens van de category
	public boolean categoryChecker(Category categoryToCheck) {
		if (categoryToCheck.getName().equals(null)) {
			return false;
		} else if (categoryToCheck.getDescription().equals(null)) {
			return false;
		} else if (categoryToCheck.getImageId().equals(null)) {
			return false;
		} else {
			return true;
		}
	}

	// Check of deze category bestaat wel of niet en check de gegevens
	public boolean findCategoryAndCheck(Category categoryToFind) {
		try {
			Long id = categoryToFind.getId();
			Category categoryToCheck = repository.findById(id)
					.orElseThrow(() -> new RequestNotFoundException("category", id));
			return categoryChecker(categoryToCheck);
		} catch (RuntimeException e) {
			return false;
		}
	}
}
