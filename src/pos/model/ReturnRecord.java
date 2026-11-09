package pos.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class ReturnRecord implements Serializable {
    private static final long serialVersionUID=1L;
    public final String id,saleId,sku;public final int quantity;public final BigDecimal refund;public final LocalDateTime time;
    public ReturnRecord(String id,String saleId,String sku,int quantity,BigDecimal refund){this.id=id;this.saleId=saleId;this.sku=sku;this.quantity=quantity;this.refund=refund;this.time=LocalDateTime.now();}
}
