import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.serialization.GtfsReader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GTFSReader implements Serializable {
    private List<Odjazd> odjazdArray;
    private GtfsDaoImpl store = new GtfsDaoImpl();

    public GTFSReader(String gtfspath) throws IOException {
        GtfsReader reader = new GtfsReader();
        reader.setInputLocation(new File(gtfspath));
        reader.setEntityStore(store);
        reader.run();
    }

    public List<Odjazd> getData(String stopName, int[] time, Date date) {
        int newTime = ((time[0] * 60) + time[1]) * 60;
        Odjazd odjazd;
        odjazdArray=new ArrayList<>();
        name: for (StopTime stoptimes : store.getAllStopTimes()
        ) {
            if (stoptimes.getStop().getName().toLowerCase().equals(stopName.toLowerCase()) && stoptimes.getArrivalTime() >= newTime && stoptimes.getArrivalTime() < (newTime + 7200) && (stoptimes.getTrip().getServiceId().getId().equals(whatDate(date)))) {
                odjazd = new Odjazd();
                odjazd.setCoordinates(stoptimes.getStop().getLat(), stoptimes.getStop().getLon());
                odjazd.setLineNumber(stoptimes.getTrip().getRoute().getShortName());
                odjazd.setLeaveTime(odjazd.splitToComponentTimes(BigDecimal.valueOf(stoptimes.getArrivalTime())));
                nextStop(stoptimes, odjazd);

                if(odjazd.getNextStop()!=null) {
                    if (odjazdArray != null) {
                        for (Odjazd odj : odjazdArray) {

                            if (odj.getLineNumber().equals(odjazd.getLineNumber()) && odj.getNextStop().equals(odjazd.getNextStop())) {
                                continue name;
                            }

                        }
                    }

                    odjazdArray.add(odjazd);
                }
            }
        }

        return odjazdArray;
    }




    private void nextStop(StopTime stopTime, Odjazd odjazd) {
        for (StopTime stoptimes : store.getAllStopTimes()
        ) {
            if (stoptimes.getStopSequence() == (stopTime.getStopSequence() + 1) && stoptimes.getTrip().equals(stopTime.getTrip())) {

                odjazd.setNextStop(stoptimes.getStop().getName());
                odjazd.setTimeForNextStop((stoptimes.getArrivalTime() - stopTime.getArrivalTime()) / 60);
            }


        }
    }

    @SuppressWarnings("deprecation")
    private String whatDate(Date date) {
        if (date.getDay() == 0) return "4";
        else if (date.getDay() == 1 || date.getDay() == 2 || date.getDay() == 3 || date.getDay() == 4) return "6";
        else if (date.getDay() == 5) return "8";
        else return "3";
    }


    class Odjazd {
        private Double[] sourceCoordinates = new Double[2];
        private String lineNumber;
        private String nextStop;
        private int timeForNextStop;
        private int[] leaveTime;
        private Double[] coordinates = new Double[2];

        @Override
        public String toString() {
            return "Odjazd{" +
                    "lineNumber='" + lineNumber + '\'' +
                    ", nextStop='" + nextStop + '\'' +
                    ", timeForNextStop=" + timeForNextStop +
                    ", leaveTime=" + (leaveTime[0] + ":" + leaveTime[1] + ":00") +
                    ", coordinates=" + Arrays.toString(coordinates) +
                    '}';
        }

        public Double[] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(Double coordinates1, Double coordinates2) {
            coordinates[0] = coordinates1;
            coordinates[1] = coordinates2;
        }

        public String getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(String lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getNextStop() {
            return nextStop;
        }

        public void setNextStop(String nextStop) {
            this.nextStop = nextStop;
        }

        public int getTimeForNextStop() {
            return timeForNextStop;
        }

        public void setTimeForNextStop(int timeForNextStop) {
            this.timeForNextStop = timeForNextStop;
        }

        public int[] getLeaveTime() {
            return leaveTime;
        }

        public void setLeaveTime(int[] leaveTime) {
            this.leaveTime = leaveTime;
        }

        public int[] splitToComponentTimes(BigDecimal biggy) {
            long longVal = biggy.longValue();
            int hours = (int) longVal / 3600;
            int remainder = (int) longVal - hours * 3600;
            int mins = remainder / 60;
            remainder = remainder - mins * 60;
            int secs = remainder;

            int[] ints = {hours, mins, secs};
            return ints;
        }


        public void setSourceCoordinates(Double[] sourceCoordinates) {
            this.sourceCoordinates = sourceCoordinates;
        }

        public Double[] getSourceCoordinates() {
            return sourceCoordinates;
        }
    }
}
