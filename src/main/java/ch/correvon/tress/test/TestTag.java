package ch.correvon.tress.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.TagNotFoundException;
import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.lyrics3.Lyrics3v1;

public class TestTag
{
	public static void main(String[] args)
	{
		System.out.println("test tag");
	    File sourceFile = new File("C:\\Temp\\30 seconds to mars\\2002 - 30 seconds to mars\\01 - 30 Seconds To Mars - Capricorn (A Brand New Name).mp3");
		try
		{
		    MP3File mp3file = new MP3File(sourceFile);
//		    ID3v1 tag = mp3file.getID3v1Tag();
//		    tag.setArtist("30 seconds to mars");
//		    mp3file.save();
		    
		    ID3v2_2 tag4 = new ID3v2_2();
		    tag4.setAlbumTitle("albooom222");
		    mp3file.setID3v2Tag(tag4);
		    mp3file.save();
		    
		    Lyrics3v1 lyrics3 = new Lyrics3v1();
		    lyrics3.setLyric("lyr3v1");
		    mp3file.setLyrics3Tag(lyrics3);
		    mp3file.save();
		}
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch(TagNotFoundException tnfe)
		{
			tnfe.printStackTrace();
		}
		catch(TagException te)
		{
			te.printStackTrace();
		}
		
		System.out.println("end");
	}
	
	private static void removeID3v1(MP3File mp3File)
	{
		// TODO
	}

	private static void removeID3v2(MP3File mp3File)
	{
		// TODO
	}
	
	private static void removeLyrics3v1(MP3File mp3File)
	{
		// TODO
	}
	
	private static AbstractID3v1 copyID3v2ToId3v1(AbstractID3v2 id3v2)
	{
		// TODO
	    ID3v1 tag = new ID3v1();
	    tag.setArtist("30 seconds to mars");
	    return tag;
	}
}
