package pos.store;
import pos.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class Database implements Serializable {
    private static final long serialVersionUID=1L;
    public final LinkedHashMap<String,Product> products=new LinkedHashMap<>();
    public final LinkedHashMap<String,Sale> sales=new LinkedHashMap<>();
    public final ArrayList<ReturnRecord> returns=new ArrayList<>();
    public long nextSale=1,nextReturn=1;
    public static Database load(Path file){
        if(!Files.exists(file)){
            Database d=new Database();d.products.put("1001",new Product("1001","Milk 1L","2.50",30));
            d.products.put("1002",new Product("1002","Bread","1.80",25));
            d.products.put("1003",new Product("1003","Eggs 12-pack","4.20",20));
            d.products.put("1004",new Product("1004","Apple","0.90",60));
            d.products.put("1005",new Product("1005","Coffee","6.50",12));return d;
        }
        try(ObjectInputStream in=new ObjectInputStream(Files.newInputStream(file))){return (Database)in.readObject();}
        catch(Exception e){throw new IllegalStateException("Database could not be loaded; file was NOT overwritten: "+file,e);}
    }
    public void save(Path file){
        try{
            Path target=file.toAbsolutePath();Files.createDirectories(target.getParent());Path tmp=target.resolveSibling(target.getFileName()+".tmp");
            try(ObjectOutputStream out=new ObjectOutputStream(Files.newOutputStream(tmp))){out.writeObject(this);}
            try{Files.move(tmp,target,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
            catch(AtomicMoveNotSupportedException e){Files.move(tmp,target,StandardCopyOption.REPLACE_EXISTING);}
        }catch(IOException e){throw new IllegalStateException("Saving database failed",e);}
    }
}
