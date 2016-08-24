package com.josue.micro.registry.client;

import com.josue.ssr.common.Instance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Josue Gontijo.
 */
public abstract class Strategy {
    AtomicInteger counter = new AtomicInteger();

    public static Strategy first() {
        return new Strategy() {
            @Override
            public Instance apply(List<Instance> configs) {
                return configs.get(0);
            }
        };
    }

    public static Strategy random() {
        return new Strategy() {
            @Override
            public Instance apply(List<Instance> configs) {
                int idx = ThreadLocalRandom.current().nextInt(0, configs.size() - 1);
                return configs.get(idx);
            }
        };
    }

    public static Strategy roundRobin() {
        return new Strategy() {
            @Override
            public Instance apply(List<Instance> configs) {
                int current = counter.getAndIncrement();
                if (current >= configs.size()) {
                    current = 0;
                    counter.set(0);
                }
                return configs.get(current);
            }
        };
    }

    abstract Instance apply(List<Instance> configs);

}
