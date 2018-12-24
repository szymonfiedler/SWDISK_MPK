
public class Main {
    public static void main(String[] args) {

            int[] time = {7,00};
            String start = "Dzielna";
            String target = "PL. GRUNWALDZKI";
            System.out.println(start+" - "+target+" at "+time[0]+":"+time[1]+":00");
            Connection fast = new Connection(start, target, time,false);
            fast.search();
            Route result = fast.getRoute();
            if(result!=null) System.out.println(result);
            else System.out.println("No available connection, check your input.");
            System.out.println("or");
            Connection convinient = new Connection(start, target, time,true);
            convinient.search();
            result = convinient.getRoute();
            if(result!=null) System.out.println(result);
            else System.out.println("No available connection, check your input.");
        }
    }

