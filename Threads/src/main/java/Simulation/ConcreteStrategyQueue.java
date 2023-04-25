package Simulation;

import Model.*;

import java.util.ArrayList;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addTask(ArrayList<Server> servers, Task t) {
        Server selectedServer = null;
        int leastNumberOfClients = Integer.MAX_VALUE;
        for(Server server: servers) {
            int currentNumberOfClients = server.getCurrentNumberOfTasks();
            if(currentNumberOfClients < leastNumberOfClients) {
                leastNumberOfClients = currentNumberOfClients;
                selectedServer = server;
            }
        }
        if(selectedServer != null) {
            selectedServer.addClient(t);
        }
    }
}
