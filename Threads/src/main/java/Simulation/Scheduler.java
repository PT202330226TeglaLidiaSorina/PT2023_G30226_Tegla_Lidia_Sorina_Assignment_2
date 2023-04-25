package Simulation;

import Model.*;
import Simulation.ConcreteStrategyQueue;
import Simulation.ConcreteStrategyTime;

import java.util.ArrayList;

public class Scheduler{
    public SimulationManager sm;
    private ArrayList<Server> servers = new ArrayList<Server>();
    private Strategy strategy;

    public Scheduler (int maxNoServers, SelectionPolicy sp, SimulationManager sm) {
        for (int i = 0; i < maxNoServers; i++) {
            Server s = new Server(i, this);
            servers.add(s);
            s.start();
        }
        this.sm = sm;
        this.changeStrategy(sp);
    }

    public void changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE)
            strategy = new ConcreteStrategyQueue();
        if (policy == SelectionPolicy.SHORTEST_TIME)
            strategy = new ConcreteStrategyTime();
    }

    public void dispatchClient (Task c) {
        strategy.addTask(servers, c);
    }

    public ArrayList<Server> getServers() {
        return servers;
    }

}