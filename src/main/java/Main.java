
public class Main {
    public static void main(String[] args) {

            int[] time = {9, 30};
            System.out.println(time[0]);
            String start = "Mickiewicza";
            String target = "DWORZEC AUTOBUSOWY";
            System.out.println(start+" - "+target+" at "+time[0]+":"+time[1]+":00");
            Connection con = new Connection(start, target, time);
            con.search();
            Route result = con.getRoute();
            if(result!=null) result.print();
            else System.out.println("No available connection, check your input.");
        }
    }

