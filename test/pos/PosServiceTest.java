package pos;
import pos.model.*;import pos.service.PosService;
import java.nio.file.*;import java.math.*;import java.util.*;
public class PosServiceTest {
    static int passed=0;
    static void check(boolean b,String label){if(!b)throw new AssertionError(label);System.out.println("PASS: "+label);passed++;}
    static void rejects(Runnable r,String label){try{r.run();throw new AssertionError("Did not reject "+label);}catch(IllegalArgumentException ok){check(true,label);}}
    public static void main(String[] args)throws Exception{
        Path folder=Files.createTempDirectory("pos-tests-");Path file=folder.resolve("pos.bin");PosService s=new PosService(file);
        check(s.products().size()==5,"seed products");check(s.product("1001").quantity()==30,"initial stock");
        LinkedHashMap<String,Integer> cart=new LinkedHashMap<>();cart.put("1001",2);cart.put("1002",1);
        check(s.total(cart).compareTo(new BigDecimal("6.80"))==0,"cart totals exact with decimal currency");
        rejects(()->s.checkout(cart,"6.00"),"insufficient cash");check(s.product("1001").quantity()==30,"failed payment does not mutate stock");
        Sale sale=s.checkout(cart,"10.00");check(sale.change.compareTo(new BigDecimal("3.20"))==0,"correct change");
        check(s.product("1001").quantity()==28,"sale decrements inventory");
        check(s.receipt(sale).contains("S00001"),"receipt includes sale ID");
        rejects(()->s.checkout(Map.of("1003",100),"1000"),"overselling rejected");
        ReturnRecord ret=s.refund(sale.id,"1001",1);check(ret.refund.compareTo(new BigDecimal("2.50"))==0,"refund original price");
        check(s.product("1001").quantity()==29,"return restores stock");
        rejects(()->s.refund(sale.id,"1001",2),"excess duplicate return rejected");
        rejects(()->s.refund("NO-SALE","1001",1),"unknown sale rejected");
        rejects(()->s.refund(sale.id,"1003",1),"non-purchased item rejected");
        PosService restarted=new PosService(file);check(restarted.sales().size()==1&&restarted.returns().size()==1,"sales and returns persist after restart");
        check(restarted.product("1001").quantity()==29,"inventory persists after restart");
        restarted.addProduct("9999","Rice","3.75",5);check(restarted.product("9999").quantity()==5,"create product");
        restarted.restock("9999",3);check(restarted.product("9999").quantity()==8,"restock product");
        rejects(()->restarted.addProduct("9999","Rice","3.75",5),"duplicate SKU rejected");
        rejects(()->restarted.restock("9999",-1),"negative restock rejected");
        System.out.println("ALL TESTS PASSED: "+passed);
    }
}
