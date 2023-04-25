package Simulation;

import Model.*;

import java.util.ArrayList;
public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addTask(ArrayList<Server> servers, Task t) {
        int smallestWaitTime = Integer.MAX_VALUE;
        Server selectedServer = null;
        for(Server server: servers) {
            int currentServerWaitingPeriod = server.getWaitingPeriod().intValue();

            if(currentServerWaitingPeriod < smallestWaitTime) {
                selectedServer = server;
                smallestWaitTime = currentServerWaitingPeriod;
            }
        }
        if(selectedServer != null) {
            selectedServer.addClient(t);
        }
    }
}