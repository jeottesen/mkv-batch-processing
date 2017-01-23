import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class TrackInfo
{
	
	private Integer	TrackNo;
	private String	Type;
	private String	TypeDetails;
	private String	language;
	private Boolean	DefaultTrack;
	private Boolean	ForcedTrack;
	
	public Integer	getTrackNo()	{ return TrackNo; }
	public String	getType()		{ return Type; }
	public String	getTypeDetails(){ return TypeDetails; }
	public String	getLanguage()	{ return language; }
	public Boolean	getDefault()	{ return DefaultTrack; }
	public Boolean	getForced()		{ return ForcedTrack; }
	
	void display() // display track information
	{
		System.out.print("TrackNo: " + TrackNo);
		System.out.print("\tlanguage: " + language);
		System.out.print("\tType: " + Type);
		System.out.print("\tType Details: " + TypeDetails);
		System.out.print("\tDefaultTrack: " + DefaultTrack);
		System.out.println("\tForcedTrack:" + ForcedTrack);
	}
	Boolean equals(TrackInfo t)
	{
		if(TrackNo.equals(t.getTrackNo())
		&& Type.equals(t.getType())
		//&& TypeDetails.equals(t.getTypeDetails())
		&& language.equals(t.getLanguage())
		//&& DefaultTrack.equals(t.getDefault())
		//&& ForcedTrack.equals(t.getForced())
		)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//finds track information by analyzing strings that come from the --identify-verbose argument in MKVMerge 
	public void GetTrackInfo(String RawTrackInfo)
	{
		
		PrintWriter fileOut;
		try
		{
			fileOut = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true))); // Will log any errors that might occur when gathering track data
			//String RawTrackInfo = "Track ID 2: audio (A_AAC/MPEG4/LC/SBR) [language:jpn track_name:Japanese\\sAudio default_track:0 forced_track:0 enabled_track:1  audio_channels:6]"; - example track info
			try
			{
				//Get TrackNo
				TrackNo = Integer.parseInt(RawTrackInfo.substring(9,10));
			}
			catch(NumberFormatException e)
			{
				fileOut.print("No Track No");
				TrackNo = -1;
			}
			
			try
			{
				//Gets Track Type
				if(RawTrackInfo.substring(12, 20).indexOf(" ") != -1)
				{
					Type = RawTrackInfo.substring(12, 20).substring(0, RawTrackInfo.substring(12, 20).indexOf(" "));
				}
				else // in case track type is subtitle
				{
					Type = RawTrackInfo.substring(12, 21);
				}
			}
			catch(IndexOutOfBoundsException e)
			{
				fileOut.println("\tNo Track Type");
				Type = "";
			}
			
			try
			{
				//Gets Track TypeDetails
				TypeDetails = RawTrackInfo.substring(RawTrackInfo.indexOf("("), (RawTrackInfo.indexOf(")") + 1));
			}
			catch(IndexOutOfBoundsException e)
			{
				fileOut.println("\tNo Track Details");
				Type = "";
			}
			
			//Get language by searching for the starting index of "language:" in the string then adding 12 to get the three character language identifier then it will get just the three character language identifier
			try
			{
				language = RawTrackInfo.substring(RawTrackInfo.indexOf("language:"), RawTrackInfo.indexOf("language:") + 12).substring(9, 12);
			}
			catch(IndexOutOfBoundsException e)
			{
				fileOut.println("\tNo language");
				language = "";
			}
			
			//gets the defualt_track value
			try
			{
				DefaultTrack = (Integer.parseInt(RawTrackInfo.substring(RawTrackInfo.indexOf("default_track:"), RawTrackInfo.indexOf("default_track:") + 15).substring(14, 15)) != 0);
			}
			catch(IndexOutOfBoundsException e)
			{
				fileOut.println( "\tNo Default Track");
				DefaultTrack = false;
			}
			
			//Gets the forced_track value
			try
			{
				ForcedTrack = (Integer.parseInt(RawTrackInfo.substring(RawTrackInfo.indexOf("forced_track:"), RawTrackInfo.indexOf("forced_track:") + 14).substring(13, 14)) != 0);
			}
			catch(IndexOutOfBoundsException e)
			{
				fileOut.println("\tNo Forced Track");
				ForcedTrack = false;
			}
			fileOut.close();
		}
		catch(IOException e)
		{
			System.out.println("Error "+ e.getMessage());
		}
			
		//display();
	}

}
