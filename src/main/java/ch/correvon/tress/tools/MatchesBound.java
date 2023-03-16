package ch.correvon.tress.tools;

public class MatchesBound
{
	public MatchesBound(int start, int end)
	{
		this.start = start;
		this.end = end;
	}
	
	public void setStart(int start)
	{
		this.start = start;
	}
	
	public void setEnd(int end)
	{
		this.end = end;
	}
	
	public int getStart()
	{
		return this.start;
	}
	
	public int getEnd()
	{
		return this.end;
	}
	
	private int start;
	private int end;
}
