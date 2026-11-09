package pos;
import pos.ui.PosFrame;import pos.service.PosService;
import javax.swing.*;import java.nio.file.*;
public class Main {
    public static void main(String[] args){
        Path db=Paths.get(System.getProperty("user.dir"),"data","pos-data.bin");
        SwingUtilities.invokeLater(()->{try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());new PosFrame(new PosService(db)).setVisible(true);}
        catch(Exception e){JOptionPane.showMessageDialog(null,e.getMessage(),"Startup error",JOptionPane.ERROR_MESSAGE);e.printStackTrace();}});
    }
}
