package fordFulkerson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


import java.util.Scanner;

public class ImageSegmentation extends GraphConstruct {


	private static HashMap<String, HashMap<String, Nodes>> InputGraph1;
	static int row_max;
	static int colMax;
	static int MaxPixel_Value;
	static int[][] Graph_Image = null;
	static int scale_value;

	private static HashMap<String, Nodes> Reverse_edges, f, sinkflow;
	private static HashMap<String, HashMap<String, Nodes>> tempGraph = new HashMap<String, HashMap<String, Nodes>>();;

	
	private static String foreground = null;
	private static String background = null;

	

	private static String sourceId;
	private static int MaxFlow = 0;
	private static int MinCapacity;
	private static String sinkID;
	private static LinkedList<Nodes> ShortPath = new LinkedList<Nodes>();

	public static void main(String[] args) {

		InputGraph1 = new HashMap<String, HashMap<String, Nodes>>();
		foreground = args[2];
		background = args[3];

		File f = new File(args[1]);
		try {
			Scanner input = new Scanner(f);
			input.nextLine();
			input.nextLine();
			row_max = input.nextInt();
			colMax = input.nextInt();
			MaxPixel_Value = input.nextInt();
			Graph_Image = new int[row_max][colMax];

			for (int y = 0; y < colMax; ++y) {
				for (int x = 0; x < row_max; ++x) {
					Graph_Image[x][y] = input.nextInt();

				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(calculatescale_value());
		calculatescale_value();
		addPaths();
		addSuperNodes();
		setVisitedNodes(InputGraph1);

		doBfs(new Nodes(sourceId), InputGraph1);
		write();
	}

	private static void write() {
		// TODO Auto-generated method stub
		try {
			File writer = new File(foreground);
			FileWriter writer2 = new FileWriter(writer);
			PrintWriter printWriter = new PrintWriter(writer2);

			printWriter.write("P2\n");
			printWriter.write("#Created by Image Segmentation\n");
			printWriter.write(row_max + " " + colMax + "\n");
			printWriter.write(MaxPixel_Value + "\n");
			

			for (int y = 0; y < colMax; y++) {
				for (int x = 0; x < row_max; x++)

				{
					int nodeID = (row_max * y) + x + 1;

				/*	if (nodeID == 10) {
						System.out.println("dfgh");
					}*/

					HashMap<String, Nodes> m_ConnectedEdges = tempGraph.get(sourceId);
					if (m_ConnectedEdges.containsKey(Integer.toString(nodeID)))
						printWriter.write(MaxPixel_Value + " ");
					else
						printWriter.write(Graph_Image[x][y] + " ");

				}

			}

			File writer3 = new File(background);
			FileWriter writer4 = new FileWriter(writer3);
			PrintWriter printWriter2 = new PrintWriter(writer4);

			printWriter2.write("P2\n");
			printWriter2.write("#Created by Image Segmentation\n");
			printWriter2.write(row_max + " " + colMax + "\n");
			printWriter2.write(MaxPixel_Value + "\n");
			

			for (int y = 0; y < colMax; y++) {
				for (int x = 0; x < row_max; x++)

				{
					int nodeID = (row_max * y) + x + 1;

					if (nodeID == 10) {
						//System.out.println("dfgh");
					}

					HashMap<String, Nodes> m_ConnectedEdges = tempGraph.get(sourceId);
					if (m_ConnectedEdges.containsKey(Integer.toString(nodeID)))
						printWriter2.write(Graph_Image[x][y] + " ");
					else
						printWriter2.write(0 + " ");

				}

			}

			printWriter2.close();
			printWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showgraph() {

		for (String key1 : InputGraph1.keySet()) {

			HashMap<String, Nodes> m_ConnectedEdges = InputGraph1.get(key1);
			Iterator<String> it = m_ConnectedEdges.keySet().iterator();

			while (it.hasNext()) {
				Nodes n1 = (Nodes) m_ConnectedEdges.get(it.next());

				System.out.println("before residual..For node" + key1 + ": " + n1.getName() + " " + n1.getweight());
			}

		}
	}

	public static int calculatescale_value() {

		long sumOfNodes = 0;
		for (int x = 0; x < row_max; ++x)
			for (int y = 0; y < colMax; ++y)
				sumOfNodes += Graph_Image[x][y];

		scale_value = (int) Math.abs(MaxPixel_Value - (sumOfNodes / (row_max * colMax)));
		return scale_value;
	}

	public static void addPaths() {
		for (int x = 0; x < row_max; ++x) {
			for (int y = 0; y < colMax; ++y) {
				int currentID = (row_max * y) + x;

				HashMap<String, Nodes> ConnectedEdges = new HashMap<String, Nodes>();

				if (x > 0) 
				{
					int weight = Math.abs(MaxPixel_Value - Math.abs(Graph_Image[x - 1][y] - Graph_Image[x][y]));
					if (weight > scale_value) {

						int id = (row_max * y) + (x - 1);

						ConnectedEdges.put(Integer.toString(id), new Nodes(Integer.toString(id), weight));

					}
				}
				if (x < (row_max - 1)) 
				{
					int weight = Math.abs(MaxPixel_Value - Math.abs(Graph_Image[x + 1][y] - Graph_Image[x][y]));
					if (weight > scale_value) {

						int id = (row_max * y) + (x + 1);

						ConnectedEdges.put(Integer.toString(id), new Nodes(Integer.toString(id), weight));

					}	
				}
				if (y > 0) 
				{
					int weight = Math.abs(MaxPixel_Value - Math.abs(Graph_Image[x][y - 1] - Graph_Image[x][y]));
					if (weight > scale_value) {

						int id = (row_max * (y - 1) + x);

						ConnectedEdges.put(Integer.toString(id), new Nodes(Integer.toString(id), weight));

					}
				}
				if (y < (colMax - 1)) 
				{
					int weight = Math.abs(MaxPixel_Value - Math.abs(Graph_Image[x][y + 1] - Graph_Image[x][y]));
					if (weight > scale_value) {

						int id = (row_max * (y + 1) + x);

						ConnectedEdges.put(Integer.toString(id), new Nodes(Integer.toString(id), weight));

					}
				}
				InputGraph1.put(Integer.toString(currentID), ConnectedEdges);
			}
		}

	}

	public static void doBfs(Nodes source, HashMap<String, HashMap<String, Nodes>> InputGraph) {
		// TODO Auto-generated method stub
		boolean isDestinationAvailable = false;

		InputGraph1 = InputGraph;

		LinkedList<Nodes> Queue = new LinkedList<Nodes>();
		HashMap<String, Nodes> TraversalPath = new HashMap<String, Nodes>();
		HashMap<Nodes, Nodes> parent_Path = new HashMap<Nodes, Nodes>();

		int destination = Integer.parseInt(sinkID);
		visited[Integer.parseInt(source.getName())] = true;

		Queue.addFirst(source);
		TraversalPath.put(source.getName(), source);

		while (!Queue.isEmpty()) {

			Nodes top = Queue.remove();
			HashMap<String, Nodes> ConnectedNodes = InputGraph.get(top.getName());

			Iterator<String> it = ConnectedNodes.keySet().iterator();

			while (it.hasNext()) {
				Nodes n1 = (Nodes) ConnectedNodes.get(it.next());

				if ((visited[Integer.parseInt(n1.getName())] != true) && n1.getweight() > 0) {

					visited[Integer.parseInt(n1.getName())] = true;
					n1.setVisited(true);
					Queue.add(n1);
					parent_Path.put(n1, top);
					TraversalPath.put(n1.getName(), n1);
				}
				if (Integer.parseInt(n1.getName()) == destination) {
					isDestinationAvailable = true;
					ShortPath.addFirst(n1);

					System.out.println("destination is" + n1.getName());
					printPath(parent_Path, n1, source);

					System.out.println("shortest path size is" + ShortPath.size());

					System.out.println("\nThe time of execution of BFS is " + (endTime - startTime) + " millisecs");

					Queue.clear();
					

					break;
				}
			}

		}

		if (isDestinationAvailable)

		{
			MinCapacity = getminCapacity(ShortPath);
			MaxFlow = residualNetwork(MinCapacity, ShortPath);
			System.out.println("maxflow vaue is " + MaxFlow);

			setVisitedNodes(tempGraph);
			ShortPath.clear();
			doBfs(source, tempGraph);
			// isDestinationAvailable=false;

		}

	}

	private static int residualNetwork(int c, LinkedList<Nodes> shortPath2) {
		// TODO Auto-generated method stub

		MaxFlow = MaxFlow + c;
		tempGraph = InputGraph1;

		sinkflow = new HashMap<String, Nodes>();
		for (int i = 0; i <= shortPath2.size() - 2; i++) {

			Nodes one = ShortPath.get(i);
			Nodes two = ShortPath.get(i + 1);
			f = InputGraph1.get(one.getName());

			Iterator<String> it = f.keySet().iterator();
			while (it.hasNext()) {
				Nodes sec = f.get(it.next());
				int cc = sec.getweight();
				if (sec.getName().equals(two.getName())) {
					if (cc == c) {

						f.remove(sec.getName());

					} else {
						int updcap = sec.getweight() - c;
						sec.setweight(updcap);
					}
					Reverse_edges = InputGraph1.get(sec.getName());

					if (Reverse_edges != null) {
						Iterator<String> it2 = Reverse_edges.keySet().iterator();

						int flag = -1;
						while (it2.hasNext()) {
							if (it2.next().equals(one.getName())) {
								flag = 1;

							}
						}

						if (flag == -1) {
							Nodes n = new Nodes(one.getName(), c);
							Reverse_edges.put(n.getName(), n);
							flag = -1;
						}
					} else {

						Nodes n1 = new Nodes(one.getName(), c);
						sinkflow.put(n1.getName(), n1);

					}

					tempGraph.put(one.getName(), f);
					if (Reverse_edges != null)
						tempGraph.put(two.getName(), Reverse_edges);

					break;
				}
			}
		}

		return MaxFlow;

	}

	public static void jusPrint() {
		System.out.println("printing---------------------------------------------------------------------------------");
		for (String key1 : tempGraph.keySet()) {
			HashMap<String, Nodes> m_ConnectedEdges = tempGraph.get(key1);

			Iterator<String> it = m_ConnectedEdges.keySet().iterator();
			if (Integer.parseInt(key1) == Integer.parseInt("230")) {

				while (it.hasNext()) {
					Nodes n1 = (Nodes) m_ConnectedEdges.get(it.next());
					if (n1.getName().equals("402"))
						System.out.println(
								"before residual For node" + key1 + ": " + n1.getName() + " " + n1.getweight());
				}
			}
		}
		HashMap<String, Nodes> m_ConnectedEdges = tempGraph.get("230");
		System.out.println("size" + m_ConnectedEdges.size());
		// System.out.println("---------------------------------------------------------------------------------");

	}

	public static int getminCapacity(LinkedList<Nodes> shortPath2) {
		// TODO Auto-generated method stub
		int min = 0;
		for (Nodes s : shortPath2) {
			if (min == 0)
				min = s.getweight();
			if (min > s.getweight())
				min = s.getweight();
		}
		System.out.println("minimum capcity in shortest path " + min);
		return min;
	}

	public static void printPath(HashMap<Nodes, Nodes> parent_path, Nodes destination, Nodes source) {

		Nodes path = parent_path.get(destination);
		// Nodes n2=(Nodes)path;
		ShortPath.addFirst(path);

		while (!parent_path.get(destination).equals(source)) {

			printPath(parent_path, path, source);
			break;
		}
		endTime = System.currentTimeMillis();

	}

	public static void addSuperNodes() {

		sourceId = Integer.toString(InputGraph1.size() + 1);
		sinkID = Integer.toString(InputGraph1.size() + 2);
		//System.out.println("source" + sourceId + "sink" + sinkID);
		HashMap<String, Nodes> ConnectedEdges1 = new HashMap<String, Nodes>();

		System.out.println("");
		for (int x = 0; x < row_max; ++x) {
			for (int y = 0; y < colMax; ++y) {
				if (Math.abs(MaxPixel_Value - Graph_Image[x][y]) > scale_value) {

					int id = (row_max * y) + x;
					int weight = Math.abs(MaxPixel_Value - Graph_Image[x][y]);

					ConnectedEdges1.put(Integer.toString(id), new Nodes(Integer.toString(id), weight));
					InputGraph1.put(sourceId, ConnectedEdges1);

				}

				if (Graph_Image[x][y] > scale_value)

				{
				

					int weight = Graph_Image[x][y];
					int id = (row_max * y) + x;
					if (InputGraph1.containsKey(Integer.toString(id))) {
						HashMap<String, Nodes> m_ConnectedEdges = InputGraph1.get(Integer.toString(id));

						m_ConnectedEdges.put(sinkID, new Nodes(sinkID, weight));

					}

				}

			}

			//InputGraph1.put(sourceId, ConnectedEdges1);

		}

	}

}
