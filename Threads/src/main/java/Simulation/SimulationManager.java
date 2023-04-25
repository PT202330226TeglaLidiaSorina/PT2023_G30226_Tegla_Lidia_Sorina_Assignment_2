package Simulation;

import GUI.View;
import Model.*;
import Simulation.Scheduler;
import Simulation.SelectionPolicy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager extends Thread {
    public int maxProcessingTime;
    public int minProcessingTime;
    public int minArrivalTime;
    public int maxArrivalTime;
    public int numberOfServers;
    public int numberOfClients;
    public int timeLimit;
    public int max=Integer.MIN_VALUE;
    public int maxClients=Integer.MIN_VALUE;

    public double avgWaitingTime=0.0;
    public double avgServiceTime=0.0;

    public int waiting=0;
    public int service=0;
    public AtomicInteger simulationTime;
    private Scheduler scheduler;
    public View view;
    private ArrayList<Task> generatedClients;
    private ArrayBlockingQueue<Task> clients = new ArrayBlockingQueue<Task>(100);

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setGui(View view) {
        this.view = view;
    }

    public ArrayList<Task> getGeneratedClients() {
        return generatedClients;
    }


    public SimulationManager(int timeLimit, int maxProcessingTime, int minProcessingTime,
                             int minArrivalTime, int maxArrivalTime, int numberOfServers, int numberOfClients) {
        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;

        this.scheduler = new Scheduler(numberOfServers, SelectionPolicy.SHORTEST_TIME, this);
        this.generatedClients = generateNRandomClients();
        this.simulationTime = new AtomicInteger(0);
        for(int i = 0; i<numberOfServers;i++)
        {
            Server server = new Server(i,scheduler);
            Thread t = new Thread(server);
            t.start();
        }
    }

    public ArrayList<Task> generateNRandomClients() {
        ArrayList<Task> result = new ArrayList<Task>();
        Random rand = new Random();

        for (int i = 0; i < numberOfClients; i++) {
            int process = rand.nextInt(maxProcessingTime - minProcessingTime) + minProcessingTime;
            int arrival = rand.nextInt(maxArrivalTime - minArrivalTime) + minArrivalTime;
            Task c = new Task(i, process, arrival);
            result.add(c);
        }

        Collections.sort(result);
        return result;
    }
    public String clientsToString() {
        String result = new String();
        for (Task c : this.getGeneratedClients()) {
            if (c.sent == false)
                result = result + "\n(ID: " + c.getID() + ", Service time: " + c.getServiceTime() + ", Arrival Time: " + c.getArrivalTime() + ") ";
        }

        return result;
    }

    public String loggerToString() {
        String result = new String();
        for (Server s : scheduler.getServers()) {
            result = result + "\nServer " + s.getServerID() + ":";
            for (Task c : s.getClients())
                if (c.getServiceTime() != 0) {
                    result = result + "\n(ID: " + c.getID() + ", Service time: " + c.getServiceTime() + ", Arrival Time: " + c.getArrivalTime() + ") ";

                }
            result = result + "\n";
        }
        return result;
    }

    @Override
    public void run() {
        ArrayList<Task> toBeRemoved = new ArrayList<>();
        int[] frequency = new int[5000];
        int currentTime = 0;
        int initialSize = this.getGeneratedClients().size();
        this.scheduler = new Scheduler(numberOfServers, SelectionPolicy.SHORTEST_TIME, this);
        while (currentTime < timeLimit) {
            handleNewClients(currentTime, toBeRemoved, frequency);
            decrementServiceTime(currentTime);
            updateGUI(currentTime, initialSize);
            avgWaitingTime = (double)(waiting) / initialSize;
            avgServiceTime = (double)(service) / initialSize;
            updateLogFile(currentTime);
            sleepForOneSecond();
            peakHour(currentTime);
            currentTime = simulationTime.incrementAndGet();
        }

        showStatistics(frequency,avgWaitingTime,avgServiceTime);
        // updateLogFile(initialSize, waiting, service, max);
    }
    public String toString() {
        String result;
        result = "Clients: " + numberOfClients + " Servers: " + numberOfServers + " Simulation time: " + timeLimit
                + "\nMinimum arrival time: " + minArrivalTime + " Maximum arrival time: " + maxArrivalTime
                + "\nMinimum service time: " + minProcessingTime + " Maximum service time: " + maxProcessingTime + "\n\n";
        synchronized (this.generatedClients) {
            result=result+"\nWaiting clients: " + this.clientsToString() ;
            for (Server s : scheduler.getServers()) {
                result = result + "\nServer " + s.getServerID() + ":";
                for (Task c : s.getClients())
                    if (c.getServiceTime() != 0)
                        result = result + "\n(ID: " + c.getID() + ", Service time: " + c.getServiceTime() + ", Arrival Time: " + c.getArrivalTime() + ") ";
            }
        }
        return result;
    }
    private void handleNewClients(int currentTime, ArrayList<Task> toBeRemoved, int[] frequency) {
        for (Task c : this.getGeneratedClients()) {
            if (c.getArrivalTime() == currentTime) {
                frequency[c.getArrivalTime()] += 1;
                service += c.getServiceTime();
                waiting += c.getArrivalTime();

                this.getScheduler().dispatchClient(c);
                c.sent = true;
                toBeRemoved.add(c);
            }
        }

        this.generatedClients.removeAll(toBeRemoved);
    }

    private void decrementServiceTime(int currentTime) {
        for (Server s : scheduler.getServers()) {
            clients=s.getClients();
            Task firstTask=clients.peek();

            if ((firstTask != null ? firstTask.getServiceTime() : 0) > 0 && firstTask.getArrivalTime() != currentTime && firstTask.getServiceTime() != 0) { // decrement service time
                firstTask.setServiceTime(firstTask.getServiceTime() - 1);
            }

            if(firstTask != null && firstTask.getServiceTime()==0) clients.remove();
        }
    }

    private void updateGUI(int currentTime, int initialSize) {
        synchronized (this) {
            view.getTimeLabel().setText(Integer.toString(currentTime));
            view.queues.setText(this.toString());
        }
    }

    private void updateLogFile(int currentTime) {
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("log.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            myWriter.write("Time: " + currentTime +
                    "\nWaiting clients: " + this.clientsToString() + this.loggerToString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void peakHour(int currentTime){
        for(Server s:scheduler.getServers()){
            if(s.getCurrentNumberOfTasks()>maxClients)
            {
                max=currentTime;
                maxClients=s.getCurrentNumberOfTasks();
            }
        }
    }

    private void sleepForOneSecond() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showStatistics(int[] frequency,double avgWaitingTime, double avgServiceTime) {
        synchronized (this) {
            view.queues.setText(this + "\nPeak Time: " + max
                    + "\nAverage waiting time: " + avgWaitingTime+"\nAverage service time: " + avgServiceTime + "\n" );
            FileWriter myWriter = null;
            try {
                myWriter = new FileWriter("log.txt", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                myWriter.write("\nPeak Time: " + max
                        + "\nAverage waiting time: " + avgWaitingTime + "\nAverage service time: " + avgServiceTime + "\n" );
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}