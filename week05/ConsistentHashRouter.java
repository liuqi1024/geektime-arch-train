package com.cslp.combat.consistenhash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * To hash Node objects to a hash ring with a certain amount of virtual node.
 * Method routeNode will return a Node instance which the object key should be allocated to according to consistent hash algorithm
 */
public class ConsistentHashRouter {
    private final SortedMap<Long, VirtualNode> ring = new TreeMap<>();
    private final HashFunction hashFunction;

    public ConsistentHashRouter(Collection<Node> pNodes, int vNodeCount) {
        this(pNodes,vNodeCount, new HashFunction());
    }

    /**
     *
     * @param pNodes collections of physical nodes
     * @param vNodeCount amounts of virtual nodes
     * @param hashFunction hash Function to hash Node instances
     */
    public ConsistentHashRouter(Collection<Node> pNodes, int vNodeCount, HashFunction hashFunction) {
        if (hashFunction == null) {
            throw new NullPointerException("Hash Function is null");
        }
        this.hashFunction = hashFunction;
        if (pNodes != null) {
            for (Node pNode : pNodes) {
                addNode(pNode, vNodeCount);
            }
        }
    }

    /**
     * add physic node to the hash ring with some virtual nodes
     * @param pNode physical node needs added to hash ring
     * @param vNodeCount the number of virtual node of the physical node. Value should be greater than or equals to 0
     */
    public void addNode(Node pNode, int vNodeCount) {
        if (vNodeCount < 0) throw new IllegalArgumentException("illegal virtual node counts :" + vNodeCount);
        int existingReplicas = getExistingReplicas(pNode);
        for (int i = 0; i < vNodeCount; i++) {
            VirtualNode vNode = new VirtualNode(pNode, i + existingReplicas);
            ring.put(hashFunction.hash(vNode.getKey()), vNode);
        }
    }

    /**
     * remove the physical node from the hash ring
     * @param pNode
     */
    public void removeNode(Node pNode) {
        Iterator<Long> it = ring.keySet().iterator();
        while (it.hasNext()) {
            Long key = it.next();
            VirtualNode virtualNode = ring.get(key);
            if (virtualNode.isVirtualNodeOf(pNode)) {
                it.remove();
            }
        }
    }

    /**
     * with a specified key, route the nearest Node instance in the current hash ring
     * @param objectKey the object key to find a nearest Node
     * @return
     */
    public Node routeNode(String objectKey) {
        if (ring.isEmpty()) {
            return null;
        }
        Long hashVal = hashFunction.hash(objectKey);
        //得到大于该Hash值的所有Map
        SortedMap<Long,VirtualNode> tailMap = ring.tailMap(hashVal);
        //如果能找到最近的node就返回这个node，如果找不到就去这个ring上的第一个node
        Long nodeHashVal = !tailMap.isEmpty() ? tailMap.firstKey() : ring.firstKey();
        return ring.get(nodeHashVal).getPhysicalNode();
    }

    public int getExistingReplicas(Node pNode) {
        int replicas = 0;
        for (VirtualNode vNode : ring.values()) {
            if (vNode.isVirtualNodeOf(pNode)) {
                replicas++;
            }
        }
        return replicas;
    }
}
