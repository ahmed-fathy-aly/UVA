/*
 * Dp + bitmask to try each stone for every position
 */

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
	private int n;
	private Cell[] stones;
	private Cell[] positions;
	private int[][] dp;

	private void read() throws IOException
	{
		// streams
		boolean file = false;
		if (file)
			in = new BufferedReader(new FileReader("input.txt"));
		else
			in = new BufferedReader(new InputStreamReader(System.in));
		ans = new StringBuilder();

		// parse input
		int cas = 0;
		while (true)
		{
			// param
			n = Integer.parseInt(in.readLine().trim());
			if (n == 0)
				break;

			// stones
			stones = new Cell[n];
			tok = new StringTokenizer(in.readLine().trim());
			for (int i = 0; i < stones.length; i++)
			{
				int r = Integer.parseInt(tok.nextToken()) - 1;
				int c = Integer.parseInt(tok.nextToken()) - 1;
				stones[i] = new Cell(r, c);
			}

			// answer
			cas++;
			ans.append("Board " + cas + ": " + solve() + " moves required.\n");
			ans.append("\n");
		}

		// output
		System.out.print(ans.toString());
	}

	private int solve()
	{
		// try all positions
		positions = new Cell[n];
		int min = Integer.MAX_VALUE;

		// all rows
		for (int r = 0; r < n; r++)
		{
			for (int i = 0; i < n; i++)
				positions[i] = new Cell(r, i);
			min = Math.min(min, findMin());
		}

		// all cols
		for (int c = 0; c < n; c++)
		{
			for (int i = 0; i < n; i++)
				positions[i] = new Cell(i, c);
			min = Math.min(min, findMin());
		}

		// the diag
		for (int i = 0; i < n; i++)
			positions[i] = new Cell(i, i);
		min = Math.min(min, findMin());
		for (int i = 0; i < n; i++)
			positions[i] = new Cell(i, n - 1 - i);
		min = Math.min(min, findMin());

		return min;
	}

	private int findMin()
	{
		// clear dp
		dp = new int[n][(1 << n)];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < (1 << n); j++)
				dp[i][j] = -1;
		return f(0, 0);
	}

	private int f(int idx, int mask)
	{
		// base case
		if (idx >= n)
			return 0;

		// already solved
		if (dp[idx][mask] != -1)
			return dp[idx][mask];

		// try putting all stones here
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < n; i++)
			if ((mask & (1 << i)) == 0)
			{
				int nextMask = (mask | (1 << i));
				int moves = positions[idx].dist(stones[i]) + f(idx + 1, nextMask);
				min = Math.min(min, moves);
			}

		// save
		dp[idx][mask] = min;
		return min;
	}

}

class Cell
{
	int r, c;

	public Cell(int r, int c)
	{
		this.r = r;
		this.c = c;
	}

	int dist(Cell other)
	{
		return Math.abs(r - other.r) + Math.abs(c - other.c);
	}
}