package ch.correvon.tress.windows.rollbackWindow;

import java.io.File;

public class FromToFile
{
	public FromToFile(String fromFilePath, String toFilePath)
	{
		this(new File(fromFilePath), new File(toFilePath));
	}
	
	public FromToFile(File fromFile, File toFile)
	{
		this.fromFile = fromFile;
		this.toFile = toFile;
		this.enabled = true;
	}
	
	public Boolean isEnabled()
	{
		return this.enabled;
	}
	
	public void setEnabled(Boolean value)
	{
		this.enabled = value;
	}

	public File getFromFile()
	{
		return this.fromFile;
	}
	
	public void setFromFile(File fromFile)
	{
		this.fromFile = fromFile;
	}

	/*public void setFromFile(String path)
	{
		this.setFromFile(new File(path));
	}*/
	
	public File getToFile()
	{
		return this.toFile;
	}
	
	public void setToFile(File toFile)
	{
		this.toFile = toFile;
	}

	public void setToFile(String fileName)
	{
		this.setToFile(new File(this.toFile.getParent()+"\\"+fileName));
	}
	
	private Boolean enabled;
	private File fromFile;
	private File toFile;
}
