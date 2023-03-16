package ch.correvon.tress.windows.rollbackWindow;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FromToFileCellRenderer extends DefaultTableCellRenderer
{
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		cell.setBackground(Color.WHITE);
		FromToFileModel model = (FromToFileModel)table.getModel();
		FromToFile fromToFile = model.getFiles().get(row);
		if(column == 1 && !fromToFile.getFromFile().exists())
			cell.setBackground(Color.RED);
		else if(column == 2 && !fromToFile.getToFile().exists())
			cell.setBackground(Color.RED);

		return cell;
	}
}
