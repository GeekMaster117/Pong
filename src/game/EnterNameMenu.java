package game;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import engine.Canvas;
import engine.Screen;
import functionalities.Functionalities;

public class EnterNameMenu extends Functionalities
{
	private Screen console = new Screen();
	private Canvas background = new Canvas(120, 30);
	
	private enum select
	{	
		Continue,
		Back;
		
		public String getDisplayName()
		{
			return this.toString().replace('_', ' ');
		}
	}
	private int selectPointer = 0;
	
	private int exitStatus = 0;
	
	private StringBuilder name = new StringBuilder();
	
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
				if(select.values()[selectPointer] == select.Continue && name.length() > 0)
					this.exitStatus = selectPointer + 1;
				else if(select.values()[selectPointer] == select.Back)
					this.exitStatus = selectPointer + 1;
				break;
			case NativeKeyEvent.VC_BACKSPACE:
				if(name.length() > 0)
				{
					this.name.deleteCharAt(name.length() - 1);
					this.show();
				}
				break;
			default:
				if(this.isAlphaNumeric(e) && name.length() <= 10)
				{
					this.name.append(NativeKeyEvent.getKeyText(e.getKeyCode()).charAt(0));
					this.show();
				}
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
	
	private void displayName()
	{
		String str = "Enter Name: " + this.name.toString();
		this.background.insertString(str,
				this.getMiddleXPosition(this.background.getWidth(), str.length()),
				this.getMiddleYPosition(this.background.getHeight(), 1),
				str.length(),
				1);
	}
	
	private void show()
	{
		this.background.clearCanvas();
		this.displayName();
		for(int i = 0; i < select.values().length; ++i)
			this.displayEnum(select.values()[i], 0, 2 + 2 * i, this.selectPointer == i);
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
		EnterNameMenu obj = new EnterNameMenu();
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
		obj.setPlayerName(obj.name.toString());
		obj.exit();
		return obj.exitStatus;
	}
}
