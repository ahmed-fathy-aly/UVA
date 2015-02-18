import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.StringTokenizer;

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
	private int nDays;
	private Day[] days;
	private int nOld;
	private int nNew;
	private int[] oldL;
	private int[] oldR;
	private int[] newL;
	private int[] newR;

	private void read() throws IOException
	{

		// streams
		boolean file = false;
		if (file)
			in = new BufferedReader(new FileReader("input.txt"));
		else
			in = new BufferedReader(new InputStreamReader(System.in));
		ans = new StringBuilder();

		// make all days
		nDays = 146462;
		days = new Day[nDays + 1];
		int idx = 0;
		for (int year = 1700; year < 2101; year++)
			for (int month = 1; month <= 12; month++)
				for (int day = 1; day <= 31; day++)
					if (valid(day, month, year))
					{
						days[idx] = new Day(idx, year, month, day);
						idx++;
					}
		days[nDays] = new Day(nDays, 2101, 1, 1);

		// read cases
		int cas = 0;
		while (true)
		{
			// params
			tok = new StringTokenizer(in.readLine());
			nOld = Integer.parseInt(tok.nextToken());
			nNew = Integer.parseInt(tok.nextToken());
			if (nOld == 0 && nNew == 0)
				break;

			// read old
			oldL = new int[nOld];
			oldR = new int[nOld];
			for (int i = 0; i < nOld; i++)
			{
				tok = new StringTokenizer(in.readLine());
				int l = Integer.parseInt(tok.nextToken());
				oldL[i] = toIdx(l);
				int r = Integer.parseInt(tok.nextToken());
				oldR[i] = toIdx(r);
			}

			// read new
			newL = new int[nNew];
			newR = new int[nNew];
			for (int i = 0; i < nNew; i++)
			{
				tok = new StringTokenizer(in.readLine());
				int l = Integer.parseInt(tok.nextToken());
				newL[i] = toIdx(l);
				int r = Integer.parseInt(tok.nextToken());
				newR[i] = toIdx(r);
			}

			// solve
			cas++;
			ans.append("Case " + cas + ":\n");
			ans.append(solve() + "\n");

//			String solveCorrect = solveBrute();
//			if (solveCorrect.equals(solve()) == false)
//				System.out.println("faiiiiiiiiiiiiiiiiiiiiiiiil " + cas);
		}

		// output
		System.out.println(ans.toString().trim());
	}

	private String solveBrute()
	{
		// find min
		int min = Integer.MAX_VALUE;
		for (int x : oldL)
			min = Math.min(min, x);
		for (int x : newL)
			min = Math.min(min, x);

		// find max
		int max = Integer.MIN_VALUE;
		for (int x : oldR)
			max = Math.max(max, x);
		for (int x : newR)
			max = Math.max(max, x);

		boolean oldA[] = new boolean[max + 1];
		boolean newA[] = new boolean[max + 1];
		for (int i = 0; i < nOld; i++)
			for (int j = oldL[i]; j <= oldR[i]; j++)
				oldA[j] = true;
		for (int i = 0; i < nNew; i++)
			for (int j = newL[i]; j <= newR[i]; j++)
				newA[j] = true;

		// find days that need to be updated
		StringBuilder strb = new StringBuilder();
		int d = min;
		while (d <= max)
		{
			if (newA[d] && !oldA[d])
			{
				// find the last day in that range
				int start = d;
				while (d <= max && newA[d] && !oldA[d])
					d++;
				int end = d - 1;

				// add to answer
				if (start == end)
					strb.append("    " + days[start] + "\n");
				else
					strb.append("    " + days[start] + " to " + days[end] + "\n");
			} else
				d++;
		}

		// return answer
		if (strb.length() > 0)
			return strb.toString();
		else
			return "    No additional quotes are required.\n";

	}

	private String solve()
	{
		// find min
		int min = Integer.MAX_VALUE;
		for (int x : oldL)
			min = Math.min(min, x);
		for (int x : newL)
			min = Math.min(min, x);

		// find max
		int max = Integer.MIN_VALUE;
		for (int x : oldR)
			max = Math.max(max, x);
		for (int x : newR)
			max = Math.max(max, x);

		// make old tree
		Node oldTree = makeTree(min, max);
		for (int i = 0; i < nOld; i++)
			setTrue(oldTree, oldL[i], oldR[i]);

		// make new tree
		Node newTree = makeTree(min, max);
		for (int i = 0; i < nNew; i++)
			setTrue(newTree, newL[i], newR[i]);

		// find days that need to be updated
		StringBuilder strb = new StringBuilder();
		int d = min;
		while (d <= max)
		{
			if (getVal(newTree, d) && !getVal(oldTree, d))
			{
				// find the last day in that range
				int start = d;
				while (d <= max && getVal(newTree, d) && !getVal(oldTree, d))
					d++;
				int end = d - 1;

				// add to answer
				if (start == end)
					strb.append("    " + days[start] + "\n");
				else
					strb.append("    " + days[start] + " to " + days[end] + "\n");
			} else
				d++;
		}

		// return answer
		if (strb.length() > 0)
			return strb.toString();
		else
			return "    No additional quotes are required.\n";
	}

	/**
	 * gets idx of that day
	 */
	private int toIdx(int x)
	{
		// get fields
		int year = x / 10000;
		int month = (x % 10000) / 100;
		int day = x % 100;

		// bs for the day
		int start = 0;
		int end = nDays;
		while (start <= end)
		{
			int mid = (start + end) / 2;
			int cmp = comp(days[mid], year, month, day);
			if (cmp== 0)
				return mid;
			else if (cmp == -1)
				start = mid+1;
			else
				end = mid-1;
		}
		return -1;
	}

	private int comp(Day day, int y, int m, int d)
	{
		if (day.year != y)
			return day.year < y ? -1 : 1;
		else if (day.month != m)
			return day.month < m ? -1 : 1;
		else if (day.day != d)
			return day.day < d ? -1 : 1;
		else
			return 0;
	}

	private boolean valid(int day, int month, int year)
	{
		// Recall that months 04, 06, 09, and 11
		if (month == 4 || month == 6 || month == 9 || month == 11)
			if (day == 31)
				return false;

		// leap years
		if (month == 2)
		{
			boolean leap = (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0);
			if (leap)
				if (day > 29)
					return false;
			if (!leap)
				if (day > 28)
					return false;
		}

		return true;
	}

	private boolean getVal(Node node, int i)
	{
		// node set
		if (node.set)
			return node.val;

		// base case
		if (node.l == node.r)
			return node.val;

		// see which child
		int mid = (node.l + node.r) / 2;
		if (i <= mid)
			return getVal(node.left, i);
		else
			return getVal(node.right, i);
	}

	private void setTrue(Node node, int l, int r)
	{
		// out of range
		if (l > node.r || r < node.l)
			return;

		// all segment included
		if (l <= node.l && r >= node.r)
		{
			node.val = true;
			node.set = true;
			return;
		}

		// go to children
		if (node.left != null)
		{
			setTrue(node.left, l, r);
			setTrue(node.right, l, r);
		}

	}

	private Node makeTree(int i, int j)
	{
		// base case
		if (i == j)
			return new Node(i, j);

		// make two children
		int mid = (i + j) / 2;
		Node node = new Node(i, j);
		node.left = makeTree(i, mid);
		node.right = makeTree(mid + 1, j);
		return node;
	}

}

class Node
{
	/* for tree */
	boolean val;
	boolean set;
	int l;
	int r;
	Node left;
	Node right;

	public Node(int l, int r)
	{
		this.l = l;
		this.r = r;
	}
}

class Day
{
	int idx;
	int year;
	int month;
	int day;

	public Day(int idx, int year, int month, int day)
	{
		this.idx = idx;
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public String toString()
	{
		return month + "/" + day + "/" + year;
	}

}