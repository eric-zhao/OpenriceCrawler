package fileIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class FileIOImplementation implements IfileIO{
	
       public Vector<String> fileReadByLine(String fileDir){
    	   
       Vector<String> vString = new Vector<String>();
       BufferedReader br = null;
       String b;
       File  file = new File(fileDir);
	   try {
			FileReader fr = new FileReader(fileDir);
			br = new BufferedReader(fr);
							
			while ((b = br.readLine()) != null) {
//			System.out.println(b);
			vString.add(b);
             }
	  } catch (Exception e) {
	        e.printStackTrace();
	}finally 
	{
	        try 
	        {
		             br.close(); 
	        } 
	         catch (IOException e) 
	        {
	      e.printStackTrace();
	         }
	}
	return vString;
       }
       
       public FileOutputStream fileOutputStream(String fileDir){
    	   FileOutputStream fout = null;
    	   try{
        	   fout = new FileOutputStream (fileDir);
        	   }
        
        	   catch (IOException e)
        		{
        			System.err.println ("Unable to write to file");
        		}
        	   return fout;
       }
       
       public  void FileOutputStreamClose (FileOutputStream fout){
    	   try{
    		   fout.close();
    	   }
    	   catch(IOException e){
    		   System.err.println ("Unable to close the file");
    	   }
       }
}
