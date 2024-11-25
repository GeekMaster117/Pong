package boot;

import functionalities.Functionalities;
import game.*;
import onlineFramework.ServerHandle;

public class Master extends Functionalities
{	
	private static ServerHandle handle;
	private static Thread garbageCollector;
	
	private enum fun
	{
		Init,
		EnterName,
		HostJoin,
		Join,
		Host,
		ConnectionHost,
		ConnectionJoin,
		Lobby,
		Exit;
	}
	
	private static fun handleLobbyMenu()
	{
		switch(Display.startDisplay())
		{
			case -1:
				System.out.println("Thread Interrupted in Menu");
				return fun.Exit;
			case 2:
				return fun.HostJoin;
			default:
				System.out.println("Unexpected return encountered");
				return fun.Exit;
		}
		
	}
	
	private static fun handleConnection(fun parent)
	{
		switch(Connection.startConnection())
		{
			case -1:
				System.out.println("Thread Interrupted in Menu");
				return fun.Exit;
			case 1:
				if(parent == fun.Join)
					return fun.Join;
				else
					return fun.HostJoin;
			case 2:
				return fun.Lobby;
			default:
				System.out.println("Unexpected return encountered");
				return fun.Exit;
		}
	}
	
	private static fun handleHostMenu()
	{
		Master.handle = new ServerHandle();
		Master.handle.startServer();
		Master obj = new Master();
		obj.setServerInet(obj.getIPV4());
		return fun.ConnectionHost;
	}
	
	private static fun handleJoinMenu()
	{
		switch(JoinMenu.startMenu())
		{
			case -1:
				System.out.println("Thread Interrupted in Menu");
				return fun.Exit;
			case 1:
				return fun.ConnectionJoin;
			case 2:
				return fun.HostJoin;
			default:
				System.out.println("Unexpected return encountered");
				return fun.Exit;
		}
	}
	
	private static fun handleHostJoinMenu()
	{
		switch(HostJoinMenu.startMenu())
		{
			case -1:
				System.out.println("Thread Interrupted in Menu");
				return fun.Exit;
			case 1:
				return fun.Host;
			case 2:
				return fun.Join;
			case 3:
				return fun.EnterName;
			default:
				System.out.println("Unexpected return encountered");
				return fun.Exit;
		}
	}
	
	private static fun handleEnterNameMenu()
	{
		switch(EnterNameMenu.startMenu())
		{
			case -1:
				System.out.println("Thread Interrupted in Menu");
				return fun.Exit;
			case 1:
				return fun.HostJoin;
			case 2:
				return fun.Init;
			default:
				System.out.println("Unexpected return encountered");
				return fun.Exit;
		}
	}
	
	private static fun handleInitMenu()
	{
		switch(InitMenu.startMenu())
		{
			case -1:
				System.out.println("Thread Interrupted in Menu");
				return fun.Exit;
			case 1:
				return fun.EnterName;
			case 2:
				System.out.println("Thank you for playing");
				return fun.Exit;
			default:
				System.out.println("Unexpected return encountered");
				return fun.Exit;
		}
	}
	
	public static void main(String[] args)
	{
		String launchFromEXE = System.getenv("LAUNCH_FROM_EXE");
		if(launchFromEXE == null || !launchFromEXE.equals("true"))
		{
			System.out.println("Start it from Pong.exe");
			return;
		}
		
		Master.garbageCollector = new Thread(() -> {
			while(!Master.garbageCollector.isInterrupted())
			{
				System.gc();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					return;
				}
			}
		});
		
		garbageCollector.setName("Garbage Collector");
		garbageCollector.start();
		
		Functionalities.initListener();
		
		fun nextFunCall = fun.Init;
		
		boolean exit = false;
		while(true)
		{
			if(exit)
			{
				if(Master.handle != null)
					Master.handle.endServer();
				garbageCollector.interrupt();
				break;
			}
			switch(nextFunCall)
			{
				case Init:
					nextFunCall = handleInitMenu();
					break;
				case EnterName:
					nextFunCall = handleEnterNameMenu();
					break;
				case HostJoin:
					nextFunCall = handleHostJoinMenu();
					break;
				case Join:
					nextFunCall = handleJoinMenu();
					break;
				case Host:
					nextFunCall = handleHostMenu();
					break;
				case ConnectionHost:
					nextFunCall = handleConnection(fun.Host);
					break;
				case ConnectionJoin:
					nextFunCall = handleConnection(fun.Join);
					break;
				case Lobby:
					nextFunCall = handleLobbyMenu();
					break;
				case Exit:
					exit = true;
					break;
			}
		}
		
		Functionalities.endListener();
	}
}
