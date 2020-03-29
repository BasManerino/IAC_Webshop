package webshop.services.dataCheckers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import model.Discount;

@Component
public class DiscountDataChecker {

	public Discount discountAdTextChecker(Discount discount) {
		if (discount != null) {
			Date now = new Date();
			Date from = discount.getFrom_date();
			Date until = discount.getUntil_date();
			if (discount.getFrom_date().before(now) && discount.getUntil_date().after(now)) {
				int percentage = discount.getPercentage();
				DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
				String adText = "Discount " + percentage + "% from " + formatter.format(from) + " until "
						+ formatter.format(until);
				discount.setAdText(adText);
			} else {
				discount.setAdText("This discount is expired!!!");
			}
		}

		return discount;
	}
}
