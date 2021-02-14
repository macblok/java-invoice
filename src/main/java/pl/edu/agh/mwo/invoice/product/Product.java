package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {
    private final String name;

    private final BigDecimal price;

    private final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal tax) {
    	if (name == null || name.equals("") || price == null || price.signum() == -1) {	//obiekty porownujemy przez .equals, bo inaczej może posypać sie kiedys program (patrz internowanie stringów), referencje do null porownujemy zawsze "=="
    		throw new IllegalArgumentException("You cannot create product with null or empty name or null or negate price.");
    	}
    	
    	if (price == null || price.signum() == -1) {	//obiekty porownujemy przez .equals, bo inaczej może posypać sie kiedys program (patrz internowanie stringów), referencje do null porownujemy zawsze "=="
    		throw new IllegalArgumentException("You cannot create product with null or empty name or null or negate price.");
    	}
    	
        this.name = name;
        this.price = price;
        this.taxPercent = tax;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BigDecimal getTaxPercent() {
        return this.taxPercent;
    }

    public BigDecimal getPriceWithTax() {
        return this.price.multiply(this.taxPercent).add(this.price);
    }
}
