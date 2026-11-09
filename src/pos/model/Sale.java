package pos.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
public class Sale implements Serializable {
    private static final long serialVersionUID=1L;
    public final String id;
    public final LocalDateTime time;
    public final LinkedHashMap<String,Integer> lines;
    public final LinkedHashMap<String,BigDecimal> prices;
    public final BigDecimal total,paid,change;
    public final LinkedHashMap<String,Integer> returned=new LinkedHashMap<>();
    public Sale(String id,Map<String,Integer> lines,Map<String,BigDecimal> prices,BigDecimal total,BigDecimal paid){
        this.id=id;this.time=LocalDateTime.now();this.lines=new LinkedHashMap<>(lines);this.prices=new LinkedHashMap<>(prices);
        this.total=total;this.paid=paid;this.change=paid.subtract(total);
    }
    public int returnable(String sku){return lines.getOrDefault(sku,0)-returned.getOrDefault(sku,0);}
}
