import graph.Edge;
import graph.Node;
import graph.Path;


public class GraphTest {
	
	public void testRead () {
		//         e1       e2       e3
		//     n1 ====> n2 ====> n3 ====> n4
		//        -e1      -e2   ||  -e3
		//                       ||
		//                       n5
		
		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		
		Edge e1 = new Edge(1, n2.getId(), 1, 2);
		Edge e2 = new Edge(2, n3.getId(), 2, 3);
		Edge e3 = new Edge(3, n4.getId(), 3, 4);
		Edge e4 = new Edge(4, n5.getId(), 4, 5);
		
		n1.addEdge(e1);
		n2.addEdge(e2);
		n3.addEdge(e3);
		n3.addEdge(e4);
		
		Path p1 = new Path(); p1.extend(e1); n2.addSourcePath(p1);
		Path p2 = p1.clone(); p2.extend(e2); n3.addSourcePath(p2);
		Path p3 = p2.clone(); p3.extend(e3); n4.addSourcePath(p3);
		
		Path sp1 = new Path(); sp1.extend(new Edge(-1, n3.getId(), 13, 15)); n3.addSinkPath(sp1);
		Path sp2 = sp1.clone(); sp2.extend(new Edge(-2, n2.getId(), 4, 20)); n2.addSinkPath(sp2);
		
		System.out.println("Edges");
		System.out.println("e1: " + e1);
		System.out.println("e2: " + e2);
		System.out.println("e3: " + e3);
		
		System.out.println("Paths");
		System.out.println("p1: " + p1);
		System.out.println("p2: " + p2);
		System.out.println("p3: " + p3);
		System.out.println("sp1: " + sp1);
		System.out.println("sp2: " + sp2);
		
		
		System.out.println("Nodes");
		System.out.println("n1: " + n1);
		System.out.println("n2: " + n2);
		System.out.println("n3: " + n3);
		System.out.println("n4: " + n4);
		System.out.println("n5: " + n5);
	}
	
	public void testWrite() {
		
		String text = "(1	||(1,2,1,2))\n" +
					  "(2	((1,2,1,2))|((-1,3,13,15)>(-2,2,4,20))|(2,3,2,3))\n" +
				      "(3	((1,2,1,2)>(2,3,2,3))|((-1,3,13,15))|(3,4,3,4);(4,5,4,5))\n" +
					  "(4	((1,2,1,2)>(2,3,2,3)>(3,4,3,4))||)\n" +
				      "(5	||)\n";
		
		String[] lines = text.split("\n");
		for (String l : lines) {
			Node n = new Node(l);
			System.out.println(n);
		}
		
	}
	
	public static void main(String[] args) {

		GraphTest gt = new GraphTest();
		gt.testRead();
		System.out.println("-------------------------------------");
		gt.testWrite();
	}

}
