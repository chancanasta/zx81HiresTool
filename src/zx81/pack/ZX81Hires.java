package zx81.pack;

public class ZX81Hires{
	
	private ZX81ROM zx81ROM=null;
	private final static int GoodIDX[]={0,2,6,8,10,12,14,18,20,22,26,28,-1};
	private final static int PlotGFX[]={0,135,4,131,2,133,6,129,1,134,5,130,3,132,7,128};
	
	public int getVal(int intOffset,int idx)
	{
		return getVal(intOffset,idx,false);	
	}
	
	public int getVal(int intOffset,int idx,boolean invert)
	{
		int retVal=0;
		
		retVal=(0xff &((int)zx81ROM.zx81ROMbin[(256*intOffset)+(8*idx)]));
		if(invert)
			retVal=(~retVal)&0xff;
		
		return retVal;
	}
	public ZX81Hires()
	{
		zx81ROM=new ZX81ROM();
	}
	
	public int matchBitPattern(int inPattern,int intOffset)
	{
		return matchBitPattern(inPattern,intOffset,false);
	}
	
	public int matchBitPattern(int inPattern,int intOffset,boolean exactOnly)
	{
		int i;
		
//first, be very lazy - see if the 8bit pattern matches anything exactly
		for(i=0;i<64;i++)
		{
			if(getVal(intOffset,i)==inPattern)
				return i;
			if(getVal(intOffset,i,true)==inPattern)
				return (128+i);
		}
//if we got to here and exact match is on - exit		
		if(exactOnly)
			return -1;
//ok - got to here, nothing exact so find the closest bit match
		int bestMatch=0;
		int bestIdx=0;
			
		for(i=0;i<64;i++)
		{
			int thisVal=getVal(intOffset,i);
			int workMatch=0;
			int workBit=128;
			for(int j=0;j<8;j++)
			{
				int val1=thisVal&workBit;
				int val2=inPattern&workBit;
				if(val1==val2)
					workMatch++;
				
				workBit=workBit/2;
			}
			if(workMatch>bestMatch)
			{
				bestMatch=workMatch;
				bestIdx=i;
			}
		}
		
		for(i=0;i<64;i++)
		{
			int thisVal=getVal(intOffset,i,true);
			int workMatch=0;
			int workBit=128;
			
			for(int j=0;j<8;j++)
			{
				int val1=thisVal&workBit;
				int val2=inPattern&workBit;
				if(val1==val2)
					workMatch++;
				
				workBit=workBit/2;
			}
			if(workMatch>bestMatch)
			{
				bestMatch=workMatch;
				bestIdx=i+128;
			}
		}				
		return bestIdx;
	}
	
	
	public int getBestIdx(int workImage[][][])
	{
		int retVal=0;
		int bestCount=0;
		int chkPos=0;
		int checkIdx;
		checkIdx=GoodIDX[chkPos++];
				
		while(checkIdx>=0)
		{
			System.out.println("check: "+checkIdx);
			int thisCount=0;
			for(int y=0;y<192;y++)
			{
				for(int x=0;x<256;x+=8)
				{
					int bitPos=128;
					int workByte=0;
//create a byte from the 8 pixels				
					for(int i=0;i<8;i++)
					{
						int work=workImage[y][x+i][0];
						if(work!=255)
							workByte|=bitPos;
						bitPos=bitPos/2;
					}
					int outPattern=0;
				//find the nearest ZX81 pattern match				
					outPattern=matchBitPattern(workByte,checkIdx,true);
					if(outPattern!=-1)
						thisCount++;
				}
			}
			if(thisCount>bestCount)
			{
				bestCount=thisCount;
				retVal=checkIdx;
			}
			checkIdx=GoodIDX[chkPos++];
		}
		return retVal;
	}

	public void getDisplay(String fileName)
	{
		System.out.println("Image generation from "+fileName);
		int workImage[][][]=imageIO.loadImage(fileName);		
		System.out.println(workImage.length+" , "+workImage[0].length+" , "+workImage[1].length);
		
//loop through the image
		System.out.println("Display      DEFB $76");
		for(int y=0;y<48;y+=2)
		{
			System.out.print("             DEFB ");
			for(int x=0;x<64;x+=2)
			{
				
//get the 2x2 pixel grid
				int bitpos=8;
				int outNo=0;
				for(int i=0;i<2;i++)
				{
					for(int j=0;j<2;j++)
					{
						int work=workImage[y+i][x+j][0];
						if(work!=255)
							outNo|=bitpos;
						bitpos/=2;
					}
				}
				int outPattern=PlotGFX[outNo];
				String outByte=Integer.toHexString(outPattern);
				

				if(outByte.length()<2)
					outByte="$0"+outByte;
				else
					outByte="$"+outByte;
				System.out.print(outByte+",");

			}
			System.out.println("$76");
				
		}
		
	}
	
	public void getHDisplay(String fileName)
	{
		System.out.println("Image generation from "+fileName);
		int workImage[][][]=imageIO.loadImage(fileName);		
		System.out.println(workImage.length+" , "+workImage[0].length+" , "+workImage[1].length);
		int bestIdx=getBestIdx(workImage);

		bestIdx=14;
		
		String outIdx=Integer.toHexString(bestIdx);
		if(outIdx.length()<2)
			outIdx="$0"+outIdx;
		else
			outIdx="$"+outIdx;
		
		System.out.println("IDX : "+bestIdx+" , "+outIdx);
//loop through the image
		System.out.print("HDISPLAY    ");
		for(int y=0;y<192;y++)
		{
			if(y==0)				
				System.out.print("DEFB ");
			else
				System.out.print("            DEFB ");
			for(int x=0;x<256;x+=8)
			{
				int bitPos=128;
				int workByte=0;
//create a byte from the 8 pixels				
				for(int i=0;i<8;i++)
				{
					int work=workImage[y][x+i][0];
					if(work!=255)
						workByte|=bitPos;
					bitPos=bitPos/2;
				}
				int outPattern=0;
//find the nearest ZX81 pattern match				
				outPattern=matchBitPattern(workByte,bestIdx);
				String outByte=Integer.toHexString(outPattern);
				if(outByte.length()<2)
					outByte="$0"+outByte;
				else
					outByte="$"+outByte;
				System.out.print(outByte+",");
			}
			System.out.println("$c9");
		}
	}
	
	
}