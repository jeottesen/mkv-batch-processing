import java.io.File;


public class MKVMergePaths 
{
	public File cmdPath;
	public File outPath; //used to be File
	
	MKVMergePaths(String cPath, String oPath)
	{
		cmdPath = new File(cPath).getAbsoluteFile();
		outPath = new File(oPath).getAbsoluteFile();
	}
}
