import java.io.File;


public class MKVMergePaths 
{
	public File cmdPath;
	public File fromPath;
	public File outPath; //used to be File
	
	MKVMergePaths(String cPath, String fPath, String oPath)
	{
		cmdPath = new File(cPath).getAbsoluteFile();
		fromPath = new File(fPath).getAbsoluteFile();
		outPath = new File(oPath).getAbsoluteFile();
	}
}
