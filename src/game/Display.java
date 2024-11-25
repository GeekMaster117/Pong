package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import engine.Screen;
import functionalities.Functionalities;

public class Display extends Functionalities
{
	private Screen console = new Screen();
	private volatile ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private final Object inputStreamLock = new Object();
	
	private volatile int exitStatus = 0;
	
	public void nativeKeyPressed(NativeKeyEvent e)
	{
		try
		{
			this.oos.writeObject(e);
		}
		catch (IOException ex)
		{
			this.exitStatus = 2;
		}
	}
	
	public void nativeKeyReleased(NativeKeyEvent e)
	{
		try
		{
			this.oos.writeObject(null);
		}
		catch (IOException ex)
		{
			this.exitStatus = 2;
		}
	}
	
	private void displayServerData() throws ClassNotFoundException, IOException
	{
		synchronized (inputStreamLock)
		{
			String data = (String) this.ois.readObject();
			this.console.clearConsole();
			System.out.print(data);
		}
	}
	
	private void show()
	{
		Thread showThread = new Thread(() -> {
			try
			{
				while(true)
					this.displayServerData();
			}
			catch(ClassNotFoundException | IOException e)
			{
				this.exitStatus = 2;
			}
		});
		
		showThread.setName("Show Data");
		showThread.start();
	}
	
	private void exit()
	{
		this.console.clearConsole();
		try
		{
			if(this.ois != null)	
				this.ois.close();
			if(this.oos != null)
				this.oos.close();
			if(this.getSocketInputStream() != null)	
				this.getSocketInputStream().close();
			if(this.getSocketOutputStream() != null)	
				this.getSocketOutputStream().close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		this.removeListener();
	}
	
	private void init()
	{
		this.addListener();
		synchronized (inputStreamLock)
		{
			this.ois = this.getSocketObjectInputStream();
		}
		this.oos = this.getSocketObjectOutputStream();
	}
	
	public static int startDisplay()
	{
		Display obj = new Display();
		obj.init();
		obj.show();
		try
		{
			while(obj.exitStatus == 0)
			{
				Thread.sleep(100);
			}
		}
		catch(InterruptedException e)
		{
			obj.exit();
			return -1;
		}
		obj.exit();
		return obj.exitStatus;
	}
}
