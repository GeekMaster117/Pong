package onlineFramework;

import java.io.*;
import java.net.*;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import engine.Canvas;
import engine.Entity;
import functionalities.Functionalities;

public class Server extends Functionalities
{
    private ServerSocket serverSocket;
    private ClientHandle player1;
    private ClientHandle player2;
    private Canvas background1;
    private Canvas background2;
    private Entity paddle1;
    private Entity paddle2;
    private Entity ball;
    
    private enum LobbySelect
	{	
		Toggle_Ready,
		Back;
		
		public String getDisplayName()
		{
			return this.toString().replace('_', ' ');
		}
	}
    
    private enum display
    {
    	Lobby,
    	Level;
    }
    
    private display currentDisplay = display.Lobby;
    
    private long prevTime = System.currentTimeMillis();
    private int countdown = 6;
    
    private boolean exitStatus = false;
    
    private int selectPointer1 = 0, selectPointer2 = 0;
    private boolean ready1 = false, ready2 = false;
    
    private void displayServerIP(Canvas background)
	{
		String str = "Server Hosted on Address: " + this.getIPV4();
		background.insertString(str,
				this.getMiddleXPosition(background.getWidth(), str.length()), 
				4, 
				str.length(), 
				1);
	}
    
	private void displayLobby(Canvas background)
	{
		int width = 50;
		String player1Name = (this.player1.getPlayerName() == null) ? "Waiting to Join..." : this.player1.getPlayerName();
		String player2Name = (this.player2.getPlayerName() == null) ? "Waiting to Join..." : this.player2.getPlayerName();
		String ready1Status = (this.ready1 == true) ? "Ready" : "Not Ready";
		String ready2Status = (this.ready2 == true) ? "Ready" : "Not Ready";
		background.insertXLine('|',
				this.getMiddleXPosition(background.getWidth(), width),
				5,
				this.getMiddleXPosition(background.getWidth(), width),
				9);
		background.insertXLine('|',
				this.getMiddleXPosition(background.getWidth(), width) + width - 1,
				5,
				this.getMiddleXPosition(background.getWidth(), width) + width - 1, 
				9);
		background.insertXLine('-',
				this.getMiddleXPosition(background.getWidth(), width), 
				5,
				this.getMiddleXPosition(background.getWidth(), width) + width - 1,
				5);
		background.insertXLine('-',
				this.getMiddleXPosition(background.getWidth(), width), 
				9,
				this.getMiddleXPosition(background.getWidth(), width) + width - 1,
				9);
		background.insertXLine('-',
				this.getMiddleXPosition(background.getWidth(), width),
				7,
				this.getMiddleXPosition(background.getWidth(), width) + width - 1,
				7);
		
		background.insertString(player1Name,
				this.getMiddleXPosition(background.getWidth(), width) + 2,
				6,
				player1Name.length(),
				1);
		if(this.player1.getPlayerName() != null)
			background.insertString(ready1Status,
					this.getMiddleXPosition(background.getWidth(), width) + width - ready1Status.length() - 2,
					6,
					ready1Status.length(),
					1);
		background.insertString(player2Name,
				this.getMiddleXPosition(background.getWidth(), width) + 2,
				8,
				player2Name.length(),
				1);
		if(this.player2.getPlayerName() != null)
			background.insertString(ready2Status,
					this.getMiddleXPosition(background.getWidth(), width) + width - ready2Status.length() - 2,
					8,
					ready2Status.length(),
					1);
	}
	
	private void displayEnum(LobbySelect s, int offsetX, int offsetY, Canvas background, boolean showPointer)
	{
		background.insertString(s.getDisplayName(), 
				this.getMiddleXPosition(background.getWidth(), s.getDisplayName().length()) + offsetX, 
				this.getMiddleYPosition(background.getHeight(), 1) + offsetY, 
				s.getDisplayName().length(), 
				1);
		
		if(showPointer)
			background.insertString("->", 
					this.getMiddleXPosition(background.getWidth(), s.getDisplayName().length()) + offsetX - 3, 
					this.getMiddleYPosition(background.getHeight(), 1) + offsetY, 
					2, 1);
	}
	
	private void startGame()
	{
		String str = "Starting in ";
		if(this.ready1 && this.ready2)
		{
			this.background1.insertString(str + String.valueOf(this.countdown),
					this.getMiddleXPosition(this.background1.getWidth(), str.length() + 1),
					19,
					str.length() + 1,
					1);
			this.background2.insertString(str + String.valueOf(this.countdown),
					this.getMiddleXPosition(this.background1.getWidth(), str.length() + 1),
					19,
					str.length() + 1,
					1);
			if(System.currentTimeMillis() - this.prevTime < 1000)
				return;
			this.prevTime = System.currentTimeMillis();
			--this.countdown;
		}
		else
			this.countdown = 6;
		
		if(this.countdown <= 0)
			this.currentDisplay = display.Level;
	}
	
	private void handleLobbyInput(NativeKeyEvent e, ClientHandle player)
	{
		if(e == null)
			return;
		switch(e.getKeyCode())
		{
			case NativeKeyEvent.VC_UP:
				if(player.equals(this.player1))
					this.selectPointer1 = Math.max(this.selectPointer1 - 1, 0);
				else if(player.equals(this.player2))
					this.selectPointer2 = Math.max(this.selectPointer2 - 1, 0);
				break;
			case NativeKeyEvent.VC_DOWN:
				if(player.equals(this.player1))
				{
					this.selectPointer1 = Math.min(this.selectPointer1 + 1, LobbySelect.values().length - 1);
				}
				else if(player.equals(player2))
					this.selectPointer2 = Math.min(this.selectPointer2 + 1, LobbySelect.values().length - 1);
				break;
			case NativeKeyEvent.VC_ENTER:
				if(player.equals(this.player1))
				{
					if(LobbySelect.values()[selectPointer1] == LobbySelect.Toggle_Ready)
						this.ready1 = !ready1;
					else
					{
						this.selectPointer1 = 0;
						this.ready1 = false;
						this.player1.end();
						this.exitStatus = true;
						this.end();
					}
				}
				else if(player.equals(this.player2))
				{
					if(LobbySelect.values()[selectPointer2] == LobbySelect.Toggle_Ready)
						this.ready2 = !ready2;
					else
					{
						this.selectPointer2 = 0;
						this.ready2 = false;
						this.player2.end();
					}
				}
				break;
		}
	}
	
