package Simulation;

import Model.*;

import java.util.ArrayList;
public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addTask(ArrayList<Server> servers, Task c) {
        int minTime = Integer.MAX_VALUE;
        for (Server i: servers) {
            if (i.getProcessTime().intValue() < minTime)
                minTime = i.getProcessTime().intValue();
        }

        for (Server i: servers) {
            if (i.getProcessTime().intValue() == minTime) {
                i.addClient(c);

                return;
            }
        }
    }
}