package Model;

public class Task implements Comparable{
    private int ID;
    private int arrivalTime;
    private int waitingTime;
    private int serviceTime;
    public boolean sent;

    public Task(int ID, int serviceTime, int arrivalTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public Task() { }

    public int getID() {
        return ID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return this.serviceTime;
    }
    public void setWaitTime(int waitTime) {
        this.waitingTime = waitTime;
    }

    public int getWaitTime() {
        return waitingTime;
    }
    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    @Override
    public int compareTo(Object o) {
        Task c = new Task();
        c = (Task)o;

        if (this.getArrivalTime() < c.getArrivalTime())
            return -1;
        else if (this.getArrivalTime() > c.getArrivalTime())
            return 1;
        return 0;
    }
}