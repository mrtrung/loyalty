package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.crm.thread.DispatcherThread;
import com.crm.thread.util.ThreadUtil;
import com.fss.util.AppException;

public class FileTest extends DispatcherThread
{
	// adsdasdee
	private class Test implements Runnable
	{
		String				filePath	= "log.txt";

		SimpleDateFormat	sdf			= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		DispatcherThread	dispatcher	= null;

		public Test(DispatcherThread dispatcher, String filePath)
		{
			this.dispatcher = dispatcher;
			this.filePath = filePath;
		}

		@Override
		public void run()
		{
			int i = 0;
			while(dispatcher.isAvailable())
			{
				String loopName = Thread.currentThread().getName() + "/Loop#" + (i++);
				//File file = new File(filePath);
				//try
				//{
				//	if (file.createNewFile())
				//		debugMonitor(loopName + ":File created.");
				//}
				//catch (IOException e1)
				//{
				//	debugMonitor(e1);
				//}
				// FileInputStream fis = null;
				//FileOutputStream fos = null;
				try
				{
					// fis = new FileInputStream(file);
					//fos = new FileOutputStream(file, true);
					//debugMonitor(loopName + ":File Opened.");
					String content = sdf.format(new Date()) + ":" + loopName + ":Writed.\r\n";
					//byte[] bytes = content.getBytes();

					//fos.write(bytes);
					//fos.flush();
					debugMonitor(content);

					// fis.close();
					//fos.close();
					//debugMonitor(loopName + ":File Closed.");
				}
				catch (Exception e)
				{
					debugMonitor(e);
				}
				
				try
				{
					Thread.sleep(dispatcher.getDelayTime());
				}
				catch (Exception e)
				{
					
				}
			}
		}
	}
	
	private String filePath = "";
	private int threadCount = 1000;
	
	@Override
	public Vector getDispatcherDefinition()
	{
		// TODO Auto-generated method stub
		
		Vector vtReturn = new Vector();
		vtReturn.add(ThreadUtil.createTextParameter("filePath", 400, ""));
		vtReturn.add(ThreadUtil.createIntegerParameter("threadCount", ""));
		
		vtReturn.addAll(super.getDispatcherDefinition());
		return vtReturn;
	}
	
	@Override
	public void fillDispatcherParameter() throws AppException
	{
		try
		{
			super.fillDispatcherParameter();

			filePath = ThreadUtil.getString(this, "filePath", false, "");

			threadCount = ThreadUtil.getInt(this, "threadCount", 1000);
		}
		catch (AppException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public void beforeProcessSession() throws Exception
	{
		// TODO Auto-generated method stub
		super.beforeProcessSession();
		for (int i = 0; i < threadCount; i++)
		{
			Thread t = new Thread(new Test(this, filePath));
			t.setName("Thread#" + i);
			t.start();
		}
	}

	public static void main(String[] params)
	{
		for (int i = 0; i < 1000; i++)
		{
			Thread t = new Thread(new FileTest());
			t.setName("Thread#" + i);
			t.start();
		}
	}
}
