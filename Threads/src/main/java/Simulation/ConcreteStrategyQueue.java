package Simulation;

import Model.*;

import java.util.ArrayList;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addTask(ArrayList<Server> servers, Task c) {
        int minSize = Integer.MAX_VALUE;
        for (Server i: servers) {
            if (i.getClients().size() < minSize)
                minSize = i.getProcessTime().intValue();
        }

        for (Server i: servers) {
            if (i.getClients().size() == minSize) {
                i.addClient(c);
                return;
            }
        }
    }
}