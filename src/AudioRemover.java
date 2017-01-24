import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

//Jeremy Ottesen
// This program removes the English audio from Matroska Video files


public class AudioRemover
{
	public static void main(String[] args)
	{
        MKVMergePaths Paths = new MKVMergePaths("C:\\Program Files (x86)\\MKVtoolnix\\mkvmerge.exe", // Path to File location
                                                "D:\\Users\\HTPC\\Videos\\AudExtract");              // Path to modified files
		File SeriesPath = new File("D:\\Users\\HTPC\\Videos\\DualAudio"); // Path to files you want to modify
		ArrayList<Series> AllSeries = new ArrayList<Series>();

		//deletes old error log
		File errLog = new File("log.txt");
		errLog.delete();

		File[] temp;
		temp = SeriesPath.listFiles();

		//deletes existing batch file if necessary
		File batchPath = new File("batch.bat");
		if(batchPath.exists())
		{
			Scanner stdIn = new Scanner(System.in);
			String sTemp;
			System.out.print(batchPath.getName() + " already exists overwrite(y/n)?");
			sTemp = stdIn.nextLine();
			if(sTemp.equalsIgnoreCase("y"))
			{
				if(batchPath.delete() == false)
				{
					System.err.println("File Cannot be overwritten");
					return;
				}
			}
			else
			{
				String newName = "(2)" + batchPath.getName();
				batchPath = new File(newName);
			}
		}

		//gets information for all episodes in all series
		for(Integer iCount = 0; iCount < temp.length ; iCount++)
		{
			if(temp[iCount].isDirectory())
			{
				AllSeries.add(new Series(temp[iCount].getPath(), Paths));
			}
		}


		System.out.println();

		//EpTrackList and EpArgList will match each other a track configuration will have a different argument list for the command
		ArrayList< ArrayList<TrackInfo> > 	EpTrackList = new ArrayList< ArrayList<TrackInfo> >();
		ArrayList< String >					EpArgList	= new ArrayList< String>();

		// gets final commands for all episodes in every series
		for(Integer iCount = 0; iCount < AllSeries.size() ; iCount++)
		{
			AllSeries.get(iCount).getCommands(batchPath, EpTrackList, EpArgList);
			EpTrackList = AllSeries.get(iCount).getEpTrackList(); //gets new configurations
			EpArgList = AllSeries.get(iCount).getEpArgList();
		}




	}
}
