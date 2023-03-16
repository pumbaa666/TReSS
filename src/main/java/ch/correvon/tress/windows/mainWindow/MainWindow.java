/*
 * Created by JFormDesigner on Fri Mar 07 10:39:45 CET 2008
 */

package ch.correvon.tress.windows.mainWindow;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.TableColumnModel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import ch.correvon.tress.tools.FileNameHelper;
import ch.correvon.tress.tools.ListHelper;
import ch.correvon.tress.use.MainTress;
import ch.correvon.tress.windows.BindFocus;
import ch.correvon.tress.windows.rollbackWindow.JLimitedTextField;
import ch.correvon.tress.windows.rollbackWindow.RollbackWindow;
import ch.correvon.utils.helpers.ClipBoardHelper;
import ch.correvon.utils.helpers.ComponentHelper;
import ch.correvon.utils.helpers.FileHelper;
import ch.correvon.utils.helpers.StringHelper;

/**
 * @author Loic Correvon
 */
public class MainWindow extends JFrame
{
	/* ********************************************************* *\
	|*						Constructors						 *|
	\* ********************************************************* */
	public MainWindow(String title)
	{
		super(title);
		this.initComponents();
		this.bindComponents();
		this.currentPath = null;
		String dir = ClipBoardHelper.readClipboard();
		this.textPath.setText(dir);
		if(!dir.isEmpty())
			this.readFolder(dir);
		
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/* ********************************************************* *\
	|*						Public methods						 *|
	\* ********************************************************* */
	public void run()
	{
		super.setVisible(true);
	}
	
	public void refresh()
	{
		this.readFolder(this.currentPath);
	}

	/* ********************************************************* *\
	|*						Private methods						 *|
	\* ********************************************************* */
	/* ***************** *\
	|* 		 File		 *|
	\* ***************** */
	private void menuFichierOuvrirDossierActionPerformed(ActionEvent e)
	{
		JFileChooser fileChooser = new JFileChooser(this.currentPath);
		fileChooser.setDialogTitle("Ouvrir un dossier");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File folder = fileChooser.getSelectedFile();
			if(folder != null)
			{
				this.readFolder(folder);
				this.textPath.setText(folder.getAbsolutePath());
			}
		}
	}

	private void readFolder(String folderPath)
	{
		if(folderPath != null && !folderPath.isEmpty())
			this.readFolder(new File(folderPath));
	}

	private void readFolder(File folderPath)
	{
		this.currentPath = folderPath.getAbsolutePath();
		this.showFileType();
	}

	private ArrayList<FileType> getSelectedFileList()
	{
		ArrayList<FileType> listeInput = this.tableEntreeModel.getFiles();
		ArrayList<FileType> listeOutput = new ArrayList<FileType>(listeInput.size());
				
		for(FileType file:listeInput)
			if(file.isEnabled())
				listeOutput.add(new FileType(file));

		return listeOutput;
	}

	/* **************************** *\
	|* 		 Buttons actions	    *|
	\* **************************** */
	private void buttonAddActionPerformed(ActionEvent e)
	{
		this.textAddText.setBackground(Color.white);
		this.textAddCountPadding.setBackground(Color.white);
		
		this.textOut.setText("");
		ArrayList<FileType> files = this.getSelectedFileList();

		int position = ComponentHelper.intValueOf(this.spinnerAddPosition);
		boolean atEnd = this.radioAddPositionEnd.isSelected();
		boolean atEndOfExt = this.checkAddEndExt.isSelected();
		String newName;
		int tempPos;
		String fileName;
		int lineLen;
		String newText = this.textAddText.getText();

		int start = ComponentHelper.intValueOf(this.spinnerAddCountStart);
		int increment = ComponentHelper.intValueOf(this.spinnerAddCountInc);
		
		String padding = "";
		if(this.checkAddCountPadding.isSelected())
			padding = ComponentHelper.charValueOf(this.textAddCountPadding);
		
		boolean warn = false;
		if(!FileNameHelper.isFileNameValid(newText))
		{
			this.textAddText.setBackground(Color.red);
			warn = true;
		}
		if(!FileNameHelper.isFileNameValid(padding))
		{
			this.textAddCountPadding.setBackground(Color.red);
			warn = true;
		}
		if(warn)
			JOptionPane.showMessageDialog(this, deprecatedMessage, deprecatedTitle, JOptionPane.WARNING_MESSAGE);
		
		String compteurText;
		int compteur = start;
		int nbChar = StringHelper.getNbRequiredChar(files.size(), increment, start);
		int nbCharFromExt;

		for(FileType file:files)
		{
			fileName = file.getName();
			lineLen = fileName.length();
			tempPos = position;
			nbCharFromExt = 0;
			
			if(atEnd)
			{
				tempPos = lineLen - position;
				if(atEndOfExt)
				{
					nbCharFromExt = StringHelper.findExtension(fileName).length();
					if(nbCharFromExt > 0)
						nbCharFromExt++; // Rajoute un pour le point de l'extension
				}
				tempPos -= nbCharFromExt;
				
				if(tempPos < 0)
					tempPos = 0;
			}
			else
			{
				if(tempPos > lineLen)
					tempPos = lineLen;
			}
			
			compteurText = StringHelper.getPadding(padding, nbChar, "" + compteur);
			
			if(this.radioAddText.isSelected())
				newName = fileName.substring(0, tempPos) + newText + fileName.substring(tempPos);
			else
				newName = fileName.substring(0, tempPos) + compteurText + fileName.substring(tempPos);
			file.setName(newName);
			compteur += increment;
			this.textOut.append(newName + "\n");
		}
	}

