package pos.service;
import pos.model.*;import pos.store.Database;
import java.math.*;import java.nio.file.*;import java.util.*;
public class PosService {
    private Database db;private final Path file;
    public PosService(Path file){this.file=file;this.db=Database.load(file);}
    public synchronized Collection<Product> products(){return List.copyOf(db.products.values());}
    public synchronized Collection<Sale> sales(){return List.copyOf(db.sales.values());}
    public synchronized Collection<ReturnRecord> returns(){return List.copyOf(db.returns);}
    public synchronized Product product(String sku){Product p=db.products.get(sku);if(p==null)throw new IllegalArgumentException("Unknown SKU: "+sku);return p;}
    private void commit(Database candidate){candidate.save(file);db=candidate;}
    private Database snapshot(){try{
        java.io.ByteArrayOutputStream b=new java.io.ByteArrayOutputStream();try(java.io.ObjectOutputStream o=new java.io.ObjectOutputStream(b)){o.writeObject(db);}
        try(java.io.ObjectInputStream i=new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(b.toByteArray()))){return (Database)i.readObject();}
    }catch(Exception e){throw new IllegalStateException("Snapshot failed",e);}}
    public synchronized void addProduct(String sku,String name,String price,int stock){
        Database d=snapshot();if(d.products.containsKey(sku))throw new IllegalArgumentException("SKU already exists");d.products.put(sku,new Product(sku,name,price,stock));commit(d);
    }
    public synchronized void restock(String sku,int count){if(count<=0)throw new IllegalArgumentException("Quantity must be positive");Database d=snapshot();Product p=d.products.get(sku);if(p==null)throw new IllegalArgumentException("Unknown SKU");p.adjust(count);commit(d);}
    public synchronized BigDecimal total(Map<String,Integer> cart){if(cart.isEmpty())throw new IllegalArgumentException("Cart is empty");BigDecimal sum=BigDecimal.ZERO;
        for(var e:cart.entrySet()){if(e.getValue()==null||e.getValue()<=0)throw new IllegalArgumentException("Invalid quantity");Product p=product(e.getKey());if(p.quantity()<e.getValue())throw new IllegalArgumentException("Insufficient stock: "+p.name);sum=sum.add(p.price.multiply(BigDecimal.valueOf(e.getValue())));}
        return sum.setScale(2);
    }
    public synchronized Sale checkout(Map<String,Integer> cart,String cash){
        BigDecimal amount=total(cart);BigDecimal paid=money(cash);if(paid.compareTo(amount)<0)throw new IllegalArgumentException("Cash is below total");
        Database d=snapshot();LinkedHashMap<String,BigDecimal> prices=new LinkedHashMap<>();
        for(var e:cart.entrySet()){Product p=d.products.get(e.getKey());p.adjust(-e.getValue());prices.put(e.getKey(),p.price);}
        String id=String.format("S%05d",d.nextSale++);Sale s=new Sale(id,cart,prices,amount,paid);d.sales.put(id,s);commit(d);return s;
    }
    public synchronized ReturnRecord refund(String saleId,String sku,int qty){
        if(qty<=0)throw new IllegalArgumentException("Return quantity must be positive");
        Database d=snapshot();Sale s=d.sales.get(saleId);if(s==null)throw new IllegalArgumentException("Sale ID not found");
        if(s.returnable(sku)<qty)throw new IllegalArgumentException("Return exceeds originally purchased quantity / already refunded");
        Product p=d.products.get(sku);if(p==null)throw new IllegalStateException("Product missing");
        p.adjust(qty);s.returned.merge(sku,qty,Integer::sum);BigDecimal amount=s.prices.get(sku).multiply(BigDecimal.valueOf(qty));
        ReturnRecord r=new ReturnRecord(String.format("R%05d",d.nextReturn++),saleId,sku,qty,amount);d.returns.add(r);commit(d);return r;
    }
    private static BigDecimal money(String x){try{BigDecimal v=new BigDecimal(x.trim());if(v.signum()<0||v.scale()>2)throw new NumberFormatException();return v.setScale(2);}catch(Exception e){throw new IllegalArgumentException("Enter valid nonnegative cash (max 2 decimals)");}}
    public synchronized String receipt(Sale s){StringBuilder b=new StringBuilder("PROJECT POS 2026\nSALE RECEIPT\n"+s.id+" | "+s.time+"\n--------------------------------\n");
        for(var e:s.lines.entrySet())b.append(product(e.getKey()).name).append(" x").append(e.getValue()).append(" @ $").append(s.prices.get(e.getKey())).append("\n");
        return b.append("--------------------------------\nTOTAL  $").append(s.total).append("\nCASH   $").append(s.paid).append("\nCHANGE $").append(s.change).append("\nThank you!\n").toString();}
    public synchronized String returnReceipt(ReturnRecord r){return "PROJECT POS 2026\nRETURN RECEIPT\n"+r.id+" | "+r.time+"\nOriginal sale: "+r.saleId+"\nItem: "+product(r.sku).name+"\nQuantity: "+r.quantity+"\nREFUND: $"+r.refund+"\n";}
}
