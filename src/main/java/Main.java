
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class Main {
    static Date date;


    public static void main(String[] args) {
        Logger log = (Logger) LoggerFactory.getLogger("org.onebusaway");
        log.setLevel(Level.OFF);
    /*        Scanner in=new Scanner(System.in);
            System.out.println("Podaj datę odjazdu w konwencji dd/mm/yyyy");
            String data=in.nextLine();
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            try {
                    date= simpleDateFormat.parse(data);
            } catch (ParseException e) {
                    e.printStackTrace();
            }

            System.out.println("Podaj godzine odjazdu w konwencji hh:mm");
            String txt=in.nextLine();
            int[] time = new int[2];
            try{
                    time[0]=Integer.parseInt(txt.substring(0,2));
                    time[1]=Integer.parseInt(txt.substring(3,5));}
            catch (StringIndexOutOfBoundsException sioobe){
                    System.out.println("Podaj poprawną godzinę");
                    System.exit(-1);
            }
            System.out.println("Podaj przystanek z którego wyjeżdzasz");
            String start = in.nextLine();
            System.out.println("Podaj przystanek docelowy");
            String target = in.nextLine();
            System.out.println(start+" - "+target+" at "+time[0]+":"+time[1]+":00"+" date "+date.toString());*/



        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("GTFS/stops.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Nie ma takiego pliku");
        }
        BufferedWriter bufferedWriter=null;
        BufferedWriter bufferedWriter1=null;
        try{
            bufferedWriter=new BufferedWriter(new FileWriter("Szybki.txt",true));
            bufferedWriter1=new BufferedWriter(new FileWriter("Tani.txt",true));

        } catch (IOException e) {
            e.printStackTrace();
        }
        String stop;
        String[] stops = new String[2146];
        Double [][] location=new Double[2146][2];
        try {
            bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 2146 ; i++) {
            try {
                stop=bufferedReader.readLine();
                stops[i]=stop.substring(stop.indexOf("\"")+1,stop.lastIndexOf("\""));
                location[i][0]=Double.parseDouble(stop.substring(stop.indexOf(",",stop.lastIndexOf("\""))+1,stop.lastIndexOf(",")));
                location[i][1]=Double.parseDouble(stop.substring(stop.lastIndexOf(",")+1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String data="11/01/2017";
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            date= simpleDateFormat.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        while(true) {
            Random random = new Random();
            int firstrandom,nextrandom;
            firstrandom=random.nextInt(2146);
            nextrandom=random.nextInt(2146);

            do{

                firstrandom=random.nextInt(2146);
                nextrandom=random.nextInt(2146);
            }  while((Math.abs(location[nextrandom][0]-location[firstrandom][0])>0.01D)||(Math.abs(location[nextrandom][1]-location[firstrandom][1])>0.01D)||stops[nextrandom].equals(stops[firstrandom]));
            String startstop = stops[firstrandom];
            String stopstop = stops[nextrandom];

            System.out.println("Searching connection " + startstop + " to " + stopstop);

            int[] time = new int[2];
            time[0] = random.nextInt(24) + 1;
            time[1] = random.nextInt(60) + 1;
            Route result;
            try {
                System.out.println("try fast");
                Connection fast=new Connection(startstop, stopstop, time);
                final Runnable stuffToDo = new Thread(() -> fast.search());

                final ExecutorService executor = Executors.newSingleThreadExecutor();
                final Future future = executor.submit(stuffToDo);
                executor.shutdown(); // This does not cancel the already-scheduled task.

                try {
                    future.get(5, TimeUnit.MINUTES);
                }
                catch (InterruptedException ie) {
                    /* Handle the interruption. Or ignore it. */
                }
                catch (ExecutionException ee) {
                    /* Handle the error. Or ignore it. */
                }
                catch (TimeoutException te) {
                    System.out.println("Przekroczony czas");
                }
                if (!executor.isTerminated())
                    executor.shutdownNow();

                result = fast.getRoute();
                if (result != null) {
                    System.out.println("make fast.txt");
                    bufferedWriter.newLine();
                    String r=startstop + ", " + stopstop + ", " + time[0] + ":" + time[1] + ", " + result.time + ", " + result.lines+"\n";
                   bufferedWriter.write(r);
                   bufferedWriter.flush();
                    try{
                        System.out.println("try convi");
                        Convinient convinient = new Convinient(startstop, stopstop, time, false);
                        final Runnable stuffToDo2 = new Thread(() -> convinient.search());

                        final ExecutorService executor2 = Executors.newSingleThreadExecutor();
                        final Future future2 = executor2.submit(stuffToDo2);
                        executor.shutdown(); // This does not cancel the already-scheduled task.

                        try {
                            future2.get(5, TimeUnit.MINUTES);
                        }
                        catch (InterruptedException ie) {
                            /* Handle the interruption. Or ignore it. */
                        }
                        catch (ExecutionException ee) {
                            /* Handle the error. Or ignore it. */
                        }
                        catch (TimeoutException te) {
                            System.out.println("Przekroczony czas");
                        }
                        if (!executor.isTerminated())
                            executor.shutdownNow();

                        Route result2 = convinient.getRoute();
                        if(result2!=null){
                            System.out.println("make convi.txt");
                            bufferedWriter1.newLine();
                            bufferedWriter1.write(startstop + ", " + stopstop + ", " + time[0] + ":" + time[1] + ", " + result.time + ", " + result.lines+"\n");
                            bufferedWriter1.flush();
                        }
                    } catch (IndexOutOfBoundsException io) {

                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
}

