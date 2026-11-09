package pos.model;
import java.io.Serializable;
import java.math.BigDecimal;
public class Product implements Serializable {
    private static final long serialVersionUID=1L;
    public final String sku, name;
    public final BigDecimal price;
    private int quantity;
    public Product(String sku,String name,String price,int quantity){
        if(sku==null||sku.isBlank()||name==null||name.isBlank()||quantity<0)throw new IllegalArgumentException("Invalid product");
        this.sku=sku;this.name=name;this.price=new BigDecimal(price).setScale(2);if(this.price.signum()<0)throw new IllegalArgumentException("Negative price");this.quantity=quantity;
    }
    public int quantity(){return quantity;}
    public void adjust(int diff){if((long)quantity+diff<0||(long)quantity+diff>Integer.MAX_VALUE)throw new IllegalArgumentException("Insufficient/invalid stock");quantity+=diff;}
    public String toString(){return sku+" | "+name+" | $"+price+" | stock "+quantity;}
}
