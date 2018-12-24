import java.util.ArrayList;

public class Route {
    public ArrayList<Line> route;
    public String endStop;
    public int time;
    public boolean explored;
    public int lines = 1;
    public Double distance = -1.0;

    public Route(){
        this.route = new ArrayList<Line>();
        this.time = 0;
        this.explored = true;
    }
    public void add(Line line){
        if(this.route.size()>0) {
            Line last = this.route.get(this.route.size() - 1);
            if (!last.lineNumber.equals(line.lineNumber)) {
                this.lines++;
            }
        }
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
        cp.lines = this.lines;
        return cp;
    }

    @Override
    public String toString() {
        String result = "";

        String last="";
        String line = "";

        for(Line l : route){
            if(!l.lineNumber.equals(line) && !last.equals("")){
                result += "Take line "+ line + " to " + last +"\n";
            }
            line = l.lineNumber;
            last = l.endStop;
        }
        result += "Take line "+ line + " to " + last +"\n";
        result += ("You will arrive in "+this.time+" minutes and switch the line " + (this.lines-1) + " times.");
        return result;
    }
}
