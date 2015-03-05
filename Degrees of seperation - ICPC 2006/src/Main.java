import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class Main
{

	public static void main(String[] args) throws IOException
	{
		Main main = new Main();
		main.read();
	}

	/* IO */
	private StringBuilder ans;
	private BufferedReader in;
	private StringTokenizer tok;

	/* fields */
	private int nNodes;
	private int nEdges;
	private int[][] g;
	private int nextId;
	private HashMap<String, Integer> nameToId;

	private void read() throws IOException
	{
		// streams
		boolean file = false;
		if (file)
			in = new BufferedReader(new FileReader("input.txt"));
		else
			in = new BufferedReader(new InputStreamReader(System.in));
		ans = new StringBuilder();

		// read all input
		StringBuilder allInput = new StringBuilder();
		while (true)
		{
			String str = in.readLine();
			if (str == null)
				break;
			else
				allInput.append(" " + str.trim());
		}
		tok = new StringTokenizer(allInput.toString());

		// parse input
		int cas = 0;
		while (true)
		{
			// read params
			nNodes = Integer.parseInt(tok.nextToken());
			nEdges = Integer.parseInt(tok.nextToken());
			if (nNodes == 0 && nEdges == 0)
				break;
			g = new int[nNodes][nNodes];
			for (int i = 0; i < nNodes; i++)
				for (int j = 0; j < nNodes; j++)
					if (i == j)
						g[i][j] = 0;
					else
						g[i][j] = 100000;

			// make edges
			nextId = 0;
			nameToId = new HashMap<String, Integer>();
			int remEdges = nEdges;
			while (remEdges > 0)
			{
				String x = tok.nextToken();
				String y = tok.nextToken();
				addEdge(x, y);
				remEdges -= 1;
			}

			// solve
			cas++;
			ans.append("Network " + cas + ": " + solve() + "\n");
			ans.append("\n");
		}

		// output
		System.out.print(ans.toString());
	}

	private String solve()
	{
		// find all pair shortest path
		for (int k = 0; k < nNodes; k++)
			for (int i = 0; i < nNodes; i++)
				for (int j = 0; j < nNodes; j++)
							g[i][j] = Math.min(g[i][j], g[i][k] + g[k][j]);
		
		// find max shortest path
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < nNodes; i++)
			for (int j = 0; j < nNodes; j++)
				if (g[i][j] > nNodes - 1)
					return "DISCONNECTED";
				else
					max = Math.max(max, g[i][j]);
		return "" + max;
	}

	private void addEdge(String x, String y)
	{
		int id1 = getId(x);
		int id2 = getId(y);
		g[id1][id2] = 1;
		g[id2][id1] = 1;
	}

	private int getId(String x)
	{
		if (nameToId.containsKey(x) == false)
			nameToId.put(x, nextId++);
		return nameToId.get(x);
	}

}
