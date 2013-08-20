package fileIO;
import java.util.Vector;
import java.io.*;

public interface IfileIO {
      public abstract Vector<String> fileReadByLine(String fileDir);
      //Return a pointer pointed to the file written data into.
      public abstract FileOutputStream fileOutputStream(String fileDir);
      //Help close the file pointer.
      public abstract void FileOutputStreamClose (FileOutputStream fout);
}
