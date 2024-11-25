package onlineFramework;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public class ClientHandle extends Thread
{
	private ServerSocket serverSocket = null;
	private Socket client = null;
	private Thread readThread = null;
	
	private String prevData = null;
	private volatile String data = null;
	private volatile String playerName = null;
	
	private volatile NativeKeyEvent playerInput = null;
	private volatile OutputStream os = null;
	private volatile InputStream is = null;
	private volatile ObjectOutputStream oos = null;
	private volatile ObjectInputStream ois = null;
	
	private final Object displayLock = new Object();
	private final Object inputLock = new Object();
	private final Object nameLock = new Object();
	
	public ClientHandle(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	
	public void setDisplay(String data)
	{
		synchronized (displayLock)
		{
			this.data = data;
		}
	}
	
	private String getDisplay()
	{
		return this.data;
	}
	
	public void setPlayerInput(NativeKeyEvent e)
	{
		synchronized (inputLock)
		{
			this.playerInput = e;
		}
	}
	
	public NativeKeyEvent getPlayerInput()
	{
		synchronized (inputLock)
		{
			return this.playerInput;
		}
	}
	
	private void setPlayerName(String name)
	{
		synchronized (nameLock)
		{
			this.playerName = name;
		}
	}
	
	public String getPlayerName()
	{
		synchronized (nameLock)
		{
			return this.playerName;
		}
	}
	
	public void run()
	{
		try
		{
			this.client = this.serverSocket.accept();
			this.os = this.client.getOutputStream();
			this.is = this.client.getInputStream();
			this.oos = new ObjectOutputStream(this.os);
			this.ois = new ObjectInputStream(this.is);
			
			try
			{
				this.setPlayerName((String) this.ois.readObject());
			}
			catch (ClassNotFoundException e1)
			{
				e1.printStackTrace();
				this.end();
				return;
			}
			
			this.readThread = new Thread(() -> {
				while(!this.readThread.isInterrupted())
				{
					try
					{
						NativeKeyEvent e = (NativeKeyEvent) this.ois.readObject();
						this.setPlayerInput(e);
					}
					catch (IOException | ClassNotFoundException ex)
					{
						this.end();
						return;
					}
				}
			});
			
			this.readThread.start();
			
			while(!this.isInterrupted())
			{
				synchronized (displayLock)
				{
					if(this.prevData == null && this.getDisplay() == null)
						continue;
					else if(this.prevData == null && this.getDisplay() != null)
					{
						this.oos.writeObject(this.getDisplay());
						this.prevData = this.getDisplay();
					}
					else if(!this.prevData.equals(this.getDisplay()))
					{
						this.oos.writeObject(this.getDisplay());
						this.prevData = this.getDisplay();
					}
				}
			}
		}
		catch (IOException e)
		{
			this.end();
			return;
		}
	}
	
	public void end()
	{
		try
    	{
    		if(this.isAlive())	
				this.interrupt();
    		if(this.readThread != null && this.readThread.isAlive())
    			this.readThread.interrupt();
    		if(this.ois != null)	
    			this.ois.close();
    		if(this.oos != null)	
    			this.oos.close();
    		if(this.is != null)
    			this.is.close();
    		if(this.os != null)
    			this.os.close();
			if(this.client != null)
    			this.client.close();
		}
    	catch (IOException e)
    	{
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