	private void buttonDeleteActionPerformed(ActionEvent e)
	{
		this.textOut.setText("");
		ArrayList<FileType> files = this.getSelectedFileList();

		if(this.radioDeleteRemoveFirst.isSelected())
			ListHelper.removeFirsts(ComponentHelper.intValueOf(this.spinnerDeleteRemove), files);
		else if(this.radioDeleteRemoveLast.isSelected())
			ListHelper.removeLasts(ComponentHelper.intValueOf(this.spinnerDeleteRemove), files);
		else if(this.radioDeleteBetween.isSelected())
		{
			int start = ComponentHelper.intValueOf(this.spinnerDeleteBetweenStart);
			int end = ComponentHelper.intValueOf(this.spinnerDeleteBetweenEnd);
			if(start > end)
			{
				ComponentHelper.extractTextField(this.spinnerDeleteBetweenStart).setBackground(Color.red);
				ComponentHelper.extractTextField(this.spinnerDeleteBetweenEnd).setBackground(Color.red);
				JOptionPane.showMessageDialog(
						this,
						"Le nombre de début est plus grand que le nombre de fin",
						"Erreur",
						JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				ComponentHelper.extractTextField(this.spinnerDeleteBetweenStart).setBackground(Color.white);
				ComponentHelper.extractTextField(this.spinnerDeleteBetweenEnd).setBackground(Color.white);
				ListHelper.removeBetween(start, end, files);
			}
		}
		else if(this.radioDeleteSearch.isSelected())
		{
			this.textDeleteReplace.setBackground(Color.white);
			String replaceText = this.textDeleteReplace.getText();
			
			if(!FileNameHelper.isFileNameValid(replaceText))
			{
				this.textDeleteReplace.setBackground(Color.red);
				JOptionPane.showMessageDialog(this, deprecatedMessage, deprecatedTitle, JOptionPane.WARNING_MESSAGE);
			}

			try
			{
				ListHelper.removeSearch(
						files,
						this.textDeleteSearch.getText(),
						replaceText,
						ComponentHelper.intValueOf(this.spinnerDeleteReplaceN),
						this.checkDeleteCaseSensitive.isSelected(),
						this.checkDeleteRegex.isSelected());
				this.textDeleteSearch.setBackground(Color.white);
			}
			catch(PatternSyntaxException exeption)
			{
				this.textDeleteSearch.setBackground(Color.red);
				int answer = JOptionPane.showConfirmDialog(
						this,
						"Votre exception régulière est incorrect, voulez-vous voir la trace ?",
						"Erreur",
						JOptionPane.YES_NO_OPTION);
				if(answer == JOptionPane.YES_OPTION)
				{
					JOptionPane.showMessageDialog(
						this,
						exeption.getMessage(),
						"PatternSyntaxException",
						JOptionPane.OK_OPTION);
				}
			}
		}

		this.textOut.setText("");
		for(FileType file:files)
			this.textOut.append(file.getName() + "\n");
	}

	private void buttonRenameActionPerformed(ActionEvent e)
	{
		this.textRenameTexte.setBackground(Color.white);
		this.textRenameExtension.setBackground(Color.white);
		this.textRenameCountPadding.setBackground(Color.white);

		ArrayList<FileType> files = this.getSelectedFileList();
		int nbFiles = files.size();
		String remplacement = this.textRenameTexte.getText();
		String extensionInit = this.textRenameExtension.getText();
		String padding = "";
		if(this.checkRenameCountPadding.isSelected())
			padding = ComponentHelper.charValueOf(this.textRenameCountPadding);

		boolean warn = false;
		if(!FileNameHelper.isFileNameValid(remplacement))
		{
			this.textRenameTexte.setBackground(Color.red);
			warn = true;
		}
		if(!FileNameHelper.isFileNameValid(extensionInit))
		{
			this.textRenameExtension.setBackground(Color.red);
			warn = true;
		}
		if(!FileNameHelper.isFileNameValid(padding))
		{
			this.textRenameCountPadding.setBackground(Color.red);
			warn = true;
		}
		if(warn)
			JOptionPane.showMessageDialog(this, deprecatedMessage, deprecatedTitle, JOptionPane.WARNING_MESSAGE);

		int start = ComponentHelper.intValueOf(this.spinnerRenameCountStart);
		int increment = ComponentHelper.intValueOf(this.spinnerRenameCountIncrement);
		int number = start;

		this.textOut.setText("");
		String newName;
		String extension = "";
		int nbChar;
		String numberStr;
		for(int i = 0; i < nbFiles; i++/*FileType file:files*/)
		{
			if(extensionInit.isEmpty())
			{
				extension = "";
				//if(this.checkExtAuto.isSelected())
					//extension = StringHelper.findExtension(file.getName(), true);
			}
			else
				extension = "." + extensionInit;
			
			nbChar = StringHelper.getNbRequiredChar(nbFiles, increment, start);
			numberStr = StringHelper.getPadding(padding, nbChar, number);
			
			if(this.radioRenameStart.isSelected())
				newName = numberStr + remplacement + extension;
			else
			{
				if(this.checkRenameEndExt.isSelected())
					newName = remplacement + extension + numberStr;
				else
					newName = remplacement + numberStr + extension;
			}
			
			this.textOut.append(newName + "\n");
			number += increment;
		}
	}

	private void buttonUppercaseActionPerformed(ActionEvent e)
	{
		ArrayList<FileType> input = this.getSelectedFileList();

		if(this.radioUppercaseEverywhere.isSelected())
			ListHelper.uppercaseEverywhere(input);
		else if(this.radioUppercaseNowhere.isSelected())
			ListHelper.uppercaseNowhere(input);
		else if(this.radioUppercaseEveryWord.isSelected())
			ListHelper.uppercaseWord(input, this.checkUppercaseEveryWord.isSelected(), ComponentHelper.intValueOf(this.spinnerUppercaseNb));
		else if(this.radioUppercaseFirstWord.isSelected())
			ListHelper.uppercaseFirstWord(input, this.checkUppercaseFirstWord.isSelected());
		else if(this.radioUppercaseAfter.isSelected())
		{
			String[] seq = this.textUppercaseAfter.getText().replace("\\t", "\t").replace("\\n", "\n").split("/");
			int nbSeq = seq.length;
			ArrayList<String> sequence = new ArrayList<String>(nbSeq);
			for(int i = 0; i < nbSeq; i++)
				sequence.add(seq[i]);
			ListHelper.uppercaseAfter(input, sequence, false, this.checkUppercaseAfter.isSelected(), ComponentHelper.intValueOf(this.spinnerUppercaseNb), this.checkUppercaseCaseSensitive.isSelected());
		}

		this.textOut.setText("");
		for(FileType file:input)
			this.textOut.append(file.getName() + "\n");
	}

	private void menuFichierQuitterActionPerformed(ActionEvent e)
	{
		this.exit();
	}

	private void exit()
	{
		/*String[] newNames = this.textOut.getText().split("\n");
		int i = 0;
		boolean modif = false;
		for(File fileType:this.tableEntreeModel.getSelectedFiles())
		{
			if(!fileType.getName().equals(newNames[i]))
			{
				modif = true;
				break;
			}
			i++;
		}
		
		if(modif)
		{
			int answer = JOptionPane.showConfirmDialog(
					this,
					"Il y a des modifications non effectuées, voulez-vous vraiment quitter ?",
					"Modification",
					JOptionPane.YES_NO_OPTION);
			if(answer == JOptionPane.YES_OPTION)
				System.exit(0);
		}
		else*/
			System.exit(0);
	}

	private void buttonApplyActionPerformed(ActionEvent e)
	{
		if(this.currentPath == null || this.currentPath.equals(""))
			return;
		
		String[] newNames = this.textOut.getText().split("\n");
		int nbSelectedFiles = this.tableEntreeModel.getSelectedRowCount();
		int nbRenamedFiles = newNames.length;
		
		if(nbRenamedFiles != nbSelectedFiles)
		{
			JOptionPane.showMessageDialog(
					this,
					"Le nombre de fichier en sortie (zone de texte à droite : "+nbRenamedFiles+") ne correspond pas au nombre de fichiers lus ("+nbSelectedFiles+").\nVeuillez re-cliquer sur Aperéu et/ou Actualiser et/ou changer la sélection dans le panneau de gauche [v]",
					"Erreur",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int i = 0;
		String newFile;
		String oldFile;
		String extension = "";
		String logText = "";
		boolean rollback = false;
		for(File file:this.tableEntreeModel.getSelectedFiles())
		{
			oldFile = file.getAbsolutePath();
			if(this.checkExtAuto.isSelected()/* && !file.isDirectory()*/)
			{
				extension = StringHelper.findExtension(file.getName(), true);
				if(extension.equals(StringHelper.findExtension(newNames[i], true)))
					extension = "";
			}
			newFile = this.currentPath + File.separator + newNames[i] + extension;
			if(!file.renameTo(new File(newFile)))
			{
				Object[] selectionValues = {"Continuer", "Arrêter", "Annuler modifications"};
				Object answer = JOptionPane.showInputDialog(
						super.getContentPane(),
						"Impossible de renommer le fichier n°"+(i+1)+"\n" +
								oldFile+"\n" +
								"en\n" +
								newFile+"\n\n" +
								"Voulez-vous continuer, arrêter là ou annuler les modification ?",
						"Erreur",
						JOptionPane.ERROR_MESSAGE,
						null,
						selectionValues,
						selectionValues[1]);
				
				if(answer == null || ((String)answer).equals(selectionValues[1])) // Annuler ou Arrêter
					break;
				else if(((String)answer).equals(selectionValues[2])) // Rollback
				{
					rollback = true;
					break;
				}
			}
			else // Renommage ok --> on log
				logText += oldFile+":to:"+newFile+"\n";
			i++;
		}
		
		this.readFolder(this.currentPath); // Actualise la liste d'entrée.
		String rollbackPath = MainTress.DATA_STORE_DIR.getAbsolutePath() + File.separator + StringHelper.generateLogFileName();
		FileHelper.writeFile(rollbackPath, logText);
		if(rollback)
			new RollbackWindow(this, rollbackPath);
	}

	private void buttonRefreshActionPerformed(ActionEvent e)
	{
		this.refresh();
	}

	private void checkCaseSensitiveItemStateChanged(ItemEvent e)
	{
		this.showFileType();
	}

	/* ******************** *\
	|* 		 Filters	    *|
	\* ******************** */
	private void textFiltreCaretUpdate(CaretEvent e)
	{
		this.showFileType();
	}

	private void checkFilterItemStateChanged(ItemEvent e)
	{
		this.showFileType();
	}

	private void showFileType()
	{
		if(this.currentPath == null)
			return;
		
		File dir = new File(this.currentPath);
		File[] listFiles = dir.listFiles();
		if(listFiles == null || listFiles.length == 0)
			return;
		
		List<FileType> currentList = new ArrayList<FileType>(listFiles.length);
		for(File file:dir.listFiles())
			currentList.add(new FileType(file, file.isFile() ? FileTypeEnum.FICHIER : FileTypeEnum.DOSSIER));
		Collections.sort(currentList);
		
		if(this.checkFiltreShow.isSelected())
		{
			String textAfficher = this.textFiltreShow.getText();
			if(!textAfficher.isEmpty())
				this.filterShow(currentList, textAfficher.split("/"), this.checkFilterCaseSensitive.isSelected());
		}
		
		if(this.checkFiltreHide.isSelected())
		{
			String textMasquer = this.textFiltreHide.getText();
			if(!textMasquer.isEmpty())
				this.filterHide(currentList, textMasquer.split("/"), this.checkFilterCaseSensitive.isSelected());
		}

		boolean showFile = this.checkFiltreShowFiles.isSelected();
		boolean showFolder = this.checkFiltreShowFolders.isSelected();
		this.tableEntreeModel.clear();

		if(showFolder)
			for(FileType file:currentList)
				if(file.getFile().isDirectory())
					this.tableEntreeModel.addRow(file);
		if(showFile)
			for(FileType file:currentList)
				if(file.getFile().isFile())
					this.tableEntreeModel.addRow(file);
	}

	private void filterShow(List<FileType> currentList, String[] sequence, boolean caseSensitive)
	{
		List<FileType> currentListCopy = new ArrayList<FileType>(currentList.size());
		currentListCopy = ListHelper.copyList(currentList);

		int nbSequence = sequence.length;
		for(int i = 0; i < nbSequence; i++)
			for(FileType file:currentListCopy)
				if(!(caseSensitive && file.getName().contains(sequence[i]) || !caseSensitive && file.getName().toLowerCase().contains(sequence[i].toLowerCase())))
					currentList.remove(file);
	}

	private void filterHide(List<FileType> currentList, String[] sequence, boolean caseSensitive)
	{
		List<FileType> currentListCopy = new ArrayList<FileType>(currentList.size());
		currentListCopy = ListHelper.copyList(currentList);
		
		int nbSequence = sequence.length;
		for(int i = 0; i < nbSequence; i++)
			for(FileType file:currentListCopy)
				if(caseSensitive && file.getName().contains(sequence[i]) || !caseSensitive && file.getName().toLowerCase().contains(sequence[i].toLowerCase()))
					currentList.remove(file);
	}

	private void radioRenameEndItemStateChanged(ItemEvent e)
	{
		this.checkRenameEndExt.setEnabled(this.radioRenameEnd.isSelected());
	}

	private void radioAjouterPositionFinItemStateChanged(ItemEvent e)
	{
		this.checkAddEndExt.setEnabled(this.radioAddPositionEnd.isSelected());
	}

	private void menuFichierRollbackActionPerformed(ActionEvent e)
	{	
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setDialogTitle("Ouvrir un fichier de log");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			if(file != null)
				new RollbackWindow(this, file.getAbsolutePath());
		}
	}

	private void checkAllCheckItemStateChanged(ItemEvent e)
	{
		boolean value = this.checkAllCheck.isSelected();
		for(FileType file:this.tableEntreeModel.getFiles())
			file.setEnabled(value);
		this.tableEntree.updateUI();
	}

	private void checkInvertSelectionItemStateChanged(ItemEvent e)
	{
		for(FileType file:this.tableEntreeModel.getFiles())
			file.setEnabled(!file.isEnabled());
		this.tableEntree.updateUI();
	}

	private void buttonClipboardCopyActionPerformed(ActionEvent e)
	{
		StringBuilder text = new StringBuilder(500);
		String fileName;
		
		for(FileType fileType:this.tableEntreeModel.getFiles())
		{
			fileName = fileType.getFile().getName();
			if(!this.checkClipboardCopy.isSelected())
				fileName = StringHelper.removeExtension(fileName);
			text.append(fileName + "\n");
		}
		
		ClipBoardHelper.writeClipboard(text.toString());
	}

	private void textPathCaretUpdate(CaretEvent e)
	{
		String path = ((JTextField)e.getSource()).getText();
		File file = new File(path);
		if(file.isDirectory())
			this.readFolder(path);
		else
			this.tableEntreeModel.clear();
	}

	private void textPathFocusGained(FocusEvent e)
	{
		this.textPath.selectAll();
	}

	@SuppressWarnings("all")
	private void initComponents()
	{
		// JFormDesigner - Component initialization - DO NOT MODIFY //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFichier = new JMenu();
		JMenuItem menuFichierOuvrirDossier = new JMenuItem();
		JMenuItem menuFichierRollback = new JMenuItem();
		JMenuItem menuFichierQuitter = new JMenuItem();
		JLabel labelPath = new JLabel();
		this.textPath = new JTextField();
		JButton buttonRefresh = new JButton();
		this.splitPane = new JSplitPane();
		JPanel panelInOut = new JPanel();
		JLabel labelIn = new JLabel();
		JLabel labelOut = new JLabel();
		JScrollPane scrollTextIn = new JScrollPane();
		this.tableEntree = new JTable();
		JScrollPane scrollTextOut = new JScrollPane();
		this.textOut = new JTextArea();
		this.checkAllCheck = new JCheckBox();
		JButton buttonApply = new JButton();
		this.checkInvertSelection = new JCheckBox();
		this.checkExtAuto = new JCheckBox();
		JPanel panelClipboardCopy = new JPanel();
		JButton buttonClipboardCopy = new JButton();
		this.checkClipboardCopy = new JCheckBox();
		JTabbedPane tabbedPaneOptions = new JTabbedPane();
		JScrollPane scrollRename = new JScrollPane();
		JPanel panelRename = new JPanel();
		JLabel labelRenameText = new JLabel();
		this.textRenameTexte = new JTextField();
		JLabel labelExtension = new JLabel();
		this.textRenameExtension = new JTextField();
		JSeparator separatorRename1 = new JSeparator();
		JLabel labelRenameCount = new JLabel();
		JLabel labelRenameStart = new JLabel();
		this.spinnerRenameCountStart = new JSpinner();
		JSeparator separatorRename2 = new JSeparator();
		this.radioRenameStart = new JRadioButton();
		JLabel labelRenameInc = new JLabel();
		this.spinnerRenameCountIncrement = new JSpinner();
		this.radioRenameEnd = new JRadioButton();
		this.checkRenameCountPadding = new JCheckBox();
		this.textRenameCountPadding = new JLimitedTextField();
		JPanel panelRenameExt = new JPanel();
		this.checkRenameEndExt = new JCheckBox();
		JButton buttonRename = new JButton();
		this.scrollAdd = new JScrollPane();
		JPanel panelAdd = new JPanel();
		this.radioAddText = new JRadioButton();
		this.textAddText = new JTextField();
		this.separatorAdd2 = new JSeparator();
		JSeparator separatorAdd1 = new JSeparator();
		this.radioAddCount = new JRadioButton();
		JLabel radioCountStart = new JLabel();
		this.spinnerAddCountStart = new JSpinner();
		JLabel labelAddPosition = new JLabel();
		this.spinnerAddPosition = new JSpinner();
		JLabel labelAddInc = new JLabel();
		this.spinnerAddCountInc = new JSpinner();
		this.radioAddPositionStart = new JRadioButton();
		this.checkAddCountPadding = new JCheckBox();
		this.textAddCountPadding = new JLimitedTextField();
		this.radioAddPositionEnd = new JRadioButton();
		JPanel panelAddExt = new JPanel();
		this.checkAddEndExt = new JCheckBox();
		JButton buttonAdd = new JButton();
		this.scrollDelete = new JScrollPane();
		JPanel panelDelete = new JPanel();
		this.spinnerDeleteRemove = new JSpinner();
		this.radioDeleteRemoveFirst = new JRadioButton();
		this.radioDeleteRemoveLast = new JRadioButton();
		JSeparator separatorDelete1 = new JSeparator();
		JLabel labelDeleteBetween = new JLabel();
		this.radioDeleteBetween = new JRadioButton();
		this.spinnerDeleteBetweenStart = new JSpinner();
		JLabel labelDeleteAnd = new JLabel();
		this.spinnerDeleteBetweenEnd = new JSpinner();
		JLabel labelDeleteInclude = new JLabel();
		JSeparator separatorDelete2 = new JSeparator();
		JLabel labelDeleteSearch = new JLabel();
		this.radioDeleteSearch = new JRadioButton();
		this.textDeleteSearch = new JTextField();
		this.checkDeleteRegex = new JCheckBox();
		JLabel labelDeleteReplace = new JLabel();
		this.textDeleteReplace = new JTextField();
		this.spinnerDeleteReplaceN = new JSpinner();
		JLabel labelDeleteNb = new JLabel();
		this.checkDeleteCaseSensitive = new JCheckBox();
		JButton buttonDelete = new JButton();
		this.scrollUppercase = new JScrollPane();
		JPanel panelUppercase = new JPanel();
		this.radioUppercaseFirstWord = new JRadioButton();
		this.checkUppercaseFirstWord = new JCheckBox();
		this.radioUppercaseEveryWord = new JRadioButton();
		this.checkUppercaseEveryWord = new JCheckBox();
		this.radioUppercaseNowhere = new JRadioButton();
		this.radioUppercaseEverywhere = new JRadioButton();
		this.radioUppercaseAfter = new JRadioButton();
		this.textUppercaseAfter = new JTextField();
		this.checkUppercaseAfter = new JCheckBox();
		this.checkUppercaseCaseSensitive = new JCheckBox();
		this.separatorUppercase1 = new JSeparator();
		this.spinnerUppercaseNb = new JSpinner();
		JLabel labelUppercaseNb = new JLabel();
		JButton buttonUppercase = new JButton();
		this.scrollFiltre = new JScrollPane();
		JPanel panelFiltre = new JPanel();
		this.checkFiltreShowFiles = new JCheckBox();
		this.checkFiltreShowFolders = new JCheckBox();
		this.checkFiltreShow = new JCheckBox();
		this.textFiltreShow = new JTextField();
		this.checkFiltreHide = new JCheckBox();
		this.textFiltreHide = new JTextField();
		this.checkFilterCaseSensitive = new JCheckBox();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, $lcgap, default, $lcgap, default:grow(0.5), $lcgap, default:grow, $lcgap, default, $ugap",
			"$ugap, $lgap, fill:default, $lgap, fill:default:grow, $lgap, $ugap"));

		//======== menuBar ========

		//======== menuFichier ========
		menuFichier.setText("Fichier");
		menuFichier.setMnemonic('F');

		//---- menuFichierOuvrirDossier ----
		menuFichierOuvrirDossier.setText("Ouvrir dossier");
		menuFichierOuvrirDossier.setMnemonic('D');
		menuFichierOuvrirDossier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFichierOuvrirDossierActionPerformed(e);
			}
		});
		menuFichier.add(menuFichierOuvrirDossier);

		//---- menuFichierRollback ----
		menuFichierRollback.setText("Rollback");
		menuFichierRollback.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFichierRollbackActionPerformed(e);
			}
		});
		menuFichier.add(menuFichierRollback);
		menuFichier.addSeparator();

		//---- menuFichierQuitter ----
		menuFichierQuitter.setText("Quitter");
		menuFichierQuitter.setMnemonic('Q');
		menuFichierQuitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFichierQuitterActionPerformed(e);
			}
		});
		menuFichier.add(menuFichierQuitter);
		menuBar.add(menuFichier);
		setJMenuBar(menuBar);

		//---- labelPath ----
		labelPath.setText("Chemin");
		contentPane.add(labelPath, cc.xywh(3, 3, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

		//---- textPath ----
		this.textPath.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				textPathCaretUpdate(e);
			}
		});
		this.textPath.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textPathFocusGained(e);
			}
		});
		contentPane.add(this.textPath, cc.xywh(5, 3, 4, 1));

		//---- buttonRefresh ----
		buttonRefresh.setText("Actualiser");
		buttonRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonRefreshActionPerformed(e);
			}
		});
		contentPane.add(buttonRefresh, cc.xy(9, 3));

		//======== splitPane ========
		this.splitPane.setDividerLocation(500);

		//======== panelInOut ========
		panelInOut.setLayout(new FormLayout(
			"default:grow, $lcgap, pref:grow",
			"default, $lgap, fill:default:grow, $lgap, default, $lgap, 9dlu, $lgap, 10dlu"));
		((FormLayout)panelInOut.getLayout()).setColumnGroups(new int[][] {{1, 3}});

		//---- labelIn ----
		labelIn.setText("Entr\u00e9e");
		panelInOut.add(labelIn, cc.xy(1, 1));

		//---- labelOut ----
		labelOut.setText("Sortie");
		panelInOut.add(labelOut, cc.xy(3, 1));

		//======== scrollTextIn ========
		scrollTextIn.setViewportView(this.tableEntree);
		panelInOut.add(scrollTextIn, cc.xy(1, 3));

		//======== scrollTextOut ========

		//---- textOut ----
		this.textOut.setTabSize(2);
		this.textOut.setRows(20);
		scrollTextOut.setViewportView(this.textOut);
		panelInOut.add(scrollTextOut, cc.xy(3, 3));

		//---- checkAllCheck ----
		this.checkAllCheck.setText("Tout cocher / d\u00e9cocher");
		this.checkAllCheck.setSelected(true);
		this.checkAllCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				checkAllCheckItemStateChanged(e);
			}
		});
		panelInOut.add(this.checkAllCheck, cc.xy(1, 5));

		//---- buttonApply ----
		buttonApply.setText("Appliquer");
		buttonApply.setMnemonic('A');
		buttonApply.setToolTipText("Appliquer les changements aux fichiers et dossiers s\u00e9lectionn\u00e9s");
		buttonApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonApplyActionPerformed(e);
			}
		});
		panelInOut.add(buttonApply, cc.xy(3, 5));

		//---- checkInvertSelection ----
		this.checkInvertSelection.setText("Inverser");
		this.checkInvertSelection.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				checkInvertSelectionItemStateChanged(e);
			}
		});
		panelInOut.add(this.checkInvertSelection, cc.xy(1, 7));

		//---- checkExtAuto ----
		this.checkExtAuto.setText("Extenstion automatique");
		this.checkExtAuto.setToolTipText("Ne change pas le type des fichiers");
		panelInOut.add(this.checkExtAuto, cc.xy(3, 7));

		//======== panelClipboardCopy ========
		panelClipboardCopy.setLayout(new FormLayout(
			"50dlu, $lcgap, default:grow",
			"10dlu"));
		((FormLayout)panelClipboardCopy.getLayout()).setColumnGroups(new int[][] {{1, 3}});

		//---- buttonClipboardCopy ----
		buttonClipboardCopy.setText("Copier");
		buttonClipboardCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonClipboardCopyActionPerformed(e);
				buttonClipboardCopyActionPerformed(e);
				buttonClipboardCopyActionPerformed(e);
			}
		});
		panelClipboardCopy.add(buttonClipboardCopy, cc.xy(1, 1));

		//---- checkClipboardCopy ----
		this.checkClipboardCopy.setText("Avec l'extension");
		panelClipboardCopy.add(this.checkClipboardCopy, cc.xy(3, 1));
		panelInOut.add(panelClipboardCopy, cc.xy(1, 9));
		this.splitPane.setLeftComponent(panelInOut);

		//======== tabbedPaneOptions ========

		//======== scrollRename ========

		//======== panelRename ========
		panelRename.setLayout(new FormLayout(
			"$ugap, $lcgap, default, $lcgap, 59dlu, $lcgap, center:3dlu, $lcgap, 24dlu, $lcgap, 5dlu, $lcgap, default, $lcgap, $ugap",
			"$ugap, $lgap, default, $lgap, 9dlu, 3*($lgap, default), $lgap, default:grow, $lgap, $ugap"));

		//---- labelRenameText ----
		labelRenameText.setText("Texte");
		panelRename.add(labelRenameText, cc.xy(3, 3));

		//---- textRenameTexte ----
		this.textRenameTexte.setToolTipText("Texte que porteront en commun tout les fichier");
		panelRename.add(this.textRenameTexte, cc.xywh(5, 3, 5, 1));

		//---- labelExtension ----
		labelExtension.setText(".");
		panelRename.add(labelExtension, cc.xywh(11, 3, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));

		//---- textRenameExtension ----
		this.textRenameExtension.setToolTipText("Extension des fichiers (vide pour qu'ils ne changent pas)");
		panelRename.add(this.textRenameExtension, cc.xy(13, 3));
		panelRename.add(separatorRename1, cc.xywh(3, 5, 11, 1));

		//---- labelRenameCount ----
		labelRenameCount.setText("Compteur");
		panelRename.add(labelRenameCount, cc.xy(3, 7));

		//---- labelRenameStart ----
		labelRenameStart.setText("Commencer \u00e0");
		panelRename.add(labelRenameStart, cc.xywh(5, 7, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- spinnerRenameCountStart ----
		this.spinnerRenameCountStart.setModel(new SpinnerNumberModel(1, 0, 32767, 1));
		this.spinnerRenameCountStart.setFont(this.spinnerRenameCountStart.getFont().deriveFont(this.spinnerRenameCountStart.getFont().getStyle() & ~Font.BOLD));
		panelRename.add(this.spinnerRenameCountStart, cc.xy(9, 7));

		//---- separatorRename2 ----
		separatorRename2.setOrientation(SwingConstants.VERTICAL);
		panelRename.add(separatorRename2, cc.xywh(11, 7, 1, 5, CellConstraints.CENTER, CellConstraints.FILL));

		//---- radioRenameStart ----
		this.radioRenameStart.setText("Au d\u00e9but");
		panelRename.add(this.radioRenameStart, cc.xywh(13, 7, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

		//---- labelRenameInc ----
		labelRenameInc.setText("Incr\u00e9menter de");
		panelRename.add(labelRenameInc, cc.xywh(5, 9, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- spinnerRenameCountIncrement ----
		this.spinnerRenameCountIncrement.setModel(new SpinnerNumberModel(1, 1, 32767, 1));
		this.spinnerRenameCountIncrement.setFont(this.spinnerRenameCountIncrement.getFont().deriveFont(this.spinnerRenameCountIncrement.getFont().getStyle() & ~Font.BOLD));
		panelRename.add(this.spinnerRenameCountIncrement, cc.xy(9, 9));

		//---- radioRenameEnd ----
		this.radioRenameEnd.setText("A la fin");
		this.radioRenameEnd.setSelected(true);
		this.radioRenameEnd.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				radioRenameEndItemStateChanged(e);
			}
		});
		panelRename.add(this.radioRenameEnd, cc.xywh(13, 9, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

		//---- checkRenameCountPadding ----
		this.checkRenameCountPadding.setText("Bourrage");
		this.checkRenameCountPadding.setSelected(true);
		this.checkRenameCountPadding.setToolTipText("PLUS DE REMBOURAGE POUR LE BOURRAGE !!!");
		panelRename.add(this.checkRenameCountPadding, cc.xywh(5, 11, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- textRenameCountPadding ----
		this.textRenameCountPadding.setText("0");
		panelRename.add(this.textRenameCountPadding, cc.xy(9, 11));

		//======== panelRenameExt ========
		panelRenameExt.setLayout(new FormLayout(
			"$ugap, $lcgap, default",
			"default"));

		//---- checkRenameEndExt ----
		this.checkRenameEndExt.setText("De l'extension");
		panelRenameExt.add(this.checkRenameEndExt, cc.xy(3, 1));
		panelRename.add(panelRenameExt, cc.xy(13, 11));

		//---- buttonRename ----
		buttonRename.setText("Aper\u00e7u");
		buttonRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonRenameActionPerformed(e);
			}
		});
		panelRename.add(buttonRename, cc.xywh(3, 13, 1, 1, CellConstraints.LEFT, CellConstraints.BOTTOM));
		scrollRename.setViewportView(panelRename);
		tabbedPaneOptions.addTab("Renommer", scrollRename);


		//======== scrollAdd ========

		//======== panelAdd ========
		panelAdd.setLayout(new FormLayout(
			"$ugap, $lcgap, default, $lcgap, 38dlu, $lcgap, 26dlu, $lcgap, 3dlu, $lcgap, default, $lcgap, 38dlu, $lcgap, $ugap",
			"$ugap, $lgap, default, $lgap, 2dlu, $lgap, default, $lgap, 2dlu, 3*($lgap, default), $lgap, default:grow, $lgap, $ugap"));

		//---- radioAddText ----
		this.radioAddText.setText("Texte");
		this.radioAddText.setSelected(true);
		panelAdd.add(this.radioAddText, cc.xy(3, 3));
		panelAdd.add(this.textAddText, cc.xywh(5, 3, 3, 1));

		//---- separatorAdd2 ----
		this.separatorAdd2.setOrientation(SwingConstants.VERTICAL);
		panelAdd.add(this.separatorAdd2, cc.xywh(9, 3, 1, 11, CellConstraints.RIGHT, CellConstraints.FILL));
		panelAdd.add(separatorAdd1, cc.xywh(3, 5, 6, 1));

		//---- radioAddCount ----
		this.radioAddCount.setText("Compteur");
		panelAdd.add(this.radioAddCount, cc.xy(3, 7));

		//---- radioCountStart ----
		radioCountStart.setText("Commencer \u00e0");
		panelAdd.add(radioCountStart, cc.xywh(5, 7, 2, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- spinnerAddCountStart ----
		this.spinnerAddCountStart.setModel(new SpinnerNumberModel(1, 0, 32767, 1));
		this.spinnerAddCountStart.setFont(this.spinnerAddCountStart.getFont().deriveFont(this.spinnerAddCountStart.getFont().getStyle() & ~Font.BOLD));
		panelAdd.add(this.spinnerAddCountStart, cc.xy(7, 7));

		//---- labelAddPosition ----
		labelAddPosition.setText("En position");
		panelAdd.add(labelAddPosition, cc.xy(11, 7));

		//---- spinnerAddPosition ----
		this.spinnerAddPosition.setModel(new SpinnerNumberModel(0, 0, 32767, 1));
		this.spinnerAddPosition.setFont(this.spinnerAddPosition.getFont().deriveFont(this.spinnerAddPosition.getFont().getStyle() & ~Font.BOLD));
		panelAdd.add(this.spinnerAddPosition, cc.xy(13, 7));

		//---- labelAddInc ----
		labelAddInc.setText("Incr\u00e9menter de");
		panelAdd.add(labelAddInc, cc.xywh(5, 11, 2, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- spinnerAddCountInc ----
		this.spinnerAddCountInc.setModel(new SpinnerNumberModel(1, 1, 32767, 1));
		this.spinnerAddCountInc.setFont(this.spinnerAddCountInc.getFont().deriveFont(this.spinnerAddCountInc.getFont().getStyle() & ~Font.BOLD));
		panelAdd.add(this.spinnerAddCountInc, cc.xy(7, 11));

		//---- radioAddPositionStart ----
		this.radioAddPositionStart.setText("Depuis le d\u00e9but");
		this.radioAddPositionStart.setSelected(true);
		panelAdd.add(this.radioAddPositionStart, cc.xywh(11, 11, 3, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

		//---- checkAddCountPadding ----
		this.checkAddCountPadding.setText("Bourrage");
		this.checkAddCountPadding.setSelected(true);
		this.checkAddCountPadding.setToolTipText("PLUS DE REMBOURAGE POUR LE BOURRAGE !!!");
		panelAdd.add(this.checkAddCountPadding, cc.xywh(5, 13, 2, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- textAddCountPadding ----
		this.textAddCountPadding.setText("0");
		panelAdd.add(this.textAddCountPadding, cc.xy(7, 13));

		//---- radioAddPositionEnd ----
		this.radioAddPositionEnd.setText("Depuis la fin");
		this.radioAddPositionEnd.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				radioAjouterPositionFinItemStateChanged(e);
			}
		});
		panelAdd.add(this.radioAddPositionEnd, cc.xywh(11, 13, 3, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

		//======== panelAddExt ========
		panelAddExt.setLayout(new FormLayout(
			"$ugap, $lcgap, default:grow",
			"default"));

		//---- checkAddEndExt ----
		this.checkAddEndExt.setText("De l'extension");
		this.checkAddEndExt.setEnabled(false);
		panelAddExt.add(this.checkAddEndExt, cc.xy(3, 1));
		panelAdd.add(panelAddExt, cc.xywh(11, 15, 3, 1));

		//---- buttonAdd ----
		buttonAdd.setText("Aper\u00e7u");
		buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonAddActionPerformed(e);
			}
		});
		panelAdd.add(buttonAdd, cc.xywh(3, 17, 1, 1, CellConstraints.LEFT, CellConstraints.BOTTOM));
		this.scrollAdd.setViewportView(panelAdd);
		tabbedPaneOptions.addTab("Ajouter", this.scrollAdd);


		//======== scrollDelete ========

		//======== panelDelete ========
		panelDelete.setLayout(new FormLayout(
			"$ugap, $lcgap, left:57dlu, 6*($lcgap, default), $lcgap, $ugap",
			"$rgap, 2*($lgap, default), $lgap, 3dlu, $lgap, default, $lgap, 3dlu, 3*($lgap, default), $lgap, default:grow, $lgap, $ugap"));

		//---- spinnerDeleteRemove ----
		this.spinnerDeleteRemove.setFont(this.spinnerDeleteRemove.getFont().deriveFont(this.spinnerDeleteRemove.getFont().getStyle() & ~Font.BOLD));
		this.spinnerDeleteRemove.setModel(new SpinnerNumberModel(0, 0, 32767, 1));
		this.spinnerDeleteRemove.setMinimumSize(new Dimension(40, 20));
		this.spinnerDeleteRemove.setPreferredSize(new Dimension(40, 20));
		panelDelete.add(this.spinnerDeleteRemove, cc.xywh(5, 3, 1, 3, CellConstraints.DEFAULT, CellConstraints.CENTER));

		//---- radioDeleteRemoveFirst ----
		this.radioDeleteRemoveFirst.setText("Premiers caract\u00e8res");
		this.radioDeleteRemoveFirst.setMnemonic('P');
		this.radioDeleteRemoveFirst.setSelected(true);
		panelDelete.add(this.radioDeleteRemoveFirst, cc.xywh(7, 3, 11, 1));

		//---- radioDeleteRemoveLast ----
		this.radioDeleteRemoveLast.setText("Derniers caract\u00e8res");
		this.radioDeleteRemoveLast.setMnemonic('D');
		panelDelete.add(this.radioDeleteRemoveLast, cc.xywh(7, 5, 11, 1));
		panelDelete.add(separatorDelete1, cc.xywh(5, 7, 11, 1));

		//---- labelDeleteBetween ----
		labelDeleteBetween.setText("Caract\u00e8res compris entre");
		panelDelete.add(labelDeleteBetween, cc.xywh(3, 9, 3, 1));
		panelDelete.add(this.radioDeleteBetween, cc.xy(7, 9));

		//---- spinnerDeleteBetweenStart ----
		this.spinnerDeleteBetweenStart.setModel(new SpinnerNumberModel(0, 0, 32767, 1));
		this.spinnerDeleteBetweenStart.setPreferredSize(new Dimension(40, 20));
		this.spinnerDeleteBetweenStart.setMinimumSize(new Dimension(40, 20));
		this.spinnerDeleteBetweenStart.setFont(this.spinnerDeleteBetweenStart.getFont().deriveFont(this.spinnerDeleteBetweenStart.getFont().getStyle() & ~Font.BOLD));
		panelDelete.add(this.spinnerDeleteBetweenStart, cc.xy(9, 9));

		//---- labelDeleteAnd ----
		labelDeleteAnd.setText(" et ");
		panelDelete.add(labelDeleteAnd, cc.xy(11, 9));

		//---- spinnerDeleteBetweenEnd ----
		this.spinnerDeleteBetweenEnd.setModel(new SpinnerNumberModel(0, 0, 32767, 1));
		this.spinnerDeleteBetweenEnd.setMinimumSize(new Dimension(40, 20));
		this.spinnerDeleteBetweenEnd.setPreferredSize(new Dimension(40, 20));
		this.spinnerDeleteBetweenEnd.setFont(this.spinnerDeleteBetweenEnd.getFont().deriveFont(this.spinnerDeleteBetweenEnd.getFont().getStyle() & ~Font.BOLD));
		panelDelete.add(this.spinnerDeleteBetweenEnd, cc.xy(13, 9));

		//---- labelDeleteInclude ----
		labelDeleteInclude.setText(" (inclus)");
		panelDelete.add(labelDeleteInclude, cc.xy(15, 9));
		panelDelete.add(separatorDelete2, cc.xywh(5, 11, 11, 1));

		//---- labelDeleteSearch ----
		labelDeleteSearch.setText("Rechercher");
		panelDelete.add(labelDeleteSearch, cc.xywh(3, 13, 3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		panelDelete.add(this.radioDeleteSearch, cc.xy(7, 13));

		//---- textDeleteSearch ----
		this.textDeleteSearch.setColumns(13);
		panelDelete.add(this.textDeleteSearch, cc.xywh(9, 13, 5, 1));

		//---- checkDeleteRegex ----
		this.checkDeleteRegex.setText("Regex");
		this.checkDeleteRegex.setSelected(true);
		this.checkDeleteRegex.setToolTipText("Prends le texte \u00e0 rechercher comme une expression r\u00e9guli\u00e8re");
		panelDelete.add(this.checkDeleteRegex, cc.xy(15, 13));

		//---- labelDeleteReplace ----
		labelDeleteReplace.setText("Remplacer par");
		panelDelete.add(labelDeleteReplace, cc.xywh(3, 15, 3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- textDeleteReplace ----
		this.textDeleteReplace.setColumns(10);
		panelDelete.add(this.textDeleteReplace, cc.xywh(7, 15, 5, 1));

		//---- spinnerDeleteReplaceN ----
		this.spinnerDeleteReplaceN.setModel(new SpinnerNumberModel(0, 0, 32768, 1));
		this.spinnerDeleteReplaceN.setMinimumSize(new Dimension(40, 20));
		this.spinnerDeleteReplaceN.setPreferredSize(new Dimension(40, 20));
		this.spinnerDeleteReplaceN.setToolTipText("Remplacer seulement les 'N' premi\u00e8res s\u00e9quence. 0 = infini");
		panelDelete.add(this.spinnerDeleteReplaceN, cc.xy(13, 15));

		//---- labelDeleteNb ----
		labelDeleteNb.setText("fois");
		panelDelete.add(labelDeleteNb, cc.xy(15, 15));

		//---- checkDeleteCaseSensitive ----
		this.checkDeleteCaseSensitive.setText("Distinction minuscules / majuscules");
		this.checkDeleteCaseSensitive.setSelected(true);
		panelDelete.add(this.checkDeleteCaseSensitive, cc.xywh(5, 17, 11, 1));

		//---- buttonDelete ----
		buttonDelete.setText("Aper\u00e7u");
		buttonDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonDeleteActionPerformed(e);
			}
		});
		panelDelete.add(buttonDelete, cc.xywh(3, 19, 1, 1, CellConstraints.LEFT, CellConstraints.BOTTOM));
		this.scrollDelete.setViewportView(panelDelete);
		tabbedPaneOptions.addTab("Supprimer", this.scrollDelete);


		//======== scrollUppercase ========

		//======== panelUppercase ========
		panelUppercase.setLayout(new FormLayout(
			"$ugap, $lcgap, default:grow, $lcgap, 28dlu, $lcgap, 23dlu, $lcgap, 47dlu, $lcgap, $rgap",
			"$rgap, 6*($lgap, default), $lgap, 4dlu, $lgap, default, $lgap, default:grow, $lgap, $ugap"));

		//---- radioUppercaseFirstWord ----
		this.radioUppercaseFirstWord.setText("1er mot");
		this.radioUppercaseFirstWord.setSelected(true);
		panelUppercase.add(this.radioUppercaseFirstWord, cc.xywh(3, 3, 5, 1));

		//---- checkUppercaseFirstWord ----
		this.checkUppercaseFirstWord.setText("Uniquement");
		this.checkUppercaseFirstWord.setFont(this.checkUppercaseFirstWord.getFont().deriveFont(this.checkUppercaseFirstWord.getFont().getSize() - 3f));
		this.checkUppercaseFirstWord.setSelected(true);
		panelUppercase.add(this.checkUppercaseFirstWord, cc.xywh(9, 3, 2, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

		//---- radioUppercaseEveryWord ----
		this.radioUppercaseEveryWord.setText("Au d\u00e9but de chaque mot");
		panelUppercase.add(this.radioUppercaseEveryWord, cc.xywh(3, 5, 5, 1));

		//---- checkUppercaseEveryWord ----
		this.checkUppercaseEveryWord.setText("Uniquement");
		this.checkUppercaseEveryWord.setFont(this.checkUppercaseEveryWord.getFont().deriveFont(this.checkUppercaseEveryWord.getFont().getSize() - 3f));
		this.checkUppercaseEveryWord.setSelected(true);
		panelUppercase.add(this.checkUppercaseEveryWord, cc.xywh(9, 5, 2, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

		//---- radioUppercaseNowhere ----
		this.radioUppercaseNowhere.setText("Nul part");
		panelUppercase.add(this.radioUppercaseNowhere, cc.xywh(3, 7, 5, 1));

		//---- radioUppercaseEverywhere ----
		this.radioUppercaseEverywhere.setText("Partout");
		panelUppercase.add(this.radioUppercaseEverywhere, cc.xywh(3, 9, 5, 1));

		//---- radioUppercaseAfter ----
		this.radioUppercaseAfter.setText("Apr\u00e8s ");
		this.radioUppercaseAfter.setToolTipText("S\u00e9parez les termes apr\u00e8s lesquels vous voulez voir appara\u00eetre une majuscule par des virgules \",\" (\\t pour les tabulations et \\n pour les retour \u00e0 la ligne)");
		panelUppercase.add(this.radioUppercaseAfter, cc.xywh(3, 11, 2, 1));

		//---- textUppercaseAfter ----
		this.textUppercaseAfter.setColumns(5);
		this.textUppercaseAfter.setToolTipText("S\u00e9parez les termes apr\u00e8s lesquels vous voulez voir appara\u00eetre une majuscule par des slashs \"/\" (\\t pour les tabulations et \\n pour les retour \u00e0 la ligne)");
		panelUppercase.add(this.textUppercaseAfter, cc.xywh(5, 11, 3, 1));

		//---- checkUppercaseAfter ----
		this.checkUppercaseAfter.setText("Uniquement");
		this.checkUppercaseAfter.setFont(this.checkUppercaseAfter.getFont().deriveFont(this.checkUppercaseAfter.getFont().getSize() - 3f));
		this.checkUppercaseAfter.setSelected(true);
		panelUppercase.add(this.checkUppercaseAfter, cc.xywh(9, 11, 2, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

		//---- checkUppercaseCaseSensitive ----
		this.checkUppercaseCaseSensitive.setText("Distinction minuscule / majuscule");
		this.checkUppercaseCaseSensitive.setSelected(true);
		panelUppercase.add(this.checkUppercaseCaseSensitive, cc.xy(3, 13));
		panelUppercase.add(this.separatorUppercase1, cc.xywh(3, 15, 7, 1));

		//---- spinnerUppercaseNb ----
		this.spinnerUppercaseNb.setModel(new SpinnerNumberModel(0, 0, null, 1));
		this.spinnerUppercaseNb.setToolTipText("Remplacer seulement les 'N' premi\u00e8res s\u00e9quence. 0 = infini");
		panelUppercase.add(this.spinnerUppercaseNb, cc.xy(7, 17));

		//---- labelUppercaseNb ----
		labelUppercaseNb.setText("fois");
		panelUppercase.add(labelUppercaseNb, cc.xy(9, 17));

		//---- buttonUppercase ----
		buttonUppercase.setText("Aper\u00e7u");
		buttonUppercase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonUppercaseActionPerformed(e);
			}
		});
		panelUppercase.add(buttonUppercase, cc.xywh(3, 19, 2, 1, CellConstraints.LEFT, CellConstraints.BOTTOM));
		this.scrollUppercase.setViewportView(panelUppercase);
		tabbedPaneOptions.addTab("Majuscules", this.scrollUppercase);


		//======== scrollFiltre ========

		//======== panelFiltre ========
		panelFiltre.setLayout(new FormLayout(
			"$ugap, $lcgap, default, $lcgap, default:grow, $lcgap, $ugap",
			"$rgap, 5*($lgap, default), $lgap, $rgap"));

		//---- checkFiltreShowFiles ----
		this.checkFiltreShowFiles.setText("Voir les fichiers");
		this.checkFiltreShowFiles.setSelected(true);
		this.checkFiltreShowFiles.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				checkFilterItemStateChanged(e);
			}
		});
		panelFiltre.add(this.checkFiltreShowFiles, cc.xywh(3, 3, 4, 1));

		//---- checkFiltreShowFolders ----
		this.checkFiltreShowFolders.setText("Voir les dossiers");
		this.checkFiltreShowFolders.setSelected(true);
		this.checkFiltreShowFolders.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				checkFilterItemStateChanged(e);
			}
		});
		panelFiltre.add(this.checkFiltreShowFolders, cc.xywh(3, 5, 4, 1));

		//---- checkFiltreShow ----
		this.checkFiltreShow.setText("Afficher");
		this.checkFiltreShow.setToolTipText("S\u00e9parez les termes que vous voulez voir appara\u00eetre dans un nom de fichier par des slash \"/\"");
		this.checkFiltreShow.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				checkFilterItemStateChanged(e);
			}
		});
		panelFiltre.add(this.checkFiltreShow, cc.xy(3, 7));

		//---- textFiltreShow ----
		this.textFiltreShow.setColumns(4);
		this.textFiltreShow.setVerifyInputWhenFocusTarget(false);
		this.textFiltreShow.setToolTipText("S\u00e9parez les termes que vous voulez voir appara\u00eetre dans un nom de fichier par des slash \"/\"");
		this.textFiltreShow.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				textFiltreCaretUpdate(e);
			}
		});
		panelFiltre.add(this.textFiltreShow, cc.xywh(5, 7, 2, 1));

		//---- checkFiltreHide ----
		this.checkFiltreHide.setText("Masquer");
		this.checkFiltreHide.setToolTipText("S\u00e9parez les termes que vous ne voulez pas voir appara\u00eetre dans un nom de fichier par des slash \"/\"");
		this.checkFiltreHide.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				checkFilterItemStateChanged(e);
			}
		});
		panelFiltre.add(this.checkFiltreHide, cc.xy(3, 9));

		//---- textFiltreHide ----
		this.textFiltreHide.setColumns(4);
		this.textFiltreHide.setToolTipText("S\u00e9parez les termes que vous ne voulez pas voir appara\u00eetre dans un nom de fichier par des slash \"/\"");
		this.textFiltreHide.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				textFiltreCaretUpdate(e);
			}
		});
		panelFiltre.add(this.textFiltreHide, cc.xywh(5, 9, 2, 1));

		//---- checkFilterCaseSensitive ----
		this.checkFilterCaseSensitive.setText("Distinction minuscules / majuscules");
		this.checkFilterCaseSensitive.setSelected(true);
		this.checkFilterCaseSensitive.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				checkCaseSensitiveItemStateChanged(e);
			}
		});
		panelFiltre.add(this.checkFilterCaseSensitive, cc.xywh(3, 11, 3, 1));
		this.scrollFiltre.setViewportView(panelFiltre);
		tabbedPaneOptions.addTab("Filtre", this.scrollFiltre);

		this.splitPane.setRightComponent(tabbedPaneOptions);
		contentPane.add(this.splitPane, cc.xywh(3, 5, 8, 1));
		setSize(1130, 480);
		setLocationRelativeTo(getOwner());

		//---- buttonGroup7 ----
		ButtonGroup buttonGroup7 = new ButtonGroup();
		buttonGroup7.add(this.radioRenameStart);
		buttonGroup7.add(this.radioRenameEnd);

		//---- buttonGroup3 ----
		ButtonGroup buttonGroup3 = new ButtonGroup();
		buttonGroup3.add(this.radioAddText);
		buttonGroup3.add(this.radioAddCount);

		//---- buttonGroup6 ----
		ButtonGroup buttonGroup6 = new ButtonGroup();
		buttonGroup6.add(this.radioAddPositionStart);
		buttonGroup6.add(this.radioAddPositionEnd);

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(this.radioDeleteRemoveFirst);
		buttonGroup1.add(this.radioDeleteRemoveLast);
		buttonGroup1.add(this.radioDeleteBetween);
		buttonGroup1.add(this.radioDeleteSearch);

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(this.radioUppercaseFirstWord);
		buttonGroup2.add(this.radioUppercaseEveryWord);
		buttonGroup2.add(this.radioUppercaseNowhere);
		buttonGroup2.add(this.radioUppercaseEverywhere);
		buttonGroup2.add(this.radioUppercaseAfter);
		// JFormDesigner - End of component initialization //GEN-END:initComponents

		this.textRenameCountPadding.setMaxLenght(1);
		this.textAddCountPadding.setMaxLenght(1);

		// JTable
		this.tableEntreeModel = new FileModel();
		this.tableEntree.setModel(this.tableEntreeModel);
		this.tableEntree.setDragEnabled(true);
		this.tableEntree.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		this.tableEntree.addMouseListener(new MouseListener()
		{
			@Override public void mouseReleased(MouseEvent e){}			
			@Override public void mousePressed(MouseEvent e){}			
			@Override public void mouseExited(MouseEvent e){}			
			@Override public void mouseEntered(MouseEvent e){}
			
			@Override public void mouseClicked(MouseEvent e)
			{
				MainWindow.this.tableEntreeModel.setEditable(false); // Quand le user click pour quitter l'édition (ouverte par F2) ca rend la table non-editable
				
				if(e.getClickCount() != 2) // ! Double clique
					return;
				if(MainWindow.this.tableEntree.getSelectedColumn() != 1) // ! Double clique sur le nom (et pas la checkbox ni le type)
					return;
				
				String selectedName = (String)MainWindow.this.tableEntreeModel.getValueAt(MainWindow.this.tableEntree.getSelectedRow(), 1);
				String path = MainWindow.this.textPath.getText() + "\\" + selectedName;
				File testNewFolder = new File(path);
				if(testNewFolder.exists() && testNewFolder.isDirectory())
				{
					try
					{
						MainWindow.this.textPath.setText(testNewFolder.getCanonicalPath());
					}
					catch(IOException e1)
					{
					}
				}
			}
		});
		
		this.tableEntree.addKeyListener(new KeyListener()
		{
			@Override public void keyTyped(KeyEvent keyevent){}
			
			@Override public void keyPressed(KeyEvent keyevent) // Rend la jtable editable pour permettre à F2 d'éditer la cellule
			{
				if(keyevent.getKeyCode() == KeyEvent.VK_F2)
					MainWindow.this.tableEntreeModel.setEditable(true);
			}

			@Override public void keyReleased(KeyEvent keyevent)
			{
				MainWindow.this.tableEntreeModel.setEditable(false);
			}
		});
		
		TableColumnModel columnModel = this.tableEntree.getColumnModel();
		columnModel.getColumn(0).setMaxWidth(5);
		columnModel.getColumn(2).setMaxWidth(65);
	}
	
	private void bindComponents()
	{
		// Lier les checkbox / radiobox aux textfield correspondant
		BindFocus.bind(this.radioUppercaseAfter, this.textUppercaseAfter);
		BindFocus.bind(this.radioDeleteSearch, this.textDeleteReplace);
		BindFocus.bind(this.radioDeleteSearch, this.textDeleteSearch);
		BindFocus.bind(this.radioDeleteBetween, this.spinnerDeleteBetweenEnd);
		BindFocus.bind(this.radioDeleteBetween, this.spinnerDeleteBetweenStart);
		BindFocus.bind(this.radioDeleteRemoveFirst, this.spinnerDeleteRemove);
		BindFocus.bind(this.radioDeleteRemoveLast, this.spinnerDeleteRemove);
		BindFocus.bind(this.checkFiltreShow, this.textFiltreShow, false, true);
		BindFocus.bind(this.checkFiltreHide, this.textFiltreHide, false, true);
		BindFocus.bind(this.radioUppercaseFirstWord, this.checkUppercaseFirstWord, false, true);
		BindFocus.bind(this.radioUppercaseEveryWord, this.checkUppercaseEveryWord, false, true);
		BindFocus.bind(this.radioUppercaseAfter, this.checkUppercaseAfter, false, true);
		BindFocus.bind(this.checkAddCountPadding, this.textAddCountPadding, true, false);
		BindFocus.bind(this.radioAddText, this.textAddText);
		BindFocus.bind(this.radioAddCount, this.spinnerAddCountInc);
		BindFocus.bind(this.radioAddCount, this.spinnerAddCountStart);
		BindFocus.bind(this.radioAddCount, this.textAddCountPadding);
	}

	/* ********************************************************* *\
	|*					  Private attributs						 *|
	\* ********************************************************* */
	private String currentPath;
	
	private FileModel tableEntreeModel;
	//private JLimitedTextField textRenameCountPadding;

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JTextField textPath;
	private JSplitPane splitPane;
	private JTable tableEntree;
	private JTextArea textOut;
	private JCheckBox checkAllCheck;
	private JCheckBox checkInvertSelection;
	private JCheckBox checkExtAuto;
	private JCheckBox checkClipboardCopy;
	private JTextField textRenameTexte;
	private JTextField textRenameExtension;
	private JSpinner spinnerRenameCountStart;
	private JRadioButton radioRenameStart;
	private JSpinner spinnerRenameCountIncrement;
	private JRadioButton radioRenameEnd;
	private JCheckBox checkRenameCountPadding;
	private JLimitedTextField textRenameCountPadding;
	private JCheckBox checkRenameEndExt;
	private JScrollPane scrollAdd;
	private JRadioButton radioAddText;
	private JTextField textAddText;
	private JSeparator separatorAdd2;
	private JRadioButton radioAddCount;
	private JSpinner spinnerAddCountStart;
	private JSpinner spinnerAddPosition;
	private JSpinner spinnerAddCountInc;
	private JRadioButton radioAddPositionStart;
	private JCheckBox checkAddCountPadding;
	private JLimitedTextField textAddCountPadding;
	private JRadioButton radioAddPositionEnd;
	private JCheckBox checkAddEndExt;
	private JScrollPane scrollDelete;
	private JSpinner spinnerDeleteRemove;
	private JRadioButton radioDeleteRemoveFirst;
	private JRadioButton radioDeleteRemoveLast;
	private JRadioButton radioDeleteBetween;
	private JSpinner spinnerDeleteBetweenStart;
	private JSpinner spinnerDeleteBetweenEnd;
	private JRadioButton radioDeleteSearch;
	private JTextField textDeleteSearch;
	private JCheckBox checkDeleteRegex;
	private JTextField textDeleteReplace;
	private JSpinner spinnerDeleteReplaceN;
	private JCheckBox checkDeleteCaseSensitive;
	private JScrollPane scrollUppercase;
	private JRadioButton radioUppercaseFirstWord;
	private JCheckBox checkUppercaseFirstWord;
	private JRadioButton radioUppercaseEveryWord;
	private JCheckBox checkUppercaseEveryWord;
	private JRadioButton radioUppercaseNowhere;
	private JRadioButton radioUppercaseEverywhere;
	private JRadioButton radioUppercaseAfter;
	private JTextField textUppercaseAfter;
	private JCheckBox checkUppercaseAfter;
	private JCheckBox checkUppercaseCaseSensitive;
	private JSeparator separatorUppercase1;
	private JSpinner spinnerUppercaseNb;
	private JScrollPane scrollFiltre;
	private JCheckBox checkFiltreShowFiles;
	private JCheckBox checkFiltreShowFolders;
	private JCheckBox checkFiltreShow;
	private JTextField textFiltreShow;
	private JCheckBox checkFiltreHide;
	private JTextField textFiltreHide;
	private JCheckBox checkFilterCaseSensitive;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	/* ******************** *\
	|* 		Statiques		*|
	\* ******************** */
	private final String deprecatedMessage = "L'utilisation des caractères suivants sont déconseillé dans les noms de fichier :\n" + FileNameHelper.getDeprecatedChar();
	private final String deprecatedTitle = "Caractères déconseillés";
	private static final long serialVersionUID = -5067768471819464567L;
}
