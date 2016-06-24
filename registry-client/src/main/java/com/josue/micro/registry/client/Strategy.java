package com.josue.micro.registry.client;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Josue Gontijo.
 */
public interface Strategy {
    ServiceConfig apply(List<ServiceConfig> configs);

    Strategy RANDOM = new Strategy() {
        @Override
        public ServiceConfig apply(List<ServiceConfig> configs) {
            int idx = ThreadLocalRandom.current().nextInt(0, configs.size() - 1);
            return configs.get(idx);
        }
    };

    AtomicInteger counter = new AtomicInteger();
    Strategy ROUND_ROBIN = new Strategy() {
        @Override
        public ServiceConfig apply(List<ServiceConfig> configs) {
            int current = counter.getAndIncrement();
            if (current >= configs.size()) {
                current = 0;
                counter.set(0);
            }
            return configs.get(current);
        }
    };

}
