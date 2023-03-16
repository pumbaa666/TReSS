package ch.correvon.tress.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.correvon.tress.windows.mainWindow.FileType;

// TODO voir pour déplacer la plupart des fonctions
public class ListHelper
{
	/* ***************** *\
	|* 		Remove		 *|
	\* ***************** */
	/**
	 * Remove the @param nbFirstChar first char of a list of files
	 * @param nbFirstChar
	 * @param files
	 */
	public static void removeFirsts(int nbFirstChar, ArrayList<FileType> files)
	{
		String fileName;
		int fileNameLenght;

		for(FileType file:files)
		{
			fileName = file.getName();
			fileNameLenght = fileName.length();
			if(fileNameLenght > nbFirstChar)
				file.setName(fileName.substring(nbFirstChar, fileName.length()));
		}
	}

	/**
	 * Remove the @param nbLastChar last char of a list of files
	 * @param nbFirstChar
	 * @param files
	 */
	public static void removeLasts(int nbLastChar, ArrayList<FileType> files)
	{
		String fileName;
		int fileNameLenght;

		for(FileType file:files)
		{
			fileName = file.getName();
			fileNameLenght = fileName.length();
			if(fileNameLenght > nbLastChar)
				file.setName(fileName.substring(0, fileName.length() - nbLastChar));
		}
	}

	/**
	 * Remove char between char number @param startChar and @param stopChar of a list of file 
	 * @param startChar
	 * @param stopChar
	 * @param files
	 */
	public static void removeBetween(int startChar, int stopChar, ArrayList<FileType> files)
	{
		String fileName;
		String fileNameFinal;
		int fileNameLenght;

		for(FileType file:files)
		{
			fileName = file.getName();
			fileNameFinal = fileName;
			fileNameLenght = fileName.length();

			if(fileNameLenght > startChar)
				fileNameFinal = fileName.substring(0, startChar);

			if(fileNameLenght > stopChar)
				fileNameFinal += fileName.substring(stopChar + 1, fileName.length());
			
			file.setName(fileNameFinal);
		}
	}

	/**
	 * Search and replace
	 * @param files
	 * @param find
	 * @param replace
	 * @param nbMaxReplace
	 * @param caseSensitive
	 * @param isRegex
	 */
	public static void removeSearch(ArrayList<FileType> files, String find, String replace, int nbMaxReplace, boolean caseSensitive, boolean isRegex)
	{
		// Comme j'utilise de toute façon la méthode de remplacement utilisant une regex de java, il faut échapper tout les caractères spécieux de regex dans le cas où l'utilisateur ne veut pas utiliser une regex
		if(!isRegex)
			for(byte c:REGEX_CHAR.getBytes())
				find = find.replace(""+(char)c, "\\"+(char)c);
		
		if(!caseSensitive)
			find = find.toLowerCase();
		
		List<MatchesBound> indices;
		String fileName;
		String fileNameCase;
		String newName;
		int nbCurrentReplacement;
		int lastI;
		Pattern patternReplace = Pattern.compile(find);
		
		for(FileType file:files)
		{
			// Init
			fileName = file.getName();
			fileNameCase = fileName;
			if(!caseSensitive)
				fileNameCase = fileNameCase.toLowerCase();
			indices = new ArrayList<MatchesBound>();
			nbCurrentReplacement = 0;
			
			// Recherche des éléments à remplacer
			Matcher matcherReplace = patternReplace.matcher(fileNameCase);
			while(matcherReplace.find() && (nbMaxReplace == 0 || nbCurrentReplacement < nbMaxReplace))
			{
				indices.add(new MatchesBound(matcherReplace.start(), matcherReplace.end()));
				nbCurrentReplacement++;
			}
			
			// Remplacement
			newName = "";
			lastI = 0;
			for(MatchesBound i:indices)
			{
				newName += fileName.substring(lastI, i.getStart()) + replace;
				lastI = i.getEnd();
			}
			
			// Dernier remplacement
			if(lastI == 0)
				continue;
			newName += fileName.substring(lastI);
			file.setName(newName);
		}
	}
	
	/* ***************** *\
	|* 		Uppercase	 *|
	\* ***************** */
	/**
	 * Convert filename to uppercase 
	 * @param files 
	 */
	public static void uppercaseEverywhere(ArrayList<FileType> files)
	{
		for(FileType file:files)
			file.setName(file.getName().toUpperCase());
	}

