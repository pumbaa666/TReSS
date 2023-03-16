package ch.correvon.tress.windows.mainWindow;

import java.io.File;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class FileModel extends AbstractTableModel
{
	/* ------------------------------------------------------------ *\
	|* 		  				Constructeur							*|
	\* ------------------------------------------------------------ */
	public FileModel()
	{
		this.listFile = new ArrayList<FileType>(50);
		this.addRootAndParent();
		this.isEditable = false;
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Méthodes publiques						*|
	\* ------------------------------------------------------------ */
	public void addRow(FileType file)
	{
		this.insertRow(this.listFile.size(), file);
	}

	public void insertRow(int row, FileType file)
	{
		this.listFile.add(row, file);
		super.fireTableRowsInserted(row, row);
	}

	public void removeRow(int index)
	{
		this.listFile.remove(index);
		super.fireTableRowsDeleted(index, index);
	}

	public void removeRow(FileType file)
	{
		int index = this.listFile.indexOf(file);
		if(index < 0)
			return;
		this.removeRow(index);
		super.fireTableRowsDeleted(index, index);
	}

	public void clear()
	{
		int nbLines = this.listFile.size();
		this.listFile.clear();
		this.addRootAndParent();
		super.fireTableRowsDeleted(0, nbLines);
	}

	/* ----------------------------- *\
	|* 				Get 			 *|
	\* ----------------------------- */
	@Override public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override public int getRowCount()
	{
		return this.listFile.size();
	}

	public int getSelectedRowCount()
	{
		int nb = 0;
		for(FileType file:this.listFile)
			if(file.isEnabled())
				nb++;
		return nb;
	}

	@Override public String getColumnName(int column)
	{
		return columnNames[column];
	}

	@Override public Class<?> getColumnClass(int column)
	{
		switch(column)
		{
			case 0:
				return Boolean.class;
			default:
				return String.class;
		}
	}

	@Override public Object getValueAt(int row, int column)
	{
		FileType file = this.listFile.get(row);
		switch(column)
		{
			case 0:
				return file.isEnabled();
			case 1:
				return file.getName();
			case 2:
				return file.getType();
			default:
				return null;
		}
	}

	public ArrayList<FileType> getFiles()
	{
		return this.listFile;
	}
	
	public ArrayList<File> getSelectedFiles()
	{
		ArrayList<File> selectedFiles = new ArrayList<File>(this.listFile.size());
		
		for(FileType file:this.listFile)
			if(file.isEnabled())
				selectedFiles.add(new File(file.getFile().getAbsolutePath()));
		
		return selectedFiles;
	}
	
	/* ----------------------------- *\
	|* 				Is	 			 *|
	\* ----------------------------- */
	@Override public boolean isCellEditable(int row, int column)
	{
		return row > 1 && (column == 0 || (this.isEditable && column == 1)); // Les lignes 0 et 1 ("." et ".." ne sont pas sélectionnables)
	}

	/* ----------------------------- *\
	|* 				Set 			 *|
	\* ----------------------------- */
	@Override public void setValueAt(Object aValue, int row, int column)
	{
		FileType file = this.listFile.get(row);
		switch(column)
		{
			case 0:
				file.setEnabled((Boolean)aValue);
				break;
			case 1:
				file.setName((String)aValue);
				break;
			case 2:
				file.setType((FileTypeEnum)aValue);
				break;
		}
	}
	
	public void setEditable(boolean isEditable)
	{
		this.isEditable = isEditable;
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Méthodes privées						*|
	\* ------------------------------------------------------------ */
	private void addRootAndParent()
	{
		this.listFile.add(new FileType(new File("."), FileTypeEnum.DOSSIER, false));
		this.listFile.add(new FileType(new File(".."), FileTypeEnum.DOSSIER, false));
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Attributs privés						*|
	\* ------------------------------------------------------------ */
	private ArrayList<FileType> listFile;
	private boolean isEditable;

	/* ------------------------------------- *\
	|* 				Statiques	 			 *|
	\* ------------------------------------- */
	private static final String[] columnNames = { "[v]", "Nom", "Type" };
	private static final long serialVersionUID = 5906888538680435549L;
}
