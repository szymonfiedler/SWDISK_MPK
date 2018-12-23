import java.io.IOException;
import java.util.*;

public class Connection {
    public String startName;
    public String stopName;
    public int[] startAt;
    public int travelTime;
    private HashMap<String, Route> map;
    private GTFSReader gtfsReader;
    private Double[] targetCoordinates;

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
            gtfsReader=new GTFSReader("C:\\Users\\malcz\\IdeaProjects\\SWDISK_MPK");
        } catch (IOException ioe){
            System.out.println("Nie znaleziono GTFS");
        }
        this.targetCoordinates = gtfsReader.getData(this.stopName, this.startAt,new Date()).get(0).getCoordinates();
    }

    public void search(){
        ArrayList<String> toExplore = CreateRoutes(map.get(startName));

        while(toExplore.size()>0) {
            ArrayList<String> newList = new ArrayList<String>();
            String stop = this.findMostPromising(toExplore);
            Route test = map.get(stop);
            if (!test.explored) {
                test.explored = true;
                if (!test.endStop.equals(this.stopName)) {
                    newList.addAll(CreateRoutes(map.get(stop)));
                }
            }
            toExplore = new ArrayList<String>(newList);
            newList.clear();
        }

    }

    private String findMostPromising(ArrayList<String> stops){
        String best = "";
        Double bestScore = 200000000.0;
        for(String stop : stops){
            Route route = map.get(stop);
            Double[] coordinates = gtfsReader.getData(stop, this.startAt,new Date()).get(0).getCoordinates();
            Double distance;
            if(route.distance > 0){
                distance = route.distance;
            }
            else{
                distance = this.getDistance(coordinates, this.targetCoordinates);
            }
            Double score = route.time + distance;
            if(score < bestScore){
                bestScore = score;
                best = stop;
            }
        }

        return best;
    }

    private static Double getDistance(Double[] start, Double[] stop){
        return Math.sqrt(Math.pow(start[0]-stop[0],2)+Math.pow(start[1]-stop[1],2))*333;
    }

    private ArrayList<String> CreateRoutes(Route route){
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
        //System.out.println(from+" at "+time[0]+":"+time[1]);
        for (GTFSReader.Odjazd odjazd:odjazds)
        {
            //System.out.println(odjazd);
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
