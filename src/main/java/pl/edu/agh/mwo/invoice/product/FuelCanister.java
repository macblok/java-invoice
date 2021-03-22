package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class FuelCanister extends Product {

    private final BigDecimal excise;

    public FuelCanister(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.23"));
        excise = new BigDecimal("5.56");
    }

    public BigDecimal getPriceWithTax() {
        return super.getPriceWithTax().add(excise);
    }

    public BigDecimal getExcise() {
        return excise;
    }

}
