package com.cslp.combat.consistenhash;

public class VirtualNode {
    private Node physicalNode;
    private int replicaIndex;

    public VirtualNode(Node physicalNode, int replicaIndex) {
        this.replicaIndex = replicaIndex;
        this.physicalNode = physicalNode;
    }

    public String getKey() {
        return physicalNode.getKey() + "-" + replicaIndex;
    }

    public boolean isVirtualNodeOf(Node pNode) {
        return physicalNode.getKey().equals(pNode.getKey());
    }

    public Node getPhysicalNode() {
        return physicalNode;
    }
}
