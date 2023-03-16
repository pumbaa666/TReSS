package ch.correvon.tress.windows.mainWindow;

import java.io.File;

public class FileType implements Comparable<FileType>
{
	/* ------------------------------------------------------------ *\
	|* 		  				Constructeur							*|
	\* ------------------------------------------------------------ */
	public FileType(File file)
	{
		this(file, file.isDirectory()?FileTypeEnum.DOSSIER:FileTypeEnum.FICHIER, true);
	}
	
	public FileType(File file, FileTypeEnum type)
	{
		this(file, type, true);
	}
	
	public FileType(FileType fileType)
	{
		this(fileType.getFile(), fileType.getType(), fileType.isEnabled());
	}
	
	public FileType(File file, FileTypeEnum type, boolean enabled)
	{
		this.file = file;
		this.type = type;
		this.setEnabled(enabled);
	}
	/* ------------------------------------------------------------ *\
	|* 		  				Méthodes publiques						*|
	\* ------------------------------------------------------------ */
	@Override public String toString()
	{
		return this.getFile().getAbsolutePath();
	}
	
	@Override public int compareTo(FileType o)
	{
		return this.file.getName().compareTo(o.getName());
	}

	/* ----------------------------- *\
	|* 				Get 			 *|
	\* ----------------------------- */
	public String getName()
	{
		return this.file.getName();
	}
	
	public File getFile()
	{
		return this.file;
	}
	
	public FileTypeEnum getType()
	{
		return this.type;
	}
	
	/* ----------------------------- *\
	|* 				Is	 			 *|
	\* ----------------------------- */
	public boolean isEnabled()
	{
		return this.enabled;
	}

	/* ----------------------------- *\
	|* 				Set 			 *|
	\* ----------------------------- */
	public void setName(String name)
	{
		this.file = new File(this.file.getAbsolutePath() + "\\" + name);
	}
	
	public void setFile(File file)
	{
		this.file = file;
	}
	
	public void setType(FileTypeEnum type)
	{
		this.type = type;
	}
	
	public void setEnabled(boolean enabled)
	{
		if(this.file.getName().equals(".") || this.file.getName().equals(".."))
			this.enabled = false;
		else
			this.enabled = enabled;
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Méthodes privées						*|
	\* ------------------------------------------------------------ */

	/* ------------------------------------------------------------ *\
	|* 		  				Attributs privés						*|
	\* ------------------------------------------------------------ */
	private File file;
	private FileTypeEnum type;
	private boolean enabled;
}
