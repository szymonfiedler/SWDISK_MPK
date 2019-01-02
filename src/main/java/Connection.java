import java.io.IOException;
import java.util.*;

public class Connection {
    public String startName;
    public String stopName;
    public int[] startAt;

    //best found scores
    public int travelTime;
    public int lines = 2000000;

    private HashMap<String, Route> map;
    private GTFSReader gtfsReader;
    private Double[] targetCoordinates;

    private HashMap<String, Double[]> coords = new HashMap<String, Double[]>();

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
            gtfsReader=new GTFSReader("GTFS");
        } catch (IOException ioe){
            System.out.println("Nie znaleziono GTFS");
        }
        this.targetCoordinates = gtfsReader.getData(this.stopName, this.startAt,Main.date).get(0).getCoordinates();
    }

    public void search(){
        ArrayList<String> init = CreateRoutes(map.get(startName));
        ArrayList<String> toExplore = new ArrayList<String>();
        for (String element : init) {
            if (!toExplore.contains(element)) {
                toExplore.add(element);
            }
        }

        while(toExplore.size()>0) {
            ArrayList<String> newList;
            String stop = this.findMostPromising(toExplore);
            if(stop.equals("")){
                return;
            }
            toExplore.remove(toExplore.indexOf(stop));
            Route test = map.get(stop);
            if (!test.explored) {
                test.explored = true;
                if (!test.endStop.equals(this.stopName)) {
                    newList = CreateRoutes(map.get(stop));
                    for(String toAdd : newList){
                        if(!toExplore.contains(toAdd))toExplore.add(toAdd);
                    }
                    newList.clear();
                }
            }
        }

    }

    private String findMostPromising(ArrayList<String> stops){
        String best = "";
        Double bestScore = 200000000.0;
        for(String stop : stops){
            Route route = map.get(stop);
            Double[] coordinates = coords.get(stop);
            Double distance;
            if(route.distance > 0){
                distance = route.distance;
            }
            else{
                distance = this.getDistance(coordinates, this.targetCoordinates);
            }
            Double score = route.time + distance;
            if(score < bestScore && !route.explored){
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
        if(timeDiff(this.startAt,time)>this.travelTime){
            return toExplore;
        }
        List<GTFSReader.Odjazd> odjazds = gtfsReader.getData(from, time,Main.date);
        //System.out.println(from+" at "+time[0]+":"+time[1]+ " found: "+(map.containsKey(this.stopName)?map.get(this.stopName).time:"nope"));
        for (GTFSReader.Odjazd odjazd:odjazds)
        {
            //System.out.println(odjazd);
            Route test = route.copy();
            int timeToNextStop = timeDiff(time, odjazd.getLeaveTime())+odjazd.getTimeForNextStop();
            Line line = new Line(odjazd.getLineNumber(),from,odjazd.getNextStop(),timeToNextStop);
            int totalTimeAfter = timeDiff(this.startAt, odjazd.getLeaveTime()) + odjazd.getTimeForNextStop();
            if(!coords.containsKey(odjazd.getNextStop())){
                coords.put(odjazd.getNextStop(), odjazd.getCoordinates());
            }
                if (totalTimeAfter + this.getDistance(odjazd.getCoordinates(),this.targetCoordinates)*0.7< travelTime && timeDiff(time, odjazd.getLeaveTime()) >= 0) {

                    test.add(line);
                    if (!map.containsKey(odjazd.getNextStop())) {
                        toExplore.add(odjazd.getNextStop());
                        map.put(odjazd.getNextStop(), test);
                    } else {
                        Route current = map.get(odjazd.getNextStop());
                        if (current.time > test.time) {
                            toExplore.add(odjazd.getNextStop());
                            map.replace(odjazd.getNextStop(), test);
                            if (odjazd.getNextStop().equals(this.stopName)) this.travelTime = test.time;
                        }
                    }
                }
        }
        if(map.containsKey(this.stopName)){
            Route test = map.get(this.stopName);
            test.explored = true;
            this.travelTime = test.time;
            this.lines = test.lines;
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
