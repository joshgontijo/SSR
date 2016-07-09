package com.josue.micro.service.registry.link;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Josue on 09/07/2016.
 */
@ApplicationScoped
public class LinkControl {

    private static final Map<Node, Set<Node>> dependencies = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        //testing only
        addLink("AAA", "BBB");
        addLink("AAA", "CCC");
        addLink("BBB", "CCC");
    }

    public Map<Node, Set<Node>> getNodes(String filter){
        HashMap<Node, Set<Node>> nodes = new HashMap<>(dependencies);
        return nodes.entrySet().stream()
                .filter(e -> filter == null || e.getKey().getName().equals(filter))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    public Set<Node> addLink(String source, String target) {
        Node sourceNode = new Node(source);
        Node targetNode = new Node(target);
        if (!dependencies.containsKey(sourceNode)) {
            dependencies.put(sourceNode, new HashSet<>());
        }
        if (!dependencies.containsKey(targetNode)) {
            dependencies.put(targetNode, new HashSet<>());
        }
        Node targetNodeFromMap = getNode(target);
        //retrieve the node from the map key, so it gets propagated
        dependencies.get(sourceNode).add(targetNodeFromMap);
        return dependencies.get(sourceNode);
    }

    public Node disconnectNode(String serviceName) {
        List<Node> collect = dependencies.keySet().stream()
                .filter(n -> n.getName().equals(serviceName))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            //handle
        }
        collect.get(0).setConnected(false);
        return collect.get(0);
    }

    public void removeNode(String serviceName) {
        dependencies.remove(new Node(serviceName));
    }

    private Node getNode(String serviceName){
        List<Node> collect = dependencies.keySet().stream()
                .filter(n -> n.getName().equals(serviceName))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            //handle
        }
        return collect.get(0);
    }

}
