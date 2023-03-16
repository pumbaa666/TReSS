package ch.correvon.tress.tools;

/**
 * Map java String with file representation
 * 
 * @author lco
 *
 */
public class CharCorrespondance
{
	/* ------------------------------------------------------------ *\
	|* 		  				Constructeur							*|
	\* ------------------------------------------------------------ */
	public CharCorrespondance(String stringRepresentation, char charRepresentation)
	{
		this(stringRepresentation, ""+charRepresentation);
	}
	
	public CharCorrespondance(String stringRepresentation, String fileRepresentation)
	{
		this.stringRepresentation = stringRepresentation;
		this.fileRepresentation = fileRepresentation;
	}
	
	/* ------------------------------------------------------------ *\
	|* 		  				Méthodes publiques						*|
	\* ------------------------------------------------------------ */
	/* ----------------------------- *\
	|* 				Get 			 *|
	\* ----------------------------- */
	public String getStringRepresentation()
	{
		return this.stringRepresentation;
	}
	
	public String getFileRepresentation()
	{
		return this.fileRepresentation;
	}
	
	/* ------------------------------------------------------------ *\
	|* 		  				Attributs privés						*|
	\* ------------------------------------------------------------ */
	private String stringRepresentation;
	private String fileRepresentation;
}