	private void displayLevel(Canvas background)
	{
		background.insertYLine('|',
				this.getMiddleXPosition(background.getWidth(), 1),
				0,
				this.getMiddleXPosition(background.getWidth(), 1),
				background.getHeight() - 1);
	}
	
	private void handleLevelInput(NativeKeyEvent e, Entity paddle)
	{
		if(e == null)
			return;
		switch(e.getKeyCode())
		{
			case NativeKeyEvent.VC_DOWN:
				paddle.setPosition(paddle.getXPosition(),
						Math.min(paddle.getYPosition() + 1, this.background1.getHeight() - paddle.getHeight() - 1));
				break;
			case NativeKeyEvent.VC_UP:
				paddle.setPosition(paddle.getXPosition(),
						Math.max(paddle.getYPosition() - 1, 1));
				break;
		}
	}
    
    public void start()
    {
        try
        {	
        	this.serverSocket = new ServerSocket(12345);
        	this.background1 = new Canvas(120, 30);
        	this.background2 = new Canvas(120, 30);
        	
        	this.paddle1 = new Entity();
        	this.paddle2 = new Entity();
        	this.paddle1.setDimensions(1, 4);
        	this.paddle2.setDimensions(1, 4);
        	this.paddle1.setPosition(1, 
        			this.getMiddleYPosition(this.background1.getHeight(), this.paddle1.getHeight()));
        	this.paddle2.setPosition(this.background2.getWidth() - 2,
        			this.getMiddleYPosition(this.background2.getHeight(), this.paddle2.getHeight()));
        	this.paddle1.setPaintChar(']');
        	this.paddle2.setPaintChar('[');

            this.player1 = new ClientHandle(this.serverSocket);
            this.player2 = new ClientHandle(this.serverSocket);
            
            this.player1.setName("Player1");
            this.player2.setName("Player2");
            
            this.player1.start();
            try 
            {
				Thread.sleep(100);
			}
            catch (InterruptedException e)
            {
				e.printStackTrace();
			}
            this.player2.start();
            
            while(!this.exitStatus)
            {
            	this.background1.clearCanvas();
            	this.background2.clearCanvas();
            	
            	if(this.currentDisplay == display.Lobby)
            	{
            		this.handleLobbyInput(this.player1.getPlayerInput(), this.player1);
            		this.player1.setPlayerInput(null);
                    this.handleLobbyInput(this.player2.getPlayerInput(), this.player2);
                    this.player2.setPlayerInput(null);
                    
                    this.displayServerIP(this.background1);
                    this.displayServerIP(this.background2);
                	this.displayLobby(this.background1);
                	this.displayLobby(this.background2);

                	for(int i = 0; i < LobbySelect.values().length; ++i)
                	{
                		this.displayEnum(LobbySelect.values()[i], 0, 2 * i, this.background1, this.selectPointer1 == i);
                		this.displayEnum(LobbySelect.values()[i], 0, 2 * i, this.background2, this.selectPointer2 == i);
                	}
                	
                	this.startGame();
            	}
            	else if(this.currentDisplay == display.Level)
            	{
            		this.ready1 = false;
            		this.ready2 = false;
            		
            		this.selectPointer1 = 0;
            		this.selectPointer2 = 0;
            		
            		if(System.currentTimeMillis() - this.prevTime > 150)
            		{
            			this.prevTime = System.currentTimeMillis();
                		this.handleLevelInput(this.player1.getPlayerInput(), this.paddle1);
                        this.handleLevelInput(this.player2.getPlayerInput(), this.paddle2);
            		}
                    
                    this.displayLevel(this.background1);
                    this.displayLevel(this.background2);
                    
                    this.paddle1.displayEntity(this.background1);
                    this.paddle1.displayEntity(this.background2);
                    this.paddle2.displayEntity(this.background1);
                    this.paddle2.displayEntity(this.background2);
            	}
            	
            	this.displayBorders(this.background1);
            	this.displayBorders(this.background2);
            	
            	this.player1.setDisplay(this.background1.getData());
            	this.player2.setDisplay(this.background2.getData());
            	
            	if(this.player1.isInterrupted())
            	{
            		this.player1 = new ClientHandle(this.serverSocket);
            		this.player1.setName("Player1");
            		this.player1.start();
            		this.currentDisplay = display.Lobby;
            	}
            	if(this.player2.isInterrupted())
            	{
            		this.player2 = new ClientHandle(this.serverSocket);
            		this.player2.setName("Player2");
            		this.player2.start();
            		this.currentDisplay = display.Lobby;
            	}
            }
        }
        catch (SocketException e)
        {
        	return;
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public void end()
    {
    	try
    	{
    		if(this.player1 != null)
    			this.player1.end();
    		if(this.player2 != null)
    			this.player2.end();
    		if(this.serverSocket != null)
    			this.serverSocket.close();
		}
    	catch (IOException e)
    	{
			e.printStackTrace();
			System.exit(-1);
		}
    }
}