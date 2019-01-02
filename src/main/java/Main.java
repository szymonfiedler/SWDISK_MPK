
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


public class Main {
        public static Date date;

    public static void main(String[] args) {
            Logger log = (Logger) LoggerFactory.getLogger("org.onebusaway");
            log.setLevel(Level.OFF);
            Scanner in=new Scanner(System.in);
            System.out.println("Podaj datę odjazdu w konwencji dd/mm/yyyy");
            String data=in.nextLine();
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            try {
                    date= simpleDateFormat.parse(data);
            } catch (ParseException e) {
                    e.printStackTrace();
            }

            System.out.println("Podaj godzine odjazdu w konwencji hh:mm");
            String txt=in.nextLine();
            int[] time = new int[2];
            try{
                    time[0]=Integer.parseInt(txt.substring(0,2));
                    time[1]=Integer.parseInt(txt.substring(3,5));}
            catch (StringIndexOutOfBoundsException sioobe){
                    System.out.println("Podaj poprawną godzinę");
                    System.exit(-1);
            }
            System.out.println("Podaj przystanek z którego wyjeżdzasz");
            String start = in.nextLine();
            System.out.println("Podaj przystanek docelowy");
            String target = in.nextLine();
            System.out.println(start+" - "+target+" at "+time[0]+":"+time[1]+":00"+" date "+date.toString());
            Connection fast = new Connection(start, target, time);
            fast.search();
            Route result = fast.getRoute();
            if(result!=null) System.out.println(result);
            else System.out.println("No available connection, check your input.");
            System.out.println("or");
            Convinient convinient = new Convinient(start, target, time,false);
            convinient.search();
            result = convinient.getRoute();
            if(result!=null) System.out.println(result);
            else System.out.println("No available connection, check your input.");
        }
    }

