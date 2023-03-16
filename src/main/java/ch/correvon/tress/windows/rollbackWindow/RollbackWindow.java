/*
 * Created by JFormDesigner on Tue Oct 26 08:35:43 CEST 2010
 */

package ch.correvon.tress.windows.rollbackWindow;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import ch.correvon.tress.use.MainTress;
import ch.correvon.tress.windows.mainWindow.MainWindow;
import ch.correvon.utils.helpers.FileHelper;
import ch.correvon.utils.helpers.StringHelper;

/**
 * @author Loïc Correvon
 */
public class RollbackWindow extends JFrame
{
	public RollbackWindow(MainWindow mainWindow, String path)
	{
		initComponents();
		this.mainWindow = mainWindow;
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.textPath.setText(path);
		this.fillTableFiles();
		this.setVisible(true);
	}
	
	private void fillTableFiles()
	{
		File file = new File(this.textPath.getText());
		if(!file.exists())
		{
			JOptionPane.showMessageDialog(this, "Fichier invalide", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try
		{
			InputStreamReader ipsr = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufferReader = new BufferedReader(ipsr);
			String ligne;
			File fromFile;
			File toFile;
			String[] splitStr;
			while((ligne = bufferReader.readLine()) != null)
			{
				splitStr = ligne.split(":to:");
				if(splitStr.length != 2)
					continue;
				
				fromFile = new File(splitStr[1]); // l'ancien to [1] devient le nouveau from
				toFile = new File(splitStr[0]); // l'ancien from [0] devient le nouveau to
				this.tableFilesModel.addRow(new FromToFile(fromFile, toFile));
			}
			bufferReader.close();
		}
		catch(Exception e)
		{
			System.err.println("Problème lors de la lecture du fichier " + file.getAbsolutePath());
		}
	}

	private void buttonApplyActionPerformed(ActionEvent e)
	{
		String logText = "";
		int nbRollbacked = 0;
		ArrayList<FromToFile> selectedFiles = this.tableFilesModel.getSelectedFiles();
		String nonRollbackedFiles = "";
		for(FromToFile fromToFile:selectedFiles)
		{
			if(fromToFile.getFromFile().renameTo(fromToFile.getToFile()))
			{
				logText += fromToFile.getFromFile().getAbsolutePath()+":to:"+fromToFile.getToFile().getAbsolutePath()+"\n";
				nbRollbacked++;
			}
			else
				nonRollbackedFiles += fromToFile.getFromFile().getAbsolutePath()+"\n";
		}
		
		String filePath = MainTress.DATA_STORE_DIR.getAbsolutePath() + File.separator + StringHelper.generateLogFileName("", "_rollback", ".log");
		FileHelper.writeFile(filePath, logText);
		JOptionPane.showMessageDialog(this,
				nbRollbacked+" fichiers sur "+selectedFiles.size()+" ont été rollbacké.\n\nFichiers non-rollbackés :\n"+nonRollbackedFiles,
				"Résultat",
				JOptionPane.INFORMATION_MESSAGE);
		this.mainWindow.refresh();
		super.dispose();
	}

	private void buttonCancelActionPerformed(ActionEvent e)
	{
		super.dispose();
	}

	private void initComponents()
	{
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		this.textPath = new JTextField();
		JScrollPane scrollTableFiles = new JScrollPane();
		this.tableFiles = new JTable();
		JButton buttonApply = new JButton();
		JButton buttonCancel = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle("Rollback");
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, $lcgap, default:grow, 2*($lcgap, default), $lcgap, $ugap",
			"$ugap, 3*($lgap, default), $lgap, $ugap"));
		contentPane.add(this.textPath, cc.xywh(3, 3, 5, 1));

		//======== scrollTableFiles ========
		scrollTableFiles.setViewportView(this.tableFiles);
		contentPane.add(scrollTableFiles, cc.xywh(3, 5, 5, 1));

		//---- buttonApply ----
		buttonApply.setText("Appliquer");
		buttonApply.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				buttonApplyActionPerformed(e);
			}
		});
		contentPane.add(buttonApply, cc.xy(5, 7));

		//---- buttonCancel ----
		buttonCancel.setText("Annuler");
		buttonCancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				buttonCancelActionPerformed(e);
			}
		});
		contentPane.add(buttonCancel, cc.xy(7, 7));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
		
		this.tableFilesModel = new FromToFileModel();
		this.tableFiles.setModel(this.tableFilesModel);
		this.tableFiles.setDefaultRenderer(JTextField.class, new FromToFileCellRenderer());
		
		TableColumnModel columnModel = this.tableFiles.getColumnModel();
		TableColumn column0 = columnModel.getColumn(0);
		column0.setMaxWidth(5);
	}

	private MainWindow mainWindow;
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JTextField textPath;
	private JTable tableFiles;
	FromToFileModel tableFilesModel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
