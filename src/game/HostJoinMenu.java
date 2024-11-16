package game;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import engine.Canvas;
import engine.Screen;
import functionalities.Functionalities;

public class HostJoinMenu extends Functionalities
{
	private Screen console = new Screen();
	private Canvas background = new Canvas(120, 30);
	
	private enum select
	{	
		Host_Server,
		Join_Server,
		Back;
		
		public String getDisplayName()
		{
			return this.toString().replace('_', ' ');
		}
	}
	private int selectPointer = 0;
	
	private int exitStatus = 0;
	
	public void nativeKeyPressed(NativeKeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case NativeKeyEvent.VC_UP: 
				this.selectPointer = Math.max(--this.selectPointer, 0);
				this.show();
				break;
			case NativeKeyEvent.VC_DOWN: 
				this.selectPointer = Math.min(++this.selectPointer, select.values().length - 1);
				this.show();
				break;
			case NativeKeyEvent.VC_ENTER:
				this.exitStatus = selectPointer + 1;
				break;
		}
	}
	
	private void displayEnum(select s, int offsetX, int offsetY, boolean showPointer)
	{
		this.background.insertString(s.getDisplayName(), 
				this.getMiddleXPosition(this.background.getWidth(), s.getDisplayName().length()) + offsetX, 
				this.getMiddleYPosition(this.background.getHeight(), 1) + offsetY, 
				s.getDisplayName().length(), 
				1);
		
		if(showPointer)
			this.background.insertString("->", 
					this.getMiddleXPosition(this.background.getWidth(), s.getDisplayName().length()) + offsetX - 3, 
					this.getMiddleYPosition(this.background.getHeight(), 1) + offsetY, 
					2, 1);
	}
	
	private void show()
	{
		this.background.clearCanvas();
		for(int i = 0; i < select.values().length; ++i)
			this.displayEnum(select.values()[i], 0, 2 * i, this.selectPointer == i);
		this.displayBorders(background);
		this.console.preRefresh(background);
	}
	
	private void init()
	{
		this.addListener();
		this.show();
	}
	
	private void exit()
	{
		this.console.clearConsole();
		this.removeListener();
	}
	
	public static int startMenu()
	{
		HostJoinMenu obj = new HostJoinMenu();
		obj.init();
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
