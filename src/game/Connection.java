package game;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import engine.Canvas;
import engine.Screen;
import functionalities.Functionalities;

public class Connection extends Functionalities
{
	private Screen console = new Screen();
	private Canvas background = new Canvas(120, 30);
	
	private int exitStatus = 0;
	
	private void show()
	{
		this.background.clearCanvas();
		String str = "Attempting to connect to Host...";
		this.background.insertString(str,
				this.getMiddleXPosition(this.background.getWidth(), str.length()),
				this.getMiddleYPosition(this.background.getHeight(), 1),
				str.length(),
				1);
		console.preRefresh(background);
		
		String errorMessage = null;
		
		try
		{
			Socket conn = new Socket(this.getServerInet(), 12345);
			try
			{
				OutputStream os = conn.getOutputStream();
				this.setSocketOutputStream(os);
				InputStream is = conn.getInputStream();
				this.setSocketInputStream(is);
				ObjectOutputStream oos = new ObjectOutputStream(this.getSocketOutputStream());
				this.setSocketObjectOutputStream(oos);
				ObjectInputStream ois = new ObjectInputStream(this.getSocketInputStream());
				this.setSocketObjectInputStream(ois);
				
				this.getSocketObjectOutputStream().writeObject(this.getPlayerName());
			}
			catch (IOException e)
			{
				this.setSocketInputStream(null);
				this.setSocketOutputStream(null);
				this.setSocketObjectOutputStream(null);
				this.setSocketObjectInputStream(null);
				errorMessage = "Connected, but cannot communicate with host";
			}
			this.setSocket(conn);
			this.exitStatus = 2;
		}
		catch (UnknownHostException e)
		{
			errorMessage = "Cannot find Host";
		}
		catch (IOException e)
		{
			errorMessage = "Connection Failed";
		}
		if(errorMessage == null)
			return;
		for(int i = 5; i > 0; --i)
		{
			this.background.clearCanvas();
			this.background.insertString(errorMessage,
					this.getMiddleXPosition(this.background.getWidth(), errorMessage.length()),
					this.getMiddleYPosition(this.background.getHeight(), 1),
					errorMessage.length(),
					1);
			str = "Try Again in ";
			this.background.insertString(str,
					this.getMiddleXPosition(this.background.getWidth(), str.length() + 1),
					this.getMiddleYPosition(this.background.getHeight(), 1) + 2,
					str.length(),
					1);
			this.background.insertChar((char) (i + '0'),
					this.getMiddleXPosition(this.background.getWidth(), str.length() + 1) + str.length(),
					this.getMiddleYPosition(this.background.getHeight(), 1) + 2);
			console.preRefresh(background);
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e1)
			{
				this.exitStatus = -1;
			}
			this.exitStatus = 1;
		}
	}
	
	private void exit()
	{
		this.console.clearConsole();
	}
	
	public static int startConnection()
	{
		Connection obj = new Connection();
		obj.show();
		obj.exit();
		return obj.exitStatus;
	}
}
