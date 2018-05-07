package classifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteOut{

    public static void writeOut(String path, ArrayList<String> out) {

        BufferedWriter bw = null;
        try {
            File file = new File(path);

            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (String s: out) {
                bw.write(s +  "\n");

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally
        {
            try{
                if(bw!=null)
                    bw.close();
            }catch(Exception ex){
                System.out.println("Error in closing the BufferedWriter"+ex);
            }
        }

    }
}
