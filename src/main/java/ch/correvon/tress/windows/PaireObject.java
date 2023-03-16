package ch.correvon.tress.windows;

public class PaireObject
{
	public PaireObject(Object o1, Object o2)
	{
		this.o1 = o1;
		this.o2 = o2;
	}
	
	public Object getO1()
	{
		return this.o1;
	}
	
	public Object getO2()
	{
		return this.o2;
	}
	
	private Object o1;
	private Object o2;
}
