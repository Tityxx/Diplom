import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import romanow.snn_simulator.fft.FFTAudioTextFile;

public class FileDescription extends FFTAudioTextFile
{
    DateTime createDate = new DateTime();
    String lepNumber="";            // Номер опоры
    String srcNumber="";            // Номер датчика
    String comment="";              // Комментарий
    String originalFileName="";     // Оригинальное имя

    public String parseFromName()
    {
        try
        {
            String ss = originalFileName.toLowerCase();
            if (!ss.endsWith(".txt"))
            {
                return originalFileName + " - тип файла - не txt";
            }
            ss = ss.substring(0,ss.length()-4);
            int idx1=ss.indexOf("_");
            int idx2=ss.lastIndexOf("_");
            if (idx1==-1 || idx2==-1 || idx1==idx2)
            {
                return originalFileName+": формат имени, нет \'_\'";
            }
            lepNumber = ss.substring(idx2+1);
            srcNumber = ss.substring(idx1+1,idx2);
            ss = ss.substring(0,idx1);
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss");
            createDate = formatter.parseDateTime(ss);
            return null;
        }
        catch(Exception ee)
        {
            return originalFileName+": "+ee.toString();
        }
    }
    public String toString()
    {
        return lepNumber+" "+srcNumber+"\n"+createDate.toString(DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss"));
    }
    public FileDescription(String name)
    {
        originalFileName = name;
    }
}
