import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Convinient {

    public String startName;
    public String stopName;
    public int[] startAt;

    //best found scores
    public int travelTime;
    public int lines = 2000000;

    private GTFSReader gtfsReader;
    private Double[] targetCoordinates;
    private ArrayList<Route> routes;
    private HashMap<String, Double> distances = new HashMap<String, Double>();
    private HashMap<String, ArrayList<String>> visited = new HashMap<>();

    private boolean deepSearch;

    public Convinient(String startName, String stopName, int[] startAt, boolean deepSearch) {
        this.startName = startName;
        this.stopName = stopName;
        this.startAt = startAt;
        this.travelTime = 123456789;
        this.deepSearch = deepSearch;
        Route start = new Route();
        start.endStop = startName;
        this.routes = new ArrayList<Route>();
        try {
            gtfsReader = new GTFSReader("GTFS");
        } catch (IOException ioe) {
            System.out.println("Nie znaleziono GTFS");
        }
        this.targetCoordinates = gtfsReader.getData(this.stopName, this.startAt, Main.date).get(0).getCoordinates();

        List<GTFSReader.Odjazd> odjazds = gtfsReader.getData(startName, startAt, Main.date);
        distances.put(this.startName, getDistance(odjazds.get(0).getCoordinates(), this.targetCoordinates));
        start.endStop = startName;
        start.lines = 1;
        start.explored = false;
        start.time = 0;
        this.routes.add(start);
    }


    public void search() {
        Route best = this.pickBest();
        while (best != null) {
            this.explore(best);
            if (!best.endStop.equals(this.stopName) && this.routes.size() > 1) {
                this.routes.remove(this.routes.indexOf(best));
            }
            best = this.pickBest();
        }


    }

    private void explore(Route best) {
        best.explored = true;
        String from = best.endStop;
        int[] time = {0, 0};
        time[0] = this.startAt[0];
        time[1] = this.startAt[1];
        time[0] += Math.floorDiv(best.time, 60);
        time[1] += best.time;
        if (time[1] > 60) {
            time[1] = time[1] % 60;
            time[0]++;
        }
        if (timeDiff(this.startAt, time) > this.travelTime && best.lines >= this.lines) {
            return;
        }

        Route convinientFound = null;

        //System.out.println(from+" at "+time[0]+":"+time[1]+ " best so far - time: "+this.travelTime+" lines :"+this.lines+" branches left to explore: "+this.routes.size());
        List<GTFSReader.Odjazd> odjazds = gtfsReader.getData(from, time, Main.date);

        for (GTFSReader.Odjazd odjazd : odjazds) {
            if (!odjazd.getNextStop().equals(from)) {
                Route test = best.copy();
                int timeToNextStop = timeDiff(time, odjazd.getLeaveTime()) + odjazd.getTimeForNextStop();
                Line line = new Line(odjazd.getLineNumber(), from, odjazd.getNextStop(), timeToNextStop);
                if (!distances.containsKey(odjazd.getNextStop())) {
                    distances.put(odjazd.getNextStop(), getDistance(odjazd.getCoordinates(), this.targetCoordinates));
                }
                boolean loop = false;
                if(test.contains(odjazd.getNextStop(), odjazd.getLineNumber()) || odjazd.getNextStop().equals(this.startName)){
                    loop = true;
                }
                else{
                    test.add(line);
                }

                int totalTimeAfter = timeDiff(this.startAt, odjazd.getLeaveTime()) + odjazd.getTimeForNextStop();
                if ((test.lines <= this.lines) && (totalTimeAfter + this.distances.get(test.endStop) * 0.7 < travelTime) && (timeDiff(time, odjazd.getLeaveTime()) >= 0) && !loop) {
                    this.routes.add(test);
                    if (test.endStop.equals(this.stopName) && ((test.lines < this.lines) || (test.time < this.travelTime))) {
                        test.explored = true;
                        this.lines = test.lines;
                        this.travelTime = test.time;
                        if (!this.deepSearch) {
                            convinientFound = test;
                        }
                    }
                }
            }
        }

        if(convinientFound != null){
            this.routes.clear();
            this.routes.add(convinientFound);
            return;
        }
    }

    private Route pickBest() {
        Route best = null;
        Double bestScore = 200000000.0;
        int minLines = this.lines;

        for (Route r : routes) {
            if (!r.explored) {
                Double distance = this.distances.get(r.endStop);
                r.distance = distance;
                Double score = r.time + distance;
                if (r.lines < minLines || (r.lines == minLines && score < bestScore)) {
                    best = r;
                    bestScore = score;
                    minLines = r.lines;
                }
            }
        }

        return best;
    }

    private static Double getDistance(Double[] start, Double[] stop) {
        return Math.sqrt(Math.pow(start[0] - stop[0], 2) + Math.pow(start[1] - stop[1], 2)) * 333;
    }

    private int timeDiff(int[] t2, int[] t1) {
        int time = (t1[0] - t2[0]) * 60 + t1[1] - t2[1];
        return time;
    }

    public Route getRoute() {

        Route best = null;
        int bestScore = 200000000;
        int minLines = this.lines;

        for (Route r : routes) {
            if (r.endStop.equals(this.stopName) && r.lines <= minLines && r.time < bestScore) {
                best = r;
                bestScore = r.time;
                minLines = r.lines;
            }
        }
        return best;
    }

}
