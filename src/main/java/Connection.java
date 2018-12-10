import java.io.IOException;
import java.util.*;

public class Connection {
    public String startName;
    public String stopName;
    public int[] startAt;
    public int travelTime;
    private HashMap<String, Route> map;
    private GTFSReader gtfsReader;

    public Connection(String startName, String stopName, int[] startAt){
        this.startName = startName;
        this.stopName = stopName;
        this.startAt = startAt;
        this.travelTime = 123456789;
        this.map = new HashMap<String, Route>();
        Route start = new Route();
        start.endStop = startName;
        this.map.put(this.startName,start);
        try{
            gtfsReader=new GTFSReader("C:\\Users\\Szymon\\IdeaProjects\\SWDISK_MPK");
        } catch (IOException ioe){
            System.out.println("Nie znaleziono GTFS");
        }
    }

    public void search(){
        ArrayList<String> toExplore = CreateRoutes(map.get(startName));

        while(toExplore.size()>0) {
            ArrayList<String> newList = new ArrayList<String>();
            for (String key : toExplore) {
                Route test = map.get(key);
                if (!test.explored) {
                    test.explored = true;
                    if (!test.endStop.equals(this.stopName)) {
                        newList.addAll(CreateRoutes(map.get(key)));
                    }
                }
            }
            toExplore = new ArrayList<String>(newList);
            newList.clear();
        }

    }

    public ArrayList<String> CreateRoutes(Route route){
        String from = this.startName;
        int[] time = {0,0};
        time[0] = this.startAt[0];
        time[1] = this.startAt[1];
        ArrayList<String> toExplore = new ArrayList<String>();
        if(route != null){
            from = route.endStop;
            time[0] += Math.floorDiv(route.time,60);
            time[1] += route.time;
            if(time[1] > 60){
               time[1] =time[1] % 60;
               time[0]++;
            }
        }
        else route = new Route();
        List<GTFSReader.Odjazd> odjazds = gtfsReader.getData(from, time,new Date());
     //  System.out.println(from+" at "+time[0]+":"+time[1]);
        for (GTFSReader.Odjazd odjazd:odjazds)
        {
      //      System.out.println(odjazd);
            Route test = route.copy();
            int timeToNextStop = timeDiff(time, odjazd.getLeaveTime())+odjazd.getTimeForNextStop();
            Line line = new Line(odjazd.getLineNumber(),from,odjazd.getNextStop(),timeToNextStop);
            int totalTimeAfter = timeDiff(odjazd.getLeaveTime(), this.startAt) + odjazd.getTimeForNextStop();
            if(totalTimeAfter<travelTime && timeDiff(time, odjazd.getLeaveTime()) >= 0) {

                test.add(line);
                if (!map.containsKey(odjazd.getNextStop())) {
                    map.put(odjazd.getNextStop(), test);
                    toExplore.add(odjazd.getNextStop());
                } else {
                    Route current = map.get(odjazd.getNextStop());
                    if (current.time > test.time) {
                        map.replace(odjazd.getNextStop(), test);
                        if(odjazd.getNextStop().equals(this.stopName)) this.travelTime = test.time;
                    }
                }
            }
        }
        if(map.containsKey(this.stopName)){
            Route test = map.get(this.stopName);
            test.explored = true;
            this.travelTime = test.time;
        }

        return toExplore;
    }
    private int timeDiff(int[] t2, int[] t1){
        int time = (t1[0]-t2[0])*60 + t1[1]-t2[1];
        return time;
    }

    public Route getRoute(){
        return map.get(this.stopName);
    }
}
