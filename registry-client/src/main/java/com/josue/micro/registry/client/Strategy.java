package com.josue.micro.registry.client;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Josue Gontijo.
 */
public abstract class Strategy {
    abstract ServiceConfig apply(List<ServiceConfig> configs);

    public static Strategy first() {
        return new Strategy() {
            @Override
            ServiceConfig apply(List<ServiceConfig> configs) {
                return configs.get(0);
            }
        };
    }

    public static Strategy random() {
        return new Strategy() {
            @Override
            ServiceConfig apply(List<ServiceConfig> configs) {
                int idx = ThreadLocalRandom.current().nextInt(0, configs.size() - 1);
                return configs.get(idx);
            }
        };
    }

    AtomicInteger counter = new AtomicInteger();

    public static Strategy roundRobin() {
        return new Strategy() {
            @Override
            ServiceConfig apply(List<ServiceConfig> configs) {
                int current = counter.getAndIncrement();
                if (current >= configs.size()) {
                    current = 0;
                    counter.set(0);
                }
                return configs.get(current);
            }
        };
    }

}
