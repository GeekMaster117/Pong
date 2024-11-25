package functionalities;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import engine.Canvas;
import functionalities.subFunctionalites.*;

public abstract class Functionalities implements 
NativeKeyListener, PositionQuirks, DisplayQuirks, StringQuirks, SetterGetter, OnlineInterfaceQuirks
{
	private static String playerName;
	private static String inet;
	private static Socket conn;
	private static OutputStream os;
	private static InputStream is;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	
	public static void initListener()
	{
		try
		{
            GlobalScreen.registerNativeHook();
        }
		catch(NativeHookException ex)
		{
            System.err.println("Failed to register JNativeHook");
            System.exit(-1);
        }
	}
	
	public void addListener()
	{
		GlobalScreen.addNativeKeyListener(this);
	}
	
	public void removeListener()
	{
		GlobalScreen.removeNativeKeyListener(this);
	}
	
	public static void endListener()
	{
		try
		{
			GlobalScreen.unregisterNativeHook();
		}
		catch(NativeHookException ex)
		{
            System.err.println("Failed to unregister JNativeHook");
            System.exit(-1);
        }
	}
	
	public int getMiddleXPosition(int canvasWidth, int horLength)
	{
		return ((canvasWidth - 1) / 2) - (horLength / 2); 
	}
	
	public int getMiddleYPosition(int canvasHeight, int verLength)
	{
		return ((canvasHeight - 1) / 2) - (verLength / 2);
	}
	
	public void displayBorders(Canvas cs)
	{
		cs.insertXLine('|', 0, 0, 0, cs.getHeight() - 1);
		cs.insertXLine('|', cs.getWidth() - 1, 0, cs.getWidth() - 1, cs.getHeight() - 1);
		cs.insertXLine('-', 0, 0, cs.getWidth() - 1, 0);
		cs.insertXLine('-', 0, cs.getHeight() - 1, cs.getWidth() - 1, cs.getHeight() - 1);
	}
	
	public boolean isAlphaNumeric(NativeKeyEvent e)
	{
		if((NativeKeyEvent.VC_Q <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_P) || 
				(NativeKeyEvent.VC_A <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_L) || 
				(NativeKeyEvent.VC_Z <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_M) || 
				(NativeKeyEvent.VC_1 <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_0))
			return true;
		return false;
	}
	
	public boolean isAlphaNumericInet(NativeKeyEvent e)
	{
		if((NativeKeyEvent.VC_Q <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_P) || 
				(NativeKeyEvent.VC_A <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_L) || 
				(NativeKeyEvent.VC_Z <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_M) || 
				(NativeKeyEvent.VC_1 <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_0) ||
				(NativeKeyEvent.VC_SEMICOLON == e.getKeyCode()) ||
				(NativeKeyEvent.VC_PERIOD == e.getKeyCode()))
			return true;
		return false;
	}
	
	public void setPlayerName(String playerName)
	{
		Functionalities.playerName = playerName;
	}
	
	public String getPlayerName()
	{
		return Functionalities.playerName;
	}
	
	public void setServerInet(String inet)
	{
		Functionalities.inet = inet;
	}
	
	public String getServerInet()
	{
		return Functionalities.inet;
	}
	
	public void setSocket(Socket conn)
	{
		Functionalities.conn = conn;
	}
	
	public Socket getSocket()
	{
		return Functionalities.conn;
	}
	
	public void setSocketOutputStream(OutputStream os)
	{
		Functionalities.os = os;
	}
	
	public OutputStream getSocketOutputStream()
	{
		return Functionalities.os;
	}
	
	public void setSocketInputStream(InputStream is)
	{
		Functionalities.is = is;
	}
	
	public InputStream getSocketInputStream()
	{
		return Functionalities.is;
	}
	
	public void setSocketObjectOutputStream(ObjectOutputStream oos)
	{
		Functionalities.oos = oos;
	}
	
	public ObjectOutputStream getSocketObjectOutputStream()
	{
		return Functionalities.oos;
	}
	
	public void setSocketObjectInputStream(ObjectInputStream ois)
	{
		Functionalities.ois = ois;
	}
	
	public ObjectInputStream getSocketObjectInputStream()
	{
		return Functionalities.ois;
	}
	
	public String getIPV4()
	{
		try
		{
			return Inet4Address.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void setClientSocket(Socket conn)
	{
		
	}
}
