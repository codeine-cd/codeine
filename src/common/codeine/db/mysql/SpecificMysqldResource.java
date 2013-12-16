package codeine.db.mysql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.jar.JarInputStream;

import com.mysql.management.MysqldResource;
import com.mysql.management.util.Utils;

public class SpecificMysqldResource extends MysqldResource
{
	private boolean bNewMySqldWasCopiedToWorkArea = false;

	public SpecificMysqldResource(File baseDir, File dataDir, String mysqlVersionString, boolean guessArch, PrintStream out, String binDir)
    {
    	super(baseDir, dataDir, null, false, out, out, new Utils(), binDir);
    }
    
    @Override
	protected synchronized void setVersion(boolean checkRunning, String mysqlVersionString)
    {
    	versionString = "5.0.21";
    }
    
    @Override
	protected File makeMysqld()
    {
        return getMysqldFilePointer();
    }
    
    @Override
	protected String[] constructArgs(@SuppressWarnings("rawtypes") Map mysqldArgs)
    {
    	String[] args = super.constructArgs(mysqldArgs);
    	String[] launcherArgs = new String[args.length + 1];
    	launcherArgs[0] = args[0].replaceAll("mysqld", "launcher");
    	System.arraycopy(args, 0, launcherArgs, 1, args.length);
    	return launcherArgs;
    }
    
	public void copyFile(File in, File out)
	{
		try {
			FileInputStream fis  = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while((i=fis.read(buf))!=-1)
			{
				fos.write(buf, 0, i);
			}
			fis.close();
			fos.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void ensureEssentialFilesExist()
	{
		try
		{
			if(bNewMySqldWasCopiedToWorkArea)
			{
				utils.files().deleteTree(new File(baseDir + File.separator + "share"));
			}
			
			String sSourceDir = binDir();

			JarInputStream dataIn = new JarInputStream(new FileInputStream(sSourceDir + File.separator + "data_dir.jar"));
			utils.streams().expandEachEntry(dataDir, dataIn);

			JarInputStream shareIn = new JarInputStream(new FileInputStream(sSourceDir + File.separator + "share_dir.jar"));
			utils.streams().expandEachEntry(baseDir, shareIn);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	
}
