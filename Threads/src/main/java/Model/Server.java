package Model;
import Simulation.*;
import GUI.*;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Thread{
    private ArrayBlockingQueue<Task> clients = new ArrayBlockingQueue<Task>(100);
    private int serverID;
    private AtomicInteger processTime;
    private Scheduler scheduler;
    public ArrayBlockingQueue<Task> getClients() {
        return clients;
    }
    public int getServerID() {
        return serverID;
    }
    public AtomicInteger getProcessTime() {
        return processTime;
    }

    public Server(int serverID, Scheduler s) {
        this.serverID = serverID;
        this.processTime = new AtomicInteger();
        this.scheduler = s;
    }
    public void addClient(Task c) {
        this.clients.add(c);
        this.getProcessTime().set(this.getProcessTime().intValue() + c.getServiceTime());
    }
    private Task dequeueClient() {
        Task client = null;
        if (!this.getClients().isEmpty()) {
            try {
                client = this.getClients().take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    private void processTask(Task task, ArrayList<Task> toBeRemoved) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.getProcessTime().set(this.getProcessTime().get() - task.getServiceTime());
        task.setServiceTime(0);
        toBeRemoved.add(task);
    }

    private void removeProcessedTasks(ArrayList<Task> toBeRemoved) {
        this.getClients().removeAll(toBeRemoved);
    }

    private void updateQueueDisplay() {
        synchronized (this) {
            scheduler.sm.view.queues.setText(scheduler.sm.toString());
        }
    }
    @Override
    public void run() {
        ArrayList<Task> toBeRemoved = new ArrayList<>();

        while (this.getProcessTime().get() > 0) {
            Task c = dequeueClient();
            if (c != null) {
                processTask(c, toBeRemoved);
            }
            removeProcessedTasks(toBeRemoved);

            updateQueueDisplay();
        }
    }
}