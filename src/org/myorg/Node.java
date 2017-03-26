package org.myorg;

import java.util.ArrayList;

public class Node {

	private int nodeId;
	private double nodeRank = 1.0;
	private boolean isNode;
	private boolean danglingNode;
	private ArrayList<Integer> adjacencyList;

	public Node(String nodevalues) {
		String[] splited = nodevalues.toString().split(",");
		for (int i = 0; i < splited.length; i++) {
			System.out.print(splited[i]+":"+i +" ");
		}
		System.out.println();
		
		if (splited[0].equals("fromNode")) {

			this.nodeId = Integer.parseInt(splited[1]);
			if (splited.length > 3) {
				ArrayList<Integer> list = new ArrayList<Integer>();
				for (int i = 3; i < splited.length; i++) {
				
					
					list.add(Integer.parseInt(splited[i]));
				}
				System.out.println();
				this.nodeRank =  Double.parseDouble(splited[2]);
				this.adjacencyList = list;
				this.danglingNode = false;
				this.isNode = true;
			} 
			else {
				this.nodeRank =  Double.parseDouble(splited[2]);
				this.adjacencyList = new ArrayList<Integer>();
				this.danglingNode = true;
				this.isNode = true;
			}

		} else {

			this.isNode = false;
			this.nodeRank = Double.parseDouble(splited[1]);

		}
	}

	public int getNodeId() {
		return nodeId;
	}

	public boolean isNode() {
		return isNode;
	}

	public void setNode(boolean isNode) {
		this.isNode = isNode;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public double getNodeRank() {
		return nodeRank;
	}

	public void setNodeRank(double nodeRank) {
		this.nodeRank = nodeRank;
	}

	public ArrayList<Integer> getAdjacencyList() {
		return adjacencyList;
	}

	public void setAdjacencyList(ArrayList<Integer> adjacencyList) {
		this.adjacencyList = adjacencyList;
	}

	@Override
	public String toString() {
		String NodeInfo = "";
		if (isNode) {
			if (danglingNode) {
				return "fromNode," + nodeId +","+nodeRank;
			} else {

				for (int toNode : adjacencyList) {
					NodeInfo = NodeInfo + toNode + ",";
				}
				NodeInfo = NodeInfo.substring(0, NodeInfo.length() - 1);
				return "fromNode," + nodeId + ","+ nodeRank+ "," + NodeInfo;
			}
		} else {
			return "toNode," + nodeRank;
		}

	}

}
