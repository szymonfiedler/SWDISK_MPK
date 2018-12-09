import java.util.ArrayList;

public class Route {
    public ArrayList<Line> route;
    public String endStop;
    public int time;
    public boolean explored;

    public Route(){
        this.route = new ArrayList<Line>();
        this.time = 0;
        this.explored = true;
    }
    public void add(Line line){
        this.route.add(line);
        this.endStop = line.endStop;
        this.time += line.time;
    }
    public Route copy(){
        Route cp = new Route();
        cp.endStop = this.endStop;
        cp.time = time;
        cp.explored = false;
        cp.route = new ArrayList<>(this.route);
        return cp;
    }
    public void print(){
        for(Line l : route) l.print();
        System.out.println("Total travel time: "+this.time+" minutes.");
    }
}
