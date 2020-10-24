package com.cslp.combat.consistenhash;

/**
 * 物理节点
 */
public class Node {
  private  String ip;
  private  int port;

  public Node(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public String getKey() {
    return  getIp() + "-" + getPort();
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }


}
