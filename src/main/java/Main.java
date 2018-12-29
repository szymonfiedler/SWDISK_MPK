
public class Main {
    public static void main(String[] args) {

            int[] time = {7,10};
            String start = "PILCZYCE";
            String target = "PL. GRUNWALDZKI";
            System.out.println(start+" - "+target+" at "+time[0]+":"+time[1]+":00");
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

