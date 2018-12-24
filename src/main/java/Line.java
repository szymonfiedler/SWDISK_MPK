public class Line {
    public String lineNumber;
    public String startStop;
    public String endStop;
    public int time;

    public Line(String number, String start, String stop, int time){
        this.lineNumber = number;
        this.startStop = start;
        this.endStop = stop;
        this.time = time;
    }
}