	/**
	 * Convert filename to lowercase 
	 * @param files 
	 */
	public static void uppercaseNowhere(ArrayList<FileType> files)
	{
		for(FileType file:files)
			file.setName(file.getName().toLowerCase());
	}

	/**
	 * Convert the first char of files to uppercase
	 * @param files
	 * @param majAfterUnique If true, convert every other char to lowercase
	 */
	public static void uppercaseFirstWord(ArrayList<FileType> files, boolean majAfterUnique)
	{
		String fileName;
		Pattern pattern = Pattern.compile("(^| )[a-zA-Z]*( |$|[\\.!]*[a-zA-Z]*$)");
		//Pattern pattern = Pattern.compile("(^[a-zA-Z]* )|( [a-zA-Z]* )");
		Matcher matcher;
		int spaceCount;
		String space;
		for(FileType file:files)
		{
			fileName = file.getName();
			if(majAfterUnique)
				fileName = fileName.toLowerCase();
			matcher = pattern.matcher(fileName);
			while(matcher.find())
			{
				spaceCount = 0;
				space = "";
				String firstWord = matcher.group();
				if(firstWord.startsWith(" "))
				{
					spaceCount = 1;
					space = " ";
				}
				String newFirstWord = space+firstWord.substring(0+spaceCount, 1+spaceCount).toUpperCase() + firstWord.substring(1+spaceCount);
				int firstWordIndex = fileName.indexOf(firstWord);
				String newFileName = fileName.substring(0, firstWordIndex) + newFirstWord + fileName.substring(firstWordIndex + firstWord.length());
				file.setName(newFileName);
				break;
			}
		}
	}

	/**
	 * Convert the first char of each word files to uppercase
	 * @param files
	 * @param majAfterUnique If true, convert every other char to lowercase
	 */
	public static void uppercaseWord(ArrayList<FileType> files, boolean majAfterUnique, int nbReplacement)
	{
		ArrayList<String> sequence = new ArrayList<String>(3);
		sequence.add("\t");
		sequence.add(" ");

		uppercaseAfter(files, sequence, true, majAfterUnique, nbReplacement, true);
	}

	/**
	 * Every char following a char of @param sequence are converted to uppercase 
	 * @param files
	 * @param sequence
	 * @param changeFirstChar
	 * @param majAfterUnique If true, convert every other char to lowercase
	 */
	public static void uppercaseAfter(ArrayList<FileType> files, ArrayList<String> sequence, boolean changeFirstChar, boolean majAfterUnique, int nbReplacement, boolean caseSensitive)
	{
		char[] charArray;
		char currentChar;
		int charArrayLength;
		String fileName;
		int nbCurrentReplacement;

		for(FileType file:files)
		{
			nbCurrentReplacement = 0;
			boolean change = changeFirstChar;
			charArray = file.getName().toCharArray();
			fileName = "";
			charArrayLength = charArray.length;

			for(int i = 0; i < charArrayLength; i++)
			{
				currentChar = charArray[i];
				if(change)
				{
					fileName += ("" + currentChar).toUpperCase();
					nbCurrentReplacement++;
					if(!isCharInSequence(currentChar, sequence, caseSensitive))
						change = false;
				}
				else
				{
					if(majAfterUnique)
						fileName += ("" + currentChar).toLowerCase();
					else
						fileName += ("" + currentChar);
					change = isCharInSequence(currentChar, sequence, caseSensitive) && (nbCurrentReplacement < nbReplacement || nbReplacement == 0); // nbReplacement = 0 --> infini
				}
			}

			file.setName(fileName);
		}
	}

	/**
	 * Seek a char in a sequence of char
	 * @param character The char to find
	 * @param sequence The sequence of char to analyze
	 */
	private static boolean isCharInSequence(char character, ArrayList<String> sequence, boolean caseSensitive)
	{
		if(caseSensitive)
		{
			for(String separator:sequence)
				if(separator.equals("" + character))
					return true;
		}
		else
		{
			for(String separator:sequence)
				if(separator.toLowerCase().equals(("" + character).toLowerCase()))
					return true;
		}

		return false;
	}
	
	public static List<FileType> copyList(List<FileType> source)
	{
		List<FileType> dest = new ArrayList<FileType>(source.size());
		dest.clear();
		for(FileType file:source)
			dest.add(file);
		return dest;
	}
	
	private static String REGEX_CHAR = "\\[]^.*?-$+|()";
}
