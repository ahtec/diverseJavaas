
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gerard 
 */
public class mqRemove2xCR {
    
    public static void main(String args[]) {
        Integer charval, keepCharvar;
        try {
            FileWriter fow = null;
            FileReader fir = null;
            if (args.length < 2) {
                System.out.println(" first param should be the input-file");
                System.out.println(" second the output-file");
                return;
            }
            String bronFileNaam = args[0];
            File fi = new File(bronFileNaam);
            if (fi.isDirectory()) {
                System.out.println("this is a directory, should be a file");
                return;
            }
            fir = new FileReader(fi);
            
            String doelFileNaam = args[1];
            File fo = new File(doelFileNaam);
            fow = new FileWriter(fo);
            
            keepCharvar = 9999999;
            
            while ((charval = fir.read()) > 0) {
//                fow.write(charval);
//                System.out.print(charval);
//                System.out.print(" in hex ");
//                System.out.println(Integer.toHexString(charval));
                if (keepCharvar == charval && charval == 10) {
//                if (keepCharvar == charval   ){
//                    System.out.println("Dit waren 2 oa's");
                    
                } else {
                    fow.write(charval);
                }
                keepCharvar = charval;                
            }
            
            fow.close();
            fir.close();

            
        } catch (IOException ex) {
            Logger.getLogger(mqRemove2xCR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
