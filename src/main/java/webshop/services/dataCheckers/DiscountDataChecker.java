package webshop.services.dataCheckers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import model.Discount;
import repositories.DiscountRepository;
import webshop.services.exceptions.RequestNotFoundException;

@Component
public class DiscountDataChecker {
	
	private final DiscountRepository repository;
	
	DiscountDataChecker(DiscountRepository repository){
		this.repository = repository;
	}

	public Discount discountAdTextChecker(Discount discount) {
		if (discount != null) {
			if (discountChecker(discount)) {
				int percentage = discount.getPercentage();
				DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
				String adText = "Discount " + percentage + "% from " + formatter.format(discount.getFrom_date()) + " until "
						+ formatter.format(discount.getUntil_date());
				discount.setAdText(adText);
			} else {
				discount.setAdText("This discount is expired!!!");
			}
		}

		return discount;
	}
	
	public boolean discountChecker(Discount discountToCheck) {
		Long id = discountToCheck.getId();
		Discount discount = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("discount", id));
		Date now = new Date();
		Date from = discount.getFrom_date();
		Date until = discount.getUntil_date();
		if ((from.before(now) || from.equals(now)) && (until.after(now) || until.equals(now))) {
			return true;
		} else {
			return false;
		}
	}
}
