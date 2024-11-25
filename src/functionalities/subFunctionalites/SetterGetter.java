package functionalities.subFunctionalites;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface SetterGetter
{
	public void setPlayerName(String playerName);
	
	public String getPlayerName();
	
	public void setServerInet(String inet);
	
	public String getServerInet();
	
	public void setSocket(Socket conn);
	
	public Socket getSocket();
	
	public void setSocketOutputStream(OutputStream os);
	
	public OutputStream getSocketOutputStream();
	
	public void setSocketInputStream(InputStream is);
	
	public InputStream getSocketInputStream();
	
public void setSocketObjectOutputStream(ObjectOutputStream os);
	
	public ObjectOutputStream getSocketObjectOutputStream();
	
	public void setSocketObjectInputStream(ObjectInputStream is);
	
	public ObjectInputStream getSocketObjectInputStream();
}
