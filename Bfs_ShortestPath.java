package fordFulkerson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;

public class Bfs_ShortestPath extends GraphConstruct {

	LinkedList<Nodes> ShortPath = new LinkedList<Nodes>();
	HashMap<String, HashMap<String, Nodes>> InputGraph1 = new HashMap<String, HashMap<String, Nodes>>();

	public void doBfs(Nodes source, HashMap<String, HashMap<String, Nodes>> InputGraph, int destination1) {

		LinkedList<Nodes> visitNodes = new LinkedList<Nodes>();

		HashMap<Nodes, Nodes> parent_Path = new HashMap<Nodes, Nodes>();

		int destination = destination1;
		visited[Integer.parseInt(source.getName())] = true;

		visitNodes.addFirst(source);

		while (!visitNodes.isEmpty()) {

			Nodes top = visitNodes.remove();
			HashMap<String, Nodes> ConnectedNodes = InputGraph.get(top.getName());

			Iterator<String> it = ConnectedNodes.keySet().iterator();

			while (it.hasNext()) {
				Nodes n1 = (Nodes) ConnectedNodes.get(it.next());

				if ((visited[Integer.parseInt(n1.getName())] != true) && n1.getweight() > 0) {

					visited[Integer.parseInt(n1.getName())] = true;
					n1.setVisited(true);
					visitNodes.add(n1);
					parent_Path.put(n1, top);

				}
				if (Integer.parseInt(n1.getName()) == destination) {
 
					ShortPath.addFirst(n1);

					printPath(parent_Path, n1, source);
					String path1 = "";
					for (int i = 0; i < ShortPath.size(); i++) {
						path1 = path1.concat(ShortPath.get(i).getName() + " ");
					}
					System.out.println("The shortest path is :" + path1);

					System.out.println("\nThe time of execution of BFS is " + (endTime - startTime) + " millisecs");

					visitNodes.clear();

					break;
				}
			}

		}

	}

	public void printPath(HashMap<Nodes, Nodes> parent_path, Nodes destination, Nodes source) {

		Nodes path = parent_path.get(destination);
		// Nodes n2=(Nodes)path;
		ShortPath.addFirst(path);

		while (!parent_path.get(destination).equals(source)) {

			printPath(parent_path, path, source);
			break;
		}
		endTime = System.currentTimeMillis();

	}

}
