package ch.correvon.tress.windows.rollbackWindow;

import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

public class FromToFileModel extends AbstractTableModel
{
	/* ------------------------------------------------------------ *\
	|* 		  				Constructeur							*|
	\* ------------------------------------------------------------ */
	public FromToFileModel()
	{
		this.listFile = new ArrayList<FromToFile>(50);
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Méthodes publiques						*|
	\* ------------------------------------------------------------ */
	public void addRow(FromToFile file)
	{
		this.insertRow(this.listFile.size(), file);
	}

	public void insertRow(int row, FromToFile file)
	{
		this.listFile.add(row, file);
		super.fireTableRowsInserted(row, row);
	}

	public void removeRow(int index)
	{
		this.listFile.remove(index);
		super.fireTableRowsDeleted(index, index);
	}

	public void removeRow(FromToFile file)
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
		for(FromToFile file:this.listFile)
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
				return JTextField.class;
		}
	}

	@Override public Object getValueAt(int row, int column)
	{
		FromToFile file = this.listFile.get(row);
		switch(column)
		{
			case 0: return file.isEnabled();
			case 1: return file.getFromFile().getName();
			case 2: return file.getToFile().getName();
			default: return null;
		}
	}

	public ArrayList<FromToFile> getFiles()
	{
		return this.listFile;
	}
	
	public ArrayList<FromToFile> getSelectedFiles()
	{
		ArrayList<FromToFile> selectedFiles = new ArrayList<FromToFile>(this.listFile.size());
		
		for(FromToFile file:this.listFile)
			if(file.isEnabled())
				selectedFiles.add(new FromToFile(file.getFromFile(), file.getToFile()));
		
		return selectedFiles;
	}
	
	/* ----------------------------- *\
	|* 				Is	 			 *|
	\* ----------------------------- */
	@Override public boolean isCellEditable(int row, int column)
	{
		return column != 1;
	}

	/* ----------------------------- *\
	|* 				Set 			 *|
	\* ----------------------------- */
	@Override public void setValueAt(Object aValue, int row, int column)
	{
		FromToFile file = this.listFile.get(row);
		switch(column)
		{
			case 0:
				file.setEnabled((Boolean)aValue);
				break;
			/*case 1:
				file.setFromFile((String)aValue);
				break;*/
			case 2:
				JTextField textField = (JTextField)aValue;
				file.setToFile(textField.getText());
				break;
		}
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Méthodes privées						*|
	\* ------------------------------------------------------------ */

	/* ------------------------------------------------------------ *\
	|* 		  				Attributs privés						*|
	\* ------------------------------------------------------------ */
	private ArrayList<FromToFile> listFile;

	/* ------------------------------------- *\
	|* 				Statiques	 			 *|
	\* ------------------------------------- */
	private static final String[] columnNames = { "[v]", "De", "À" };
	private static final long serialVersionUID = 5906888538680435549L;
}
