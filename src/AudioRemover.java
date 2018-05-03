import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

//Jeremy Ottesen
// This program removes the English audio from Matroska Video files


public class AudioRemover
{
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static String arch = System.getProperty("os.arch");
	
	private static final String usage = "Usage: mkv-batch-processor from_path to_path [-output file] [--mkvmerge path]\n" + 
                                  "    from_path       - Path to folder you want to process\n" + 
                                  "    to_path         - Path to folder you want the files to end up\n" + 
                                  "    --output file   - Path to output the script file (Default: ./mkv_batch.bat or ./mkv_batch.sh)\n" +
                                  "    --mkvmerge path - Path to mkvmerge executable\n\n" +
                                  "    It will scan the [from_path] and get the info about the tracks from each file. \n" +
                                  "    it then displays the info and asks what you want to do with it. It will save \n" +
                                  "    that info for that set of tracks and use it on tracks that match. at the end it\n" +
                                  "    outputs a batch file with all the commands based on the selection.";
	
	
	public static void main(String[] args)
	{
		if (args.length < 2) {
			System.out.println("please give two arguments for the from_path and to_path.\n\n");
			System.out.println(usage);
			return;
		}
		
		String fromPath = "";// Path to File location
		fromPath = args[0];
		
		String toPath = "";// Path to modified files
		toPath = args[1];
		
		if (fromPath.isEmpty() || toPath.isEmpty()) {
			System.out.println(usage);
			return;
		}
		
		String mkvmergePath = "";
        String outputFile = "";
        
		int i = 2;
        String arg;
		while (i < args.length && args[i].startsWith("-")) {
            arg = args[i];
            
            if (arg.equals("--help") || arg.equals("-h")) {
                System.out.println(usage);
                return;
            }
            
            else if (arg.equals("--mkvmerge")) {
                if (i < args.length)
                	mkvmergePath = args[++i];
                else {
        			System.out.println("Please enter a path for mkvmerge\n\n");
                	System.out.println(usage);
                	return;
                }
            }
            else if (arg.equals("--output")) {
                if (i < args.length)
                	outputFile = args[++i];
                else {
                	System.out.println("Please enter a path for the batch file\n\n");
                	System.out.println(usage);
                	return;
                }
            }
        }
		
		if (mkvmergePath.isEmpty()) {
		
			if (OS.indexOf("win") >= 0) { // Windows
				if (arch.equals("amd64"))
					mkvmergePath = "C:\\Program Files\\MKVtoolNix\\mkvmerge.exe";
				else if (arch.equals("x86"))
					mkvmergePath = "C:\\Program Files (x86)\\MKVtoolNix\\mkvmerge.exe";
			} else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ) { // Linux
				mkvmergePath = "/usr/bin/mkvmerge";
			} else // Default
				mkvmergePath = "mkvmerge";
		}
			
		
		
        MKVMergePaths Paths = new MKVMergePaths(mkvmergePath, // Path to mkvmerge executable
        										toPath);// Path to modified files
		File SeriesPath = new File(fromPath); // Path to files you want to modify
		ArrayList<Series> AllSeries = new ArrayList<Series>();

		//deletes old error log
		File errLog = new File("log.txt");
		errLog.delete();

		File[] temp;
		temp = SeriesPath.listFiles();

		if (outputFile.isEmpty()) {
			if (OS.indexOf("win") >= 0) // Windows
				outputFile = "mkv_batch.bat";
			else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ) // Linux
				outputFile = "mkv_batch.sh";
		}
		//deletes existing batch file if necessary
		File batchPath = new File(outputFile);
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
		
		if (temp == null) {
			System.err.println("There are no files under the directory: '" + fromPath + "'");
			return;
		}
		
		AllSeries.add(new Series(fromPath, Paths));
		//gets information for all episodes in all series
		for(Integer iCount = 0; iCount < temp.length ; iCount++)
		{
			if(temp[iCount].isDirectory())
			{
				AllSeries.add(new Series(temp[iCount].getPath(), Paths, true));
			}
		}


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


		System.out.println("\nProcessing complete please review '" + batchPath.getAbsoluteFile() + "'. Then run it to process your video files.");

	}
}
