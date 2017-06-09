package zx81.pack;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ZX81ROM
{		

	private static String ROMLoc=".\\zx81rom.bin";
	public byte zx81ROMbin[];
	
	public ZX81ROM()
	{				
		zx81ROMbin= new byte[1024*8];
		System.out.println("Load ROM from "+ROMLoc);
		File file = new File(ROMLoc);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fis.read(zx81ROMbin);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
