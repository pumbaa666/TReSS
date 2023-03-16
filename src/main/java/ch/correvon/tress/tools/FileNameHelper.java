package ch.correvon.tress.tools;



public class FileNameHelper
{
	/**
	 * Test si le nom de fichier est valide, donc si il ne contient pas de caractère interdits.
	 * @param fileName
	 * @return
	 */
	public static boolean isFileNameValid(String fileName)
	{
		for(CharCorrespondance deprecatedChar:DEPRECATED_CHARS)
			if(fileName.contains(""+deprecatedChar.getFileRepresentation()))
				return false;
		return true;
	}
	
	public static String getDeprecatedChar()
	{
		StringBuilder stringBuilder = new StringBuilder(DEPRECATED_CHARS.length);
		
		for(CharCorrespondance deprecatedChar:DEPRECATED_CHARS)
			stringBuilder.append(deprecatedChar.getStringRepresentation()+" ");
		
		return stringBuilder.toString();
	}
	
	public static final CharCorrespondance[] DEPRECATED_CHARS =
	{
		new CharCorrespondance("/", '/'),
		new CharCorrespondance("\\", '\\'),
		new CharCorrespondance(":", ':'),
		new CharCorrespondance("*", '*'),
		new CharCorrespondance("?", '?'),
		new CharCorrespondance("\"", '"'),
		new CharCorrespondance("<", '<'),
		new CharCorrespondance(">", '>'),
		new CharCorrespondance("|", '|'),
		new CharCorrespondance("\\0", '\0'),
		new CharCorrespondance("\\n", '\n'),
		new CharCorrespondance("\\t", '\t')
	};
}
