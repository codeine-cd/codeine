package yami.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import yami.model.Result;

public class ProcessExecuter
{
	public static Result execute(List<String> cmd) throws IOException, InterruptedException
	{
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);
		Process process = pb.start();
		Worker worker = new Worker(process);
		worker.start();
		try
		{
			long timeout = 30000;
			worker.join(timeout);
			if (worker.exit != null)
				return new Result(worker.exit, worker.output);
			else
				throw new RuntimeException();
		}
		catch (InterruptedException ex)
		{
			worker.interrupt();
			Thread.currentThread().interrupt();
			throw ex;
		} finally
		{
			process.destroy();
		}
	}
	
	public static Result execute(String cmd) throws IOException, InterruptedException
	{
		List<String> cmdList = new ArrayList<String>();
		cmdList.add(cmd);
		return execute(cmdList);
	}
	
	private static class Worker extends Thread
	{
		private final Process process;
		private Integer exit;
		private String output = "";
		
		private Worker(Process process)
		{
			this.process = process;
		}
		
		@Override
		public void run()
		{
			try
			{
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				try
				{
					while ((line = br.readLine()) != null)
					{
						output += line + "\n";
					}
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
				exit = process.waitFor();
			}
			catch (InterruptedException ignore)
			{
				return;
			}
		}
	}
}
