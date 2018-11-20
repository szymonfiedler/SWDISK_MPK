import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        GTFSReader gtfsReader=null;
        try{
            gtfsReader=new GTFSReader("C:\\Users\\Szymon\\IdeaProjects\\SWDISK_MPK");
        } catch (IOException ioe){
            System.out.println("Nie znaleziono GTFS");
        }
        if(gtfsReader!=null) {
            int[] time = {16, 30};
            Date today = new Date();
            List<GTFSReader.Odjazd> odjazds = gtfsReader.getData("Dzielna", time,today);
            for (GTFSReader.Odjazd odjazd:odjazds
            ) {
                System.out.println(odjazd);
            }

        }
    }
}
