

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Series 
{
	private ArrayList<Episode>	Videos = new ArrayList<Episode>(); // a dynamic array of the episodes
	private File 				sPath; // series path
	private MKVMergePaths 		Paths; 
	private	Boolean				extSubs;
	private	ArrayList<File>		extSubList = new ArrayList<File>();
	
	public	Integer				EpCount;
	public	ArrayList< ArrayList<TrackInfo> >	EpTrackList;
	public	ArrayList< String >					EpArgList;
	
	public	File getPath() { return sPath; }
	
	public void display()
	{
		for(Integer iCount = 0; iCount < Videos.size();iCount++)
		{
			System.out.println(Videos.get(iCount).getEpPath().getName());
		}
	}
	
	public	ArrayList< ArrayList<TrackInfo> >	getEpTrackList()	{ return EpTrackList; }
	public	ArrayList< String >					getEpArgList()		{ return EpArgList; }
	
	//al and tl are the already existing argument list and track list
	public void getCommands(File batchPath, ArrayList< ArrayList<TrackInfo> > tl, ArrayList< String >	al)
	{
		/*
		//first addition to track list
		if(tl.isEmpty() || al.isEmpty())
		{
			System.out.println("New Track Configuration: " + Videos.get(0).getEpPath().getName());
			String FileArguments = Videos.get(0).makeArguments(); // gets the arguments for first track configuration
			al.add(FileArguments);
			tl.add(Videos.get(0).getTrackList());
		}
		*/
				
		// add already existing configurations to the list 
		EpTrackList	= tl; 
		EpArgList	= al;
		
		PrintWriter fileOut;
		try
		{
			
			Boolean TrackFound = false;
			//gets commands for each episode and writes them to a batch file
			for(Integer iCount = 0; iCount < Videos.size(); iCount++)
			{
				TrackFound = false;
				if(EpTrackList.isEmpty() == false)
				{
					//compares every episode to the track list
					for(Integer it = 0; it < EpTrackList.size(); it++)
					{
						
						if(Videos.get(iCount).TrackListEquals(EpTrackList.get(it)))
						{
							if(extSubs == true)
							{
								
								for(Integer subit = 0; it < (( extSubList.size() <= EpTrackList.size())? extSubList.size(): EpTrackList.size()); it++) // uses ternary operator to choose the smallest value
								{
									if(extSubList.get(subit).getName().substring(0, extSubList.get(subit).getName().lastIndexOf(".")).equals(Videos.get(subit).getEpPath().getName().substring(0, Videos.get(it).getEpPath().getName().lastIndexOf("."))))
									{
										fileOut = new PrintWriter(new BufferedWriter(new FileWriter(batchPath, true))); // writes command to a batch file
										fileOut.println(Videos.get(iCount).makeCommand(Paths.cmdPath.getPath(), (Paths.outPath.getPath() + "\\" + sPath.getName()), EpArgList.get(it), extSubList.get(iCount)));
										fileOut.close();
										break;
									}
									else
									{
										fileOut = new PrintWriter(new BufferedWriter(new FileWriter(batchPath, true))); // writes command to a batch file
										fileOut.println(Videos.get(iCount).makeCommand(Paths.cmdPath.getPath(), (Paths.outPath.getPath() + "\\" + sPath.getName()), EpArgList.get(it)));
										fileOut.close();
									}
								}
							}
							else
							{
								fileOut = new PrintWriter(new BufferedWriter(new FileWriter(batchPath, true))); // writes command to a batch file
								fileOut.println(Videos.get(iCount).makeCommand(Paths.cmdPath.getPath(), (Paths.outPath.getPath() + "\\" + sPath.getName()), EpArgList.get(it)));
								fileOut.close();
							}
							TrackFound = true;
							break;
						}
					}
				}
				if(TrackFound == false)
				{
					
					//if there is no match then the track list is different
					String FileArguments;
					
					System.out.println();
					System.out.println();
					System.out.println("New Track Configuration: " + Videos.get(iCount).getEpPath().getName());
					System.out.println();
					FileArguments = Videos.get(iCount).makeArguments();
					
					Scanner stdIn = new Scanner(System.in);
					String sTemp;
					System.out.print("Save Track Configuration(y/n)[y]?");
					sTemp = stdIn.nextLine();
					
					if(sTemp.equalsIgnoreCase("y") || sTemp.equalsIgnoreCase(""))
					{
						EpTrackList.add(Videos.get(iCount).getTrackList());
						EpArgList.add(FileArguments);
					}
					if(extSubs == true)
					{
						
						for(Integer it = 0; it < (( extSubList.size() <= EpTrackList.size())? extSubList.size(): EpTrackList.size()); it++) // uses ternary operator to choose the smallest value
						{
							if(extSubList.get(it).getName().substring(0, extSubList.get(it).getName().lastIndexOf(".")).equals(Videos.get(it).getEpPath().getName().substring(0, Videos.get(it).getEpPath().getName().lastIndexOf("."))))
							{
								fileOut = new PrintWriter(new BufferedWriter(new FileWriter(batchPath, true))); // writes command to a batch file
								fileOut.println(Videos.get(iCount).makeCommand(Paths.cmdPath.getPath(), (Paths.outPath.getPath() + "\\" + sPath.getName()), FileArguments, extSubList.get(iCount)));
								fileOut.close();
								break;
							}
							else
							{
								fileOut = new PrintWriter(new BufferedWriter(new FileWriter(batchPath, true))); // writes command to a batch file
								fileOut.println(Videos.get(iCount).makeCommand(Paths.cmdPath.getPath(), (Paths.outPath.getPath() + "\\" + sPath.getName()), FileArguments));
								fileOut.close();
							}
						}
						
					}
					else
					{
						fileOut = new PrintWriter(new BufferedWriter(new FileWriter(batchPath, true))); // writes command to a batch file
						
						fileOut.println(Videos.get(iCount).makeCommand(Paths.cmdPath.getPath(), (Paths.outPath.getPath() + "\\" + sPath.getName()), FileArguments));
						fileOut.close();
					}
				}
				
			}
		}
		catch(IOException e)
		{
			System.out.println("Error "+ e.getMessage());
		}
	}
	
	
	
	Series(String SeriesPath, MKVMergePaths p)
	{
		Paths = p;
		
		sPath = new File(SeriesPath);
		
		File[] Files;
		Files = sPath.listFiles(); //gets a list of all files in the path
		
		System.out.println("Getting track info for: " + sPath.getName());
		
		extSubs = false;
		
		//weeds out all compatible files and puts them in a different container
		for(Integer iCount = 0;iCount < Files.length ;iCount++)
		{
			
			if (Files[iCount].getName().endsWith(".mkv") || Files[iCount].getName().endsWith(".MKV")
			 || Files[iCount].getName().endsWith(".ogm") || Files[iCount].getName().endsWith(".OGM"))
			{
				System.out.println(Files[iCount].getName());
				Videos.add(new Episode(Files[iCount].getPath()));
			}
			else if(Files[iCount].getName().endsWith(".ssa") || Files[iCount].getName().endsWith(".ass")
				|| Files[iCount].getName().endsWith(".srt"))
			{
				extSubList.add(new File(Files[iCount].getPath()));
				extSubs = true;
			}
			else
			{
				extSubs = false;
			}
		}
		
		EpCount = Videos.size(); // gets the episode count of the series
		
		//gets track information for every file
		for(Integer iCount = 0; iCount < Videos.size() ; iCount++)
		{
			Videos.get(iCount).getTracks(Paths.cmdPath.getPath());
		}
		
	}
	
}