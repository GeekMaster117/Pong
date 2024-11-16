package onlineFramework;

public class ServerHandle extends Thread
{
	private volatile Server server;
	
	public ServerHandle()
	{
		this.server = new Server();
	}
	
	public void run()
	{
		this.server.start();
	}
	
	public void startServer()
	{
		this.setName("Server");
		this.start();
	}
	
	public void endServer()
	{
		this.server.end();
	}
}
