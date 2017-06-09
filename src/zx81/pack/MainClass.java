package zx81.pack;


public class MainClass {

	
	public static void main(String[] args) {

		
		ZX81Hires zx81Hires = new ZX81Hires();
		
//zx81Hires.getHDisplay(< file name of 256x128 black and white GIF >);
// will generate bytes for psuedo hires ZX81 screen
		
//zx81Hires.getDisplay(< file name of 64x48 black and white GIF>);
// will out bytes for a standard ZX81 lowres screen

		
//		zx81Hires.getHDisplay("c:\\emus\\zx81\\asm\\pacman\\workMazeGif.gif");		
		zx81Hires.getDisplay("c:\\emus\\zx81\\asm\\pacman\\lowResPac1.gif");
			
	}

}
