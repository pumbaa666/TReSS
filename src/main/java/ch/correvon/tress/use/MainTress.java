package ch.correvon.tress.use;

import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.correvon.tress.windows.mainWindow.MainWindow;

public class MainTress
{
	private static Log s_logger = LogFactory.getLog(MainTress.class);
	public static final String APPLICATION_NAME = "Tout REnommer Sans Soucis";
	private static final String VERSION = "0.8.1";

	public static final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".tress/history");
	
	public static void main(String[] args)
	{
		s_logger.info("Démarrage du programme");
		new MainWindow(APPLICATION_NAME + " (" + VERSION + ")").run();
	}
	
	/* 
		TODO
		System.err et System.out en s_logger
		
		Refactorer : mettre les méthodes touchant aux listes de nom de fichier dans le ListHelper
		Refactorer : Un .jfd par tabpan ?
		Fichier de config (taille et position de la fenêtre, paramètre préférés, ...)
		Appliquer à tout les sous-dossiers
	 */
}
