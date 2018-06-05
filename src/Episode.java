/*
 * Contains information about the episodes in a series
 * */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Episode 
{
	private File EpPath; // path of episode
	private ArrayList<TrackInfo> TrackList = new ArrayList<TrackInfo>(); // an array of tracks contained in each episode file
	
	Episode(String EpisodePath)
	{
		EpPath = new File(EpisodePath);
	}
	
	public	File					getEpPath()		{ return EpPath;  }
	public	ArrayList<TrackInfo>	getTrackList()	{ return TrackList; }
	public	Boolean					TrackListEquals(ArrayList<TrackInfo> t)
	{
		if(TrackList.size() != t.size())
		{
			return false;
		}
		else
		{
			for(Integer iCount = 0; iCount < TrackList.size(); iCount++)
			{
				if(TrackList.get(iCount).equals(t.get(iCount)) != true)
				{
					return false;
				}
			}
			return true;
		}
			
	}
	
	// runs the mkvmerge program with the --identify-verbose command and gets the track info from each string 
	public void getTracks(String cmdPath)
	{
		Runtime run = Runtime.getRuntime();
		Process pr = null;
		//Runs the Process
		try
		{
			String temp = ("\"" +  cmdPath + "\"" + " --identify-verbose " +"\"" + EpPath + "\"");
			System.out.println(temp);
			//pr = run.exec(temp);
			ProcessBuilder getinfo = new ProcessBuilder(cmdPath.toString(),"--identify-verbose",EpPath.toString());
			//getinfo.command(temp);
			pr = getinfo.start();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.out.println("Error Cannot get Track Info for" + EpPath.getName());
		}
		

		//Gathers the Input from the command line and separate the tracks
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		ArrayList<String> temp = new ArrayList<String>();
		String line;
		try 
		{
			while ((line=buf.readLine())!=null) 
			{
				//System.out.println(line);
				if (!line.isEmpty())
				{
					temp.add(line);
				}
			}			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		//gets track info from the strings
		PrintWriter fileOut;
		try
		{
			fileOut = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
			fileOut.println(EpPath.getPath());
			fileOut.close();
			for(int iCount = 0; iCount < temp.size(); iCount++)
			{
				if(temp.get(iCount).substring(0, 8).equals("Track ID"))
				{
					TrackInfo NewTrack = new TrackInfo();
					NewTrack.GetTrackInfo(temp.get(iCount));
					TrackList.add(NewTrack);
				}
			}
			
			
		}
		catch(IOException e)
		{
			System.out.println("Error "+ e.getMessage());
		}
		
		try 
		{
			buf.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public String getLastPartOfPath(MKVMergePaths paths) {
		
		String epPathStr = EpPath.getAbsolutePath();
		String fromPath = paths.fromPath.getAbsolutePath();
		
		String lastPart = epPathStr.substring(fromPath.length());
		 
		return lastPart;
	}
	
	//makes final command
	public String makeCommand(MKVMergePaths paths, String args)
	{
		String outPath = paths.outPath.getAbsolutePath() + getLastPartOfPath(paths);
		String sCommand;
		sCommand = "\"" + paths.cmdPath + "\"";
		//if the file is not an mkv
		if(EpPath.getName().matches(".*(?<!\\.mkv)$"))
		{
			// make it an mkv
			String sTemp = outPath.replaceAll("\\.[a-zA-Z]{0,3}$", ".mkv");
			sCommand += (" -o " + "\"" + sTemp + "\"");
		}
		else
		{
			sCommand += (" -o " + "\"" + outPath + "\"");
		}
		sCommand += args;
		sCommand += " -T --no-global-tags " + "\"" + EpPath + "\"";
		
		
		return sCommand;
	}
	
	public String makeCommand(MKVMergePaths paths, String args, File subPath)
	{
		String sCommand;
		
		sCommand = makeCommand(paths, args);
		
		sCommand += addSub(subPath);
		
		sCommand += "\"" + subPath.getPath() + "\"";
		
		sCommand += makeTrackOrder(true);
		return sCommand;
	}
	
	//add External Subtitle File
	public String addSub(File subPath)
	{
		//TODO make dynamic
		//TODO Support multiple external tracks
		
		//"--language" "0:jpn" "--default-track" "0:yes" "--forced-track" "0:no" "-s" "0" "-D" "-A" "-T" "--no-global-tags" "--no-chapters" 
		//"C:\\Users\\Jeremy\\Videos\\Dual Audio\\[AHQ] Rurouni Kenshin - 01 - 95 [Dual Audio]\\[AHQ] Rurouni Kenshin - 01 - The Handsome Swordsman of Legend.ssa" "--track-order" "0:0,0:1,1:0"
	
		return " --language 0:jpn --default-track 0:yes --forced-track 0:no -s 0 -D -A -T --no-global-tags --no-chapters ";
	}
	public String makeTrackOrder(Boolean SubTrack)
	{
		
		String TrackOrder = " --track-order ";
		
		for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
		{
			if(TrackList.get(iCount).getType().equals("video"))
			{
				TrackOrder += "0:" + TrackList.get(iCount).getTrackNo();
				TrackOrder += ",";
			}
			
		}
		for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
		{
			if(TrackList.get(iCount).getType().equals("audio"))
			{
				TrackOrder += "0:" + TrackList.get(iCount).getTrackNo();
				TrackOrder += ",";
			}
			
		}
		if(SubTrack == true)
		{
			TrackOrder += "1:0";
		}
		else
		{
			
			Integer SubCount = 0;
			for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
			{
				
				if(TrackList.get(iCount).getType().equals("subtitles"))
				{
					SubCount++;
				}
			}
			
			for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
			{
				
				if(TrackList.get(iCount).getType().equals("subtitles"))
				{
					TrackOrder += "0:" + TrackList.get(iCount).getTrackNo();
					
				}
				if(iCount != SubCount)
					TrackOrder += ",";
			}
		}
		
		return TrackOrder;
	}
	
	//helps make the arguments needed in the final command
	public String makeArguments()
	{
		String sArguments;
		{
			Integer iTrackCount = 0;
			
			// lists video tracks so user can make selections easier
			iTrackCount = 0;
			sArguments = "";
			for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
			{
				if(TrackList.get(iCount).getType().equals("video"))
				{
					iTrackCount++;
				}
			}
			if(iTrackCount > 1)
			{
				System.out.println("Track Type: VIDEOS");
				for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
				{
					if(TrackList.get(iCount).getType().equals("video"))
					{
						TrackList.get(iCount).display();
					}
				}
				
				sArguments += TrackChoice("video");
			}
			else
			{
				for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
				{
					if(TrackList.get(iCount).getType().equals("video"))
						sArguments += TrackChoice("video", TrackList.get(iCount).getTrackNo(), 0);
				}
			}
			
			
			// lists audio tracks so user can make selections easier
			
			iTrackCount = 0;
			for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
			{
				if(TrackList.get(iCount).getType().equals("audio"))
				{
					iTrackCount++;
					//TrackList.get(iCount).display();
				}
			}
			if(iTrackCount != 0)
			{
				System.out.println("Track Type: AUDIO");
				for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
				{
					if(TrackList.get(iCount).getType().equals("audio"))
					{
						TrackList.get(iCount).display();
					}
				}
				System.out.println();
				sArguments += TrackChoice("audio");	
			}
			
			
			// lists subtitle tracks so user can make selections easier
			
			iTrackCount = 0;
			for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
			{
				if(TrackList.get(iCount).getType().equals("subtitles"))
				{
					iTrackCount++;
					//TrackList.get(iCount).display();
				}
			}
			if(iTrackCount != 0)
			{
				System.out.println("Track Type: SUBTITLES");
				for(Integer iCount = 0; iCount < TrackList.size() ;iCount++)
				{
					if(TrackList.get(iCount).getType().equals("subtitles"))
					{
						TrackList.get(iCount).display();
					}
				}
				System.out.println();
				sArguments += TrackChoice("subtitles");
			}
		}	
		
		return sArguments;
	}
	
	private String TrackChoice(String Type, Integer sTrack, Integer iDefaultTrack)
	{
		if(Type.equals("video"))
		{
			return " -d " + sTrack + " --default-track " + iDefaultTrack + ":yes";			
		}
		if(Type.equals("audio"))
		{	
			return " -a " + sTrack + " --default-track " + iDefaultTrack + ":yes";

		}
		if(Type.equals("subtitles"))
		{
			return " -s " + sTrack + " --default-track " + iDefaultTrack + ":yes";
		}
		return "";
	}
	
	//lets user customize the tracks for the new file 
	private String TrackChoice(String Type)
	{
		Scanner stdIn = new Scanner(System.in);
		String	sTemp;
		
		String 	sTracks;
		Integer iDefaultTrack = -1;
		Integer iForcedTrack = -1;
		
		Boolean bEnd = false;
		Boolean	bForced = false;
		
		while(bEnd == false)
		{
			
			System.out.print("Enter all of the tracks that you want to keep (seperate multiples with commas ex: 0,1): "); 
			sTracks = stdIn.nextLine();
			
			System.out.print("Which track is the defualt track(only one number): ");
			try
			{
				iDefaultTrack = Integer.parseInt(stdIn.nextLine());	
			}
			catch(NumberFormatException e)
			{
				System.out.println("Not a valid Number");
				continue;
			}
			System.out.println();
			
			
			
			System.out.println("Keep Tracks " + sTracks);
			System.out.println("Default track: " + iDefaultTrack);
			if(bForced){System.out.println(" Forced track: " + iForcedTrack);}
			sTemp = "y";
			System.out.print("Is the information correct(y/n)[" + sTemp + "]: ");
			sTemp = stdIn.nextLine();
			
			//Makes appropriate arguments for each track type
			if(sTemp.equalsIgnoreCase("y") || sTemp.equalsIgnoreCase(""))
			{
				if(Type.equals("video"))
				{
					return " -d " + sTracks + " --default-track " + iDefaultTrack + ":yes";			
				}
				if(Type.equals("audio"))
				{	
					return " -a " + sTracks + " --default-track " + iDefaultTrack + ":yes";

				}
				if(Type.equals("subtitles"))
				{
					return " -s " + sTracks + " --default-track " + iDefaultTrack + ":yes";
				}
			}
			else
			{
				bEnd = false;
			}
		}
		return "";
	}
	
	public static boolean isSubTrack(String filename) {
		return filename.toLowerCase().endsWith(".ssa") ||
			   filename.toLowerCase().endsWith(".ass") ||
			   filename.toLowerCase().endsWith(".srt");
	}

	public static boolean isVideo(String filename) {
		return filename.toLowerCase().endsWith(".mkv") ||
			   filename.toLowerCase().endsWith(".ogm") ||
			   filename.toLowerCase().endsWith(".avi");
	}
	
}
