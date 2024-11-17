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
    private ClientHandle[] players;
    private Canvas[] backgrounds;
    private Entity[] paddles;
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
    
    private int[] selectPointers;
    private boolean[] readyStatus;
    
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
		String[] playerNames = new String[2], readyMessages = new String[2];
		for(int i = 0; i < 2; ++i)
		{
			playerNames[i] = (this.players[i].getPlayerName() == null) ? "Waiting to Join..." : this.players[i].getPlayerName();
			readyMessages[i] = (this.readyStatus[i] == true) ? "Ready" : "Not Ready";
		}
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
		
		for(int i = 0; i < 2; ++i)
		{
			background.insertString(playerNames[i],
					this.getMiddleXPosition(background.getWidth(), width) + 2,
					(i == 0) ? 6 : 8,
					playerNames[i].length(),
					1);
			if(this.players[i].getPlayerName() != null)
				background.insertString(readyMessages[i],
						this.getMiddleXPosition(background.getWidth(), width) + width - readyMessages[i].length() - 2,
						(i == 0) ? 6 : 8,
						readyMessages[i].length(),
						1);
		}
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
	
	private void displayLevel(Canvas background)
	{
		background.insertYLine('|',
				this.getMiddleXPosition(background.getWidth(), 1),
				0,
				this.getMiddleXPosition(background.getWidth(), 1),
				background.getHeight() - 1);
	}
	
	private void startGame()
	{
		String str = "Starting in ";
		if(this.readyStatus[0] && this.readyStatus[1])
		{
			for(Canvas background : this.backgrounds)
				background.insertString(str + String.valueOf(this.countdown),
						this.getMiddleXPosition(background.getWidth(), str.length() + 1),
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
		{
			this.currentDisplay = display.Level;
			
			for(int i = 0; i < 2; ++i)
			{
				this.readyStatus[i] = false;
				this.selectPointers[i] = 0;
			}
			
			this.ball.startSimluation();
		}
	}
	
	private void handleLobbyInput(NativeKeyEvent e, ClientHandle player)
	{
		if(e == null)
			return;
		int index = 0;
		if(player.equals(this.players[1]))
			index = 1;
		switch(e.getKeyCode())
		{
			case NativeKeyEvent.VC_UP:
				this.selectPointers[index] = Math.max(this.selectPointers[index] - 1, 0);
				break;
			case NativeKeyEvent.VC_DOWN:
				this.selectPointers[index] = Math.min(this.selectPointers[index] + 1, LobbySelect.values().length - 1);
				break;
			case NativeKeyEvent.VC_ENTER:
				if(LobbySelect.values()[this.selectPointers[index]] == LobbySelect.Toggle_Ready)
					this.readyStatus[index] = !this.readyStatus[index];
				else
				{
					this.selectPointers[index] = 0;
					this.readyStatus[index] = false;
					this.players[index].end();
					
					if(index == 0)
					{
						this.exitStatus = true;
						this.end();
					}
				}
				break;
		}
	}
	
	private void handleLevelInput(NativeKeyEvent e, Entity paddle)
	{
		if(e == null)
			return;
		switch(e.getKeyCode())
		{
			case NativeKeyEvent.VC_DOWN:
				paddle.setPosition(paddle.getXPosition(),
						Math.min(paddle.getYPosition() + 1, this.backgrounds[0].getHeight() - paddle.getHeight() - 1));
				break;
			case NativeKeyEvent.VC_UP:
				paddle.setPosition(paddle.getXPosition(),
						Math.max(paddle.getYPosition() - 1, 1));
				break;
		}
	}
	
	private void handleBallTrajectory(Entity ball, Entity paddle)
	{
		if(!ball.detectCollision(paddle))
			return;
		if((ball.getXPosition() - paddle.getXPosition()) * ball.getHorVelocity() > 0)
			return;
		
		int paddleHalfLength = (int) Math.floor(paddle.getHeight() / 2);
		int paddleHitLocation = (ball.getYPosition() - paddle.getYPosition()) + 1;
		double paddleVerVelocityChange = 0;
		
		if(paddleHitLocation <= paddleHalfLength)
			paddleVerVelocityChange = -paddleHalfLength / paddleHitLocation;
		else if((paddle.getHeight() % 2 == 0 && paddleHitLocation > paddleHalfLength) 
				|| 
				(paddle.getHeight() % 2 != 0 && paddleHitLocation > paddleHalfLength + 1))
			paddleVerVelocityChange = paddleHalfLength / ((paddle.getHeight() - paddleHitLocation) + 1);
		
		ball.setVerVelocity(ball.getVerVelocity() + paddleVerVelocityChange);
	}
    
    public void start()
    {
    	this.players = new ClientHandle[2];
    	this.backgrounds = new Canvas[2];
    	this.paddles = new Entity[2];
    	this.ball = new Entity();
    	this.selectPointers = new int[2];
    	this.readyStatus = new boolean[2];
    	
        try
        {	
        	this.serverSocket = new ServerSocket(12345);
        	
        	for(int i = 0; i < 2; ++i)
        	{
        		this.backgrounds[i] = new Canvas(120, 30);
        		
        		this.paddles[i] = new Entity();
        		this.paddles[i].setDimensions(1, 5);
        		this.paddles[i].setPosition((i == 0) ? 1 : this.backgrounds[i].getWidth() - 1, 
            			this.getMiddleYPosition(this.backgrounds[i].getHeight(), this.paddles[i].getHeight()));
        		this.paddles[i].setPaintChar((i == 0) ? ']' : '[');
        	}
        	
        	this.ball.setPaintChar('*');
        	this.ball.setPosition(60, 15);
			this.ball.setHorVelocity(-6);
			
			for(int i = 0; i < 2; ++i)
			{
				this.players[i] = new ClientHandle(this.serverSocket);
				this.players[i].setName("Player" + String.valueOf(i + 1));
			}
            
            this.players[0].start();
            try 
            {
				Thread.sleep(100);
			}
            catch (InterruptedException e)
            {
				e.printStackTrace();
			}
            this.players[1].start();
            
            while(!this.exitStatus)
            {
            	for(Canvas background : this.backgrounds)
            		background.clearCanvas();
            	
            	if(this.currentDisplay == display.Lobby)
            	{
            		for(int i = 0; i < 2; ++i)
            		{
            			this.handleLobbyInput(this.players[i].getPlayerInput(), this.players[i]);
                		this.players[i].setPlayerInput(null);
                		
                		this.displayServerIP(this.backgrounds[i]);
                		this.displayLobby(this.backgrounds[i]);
                		
                		for(int j = 0; j < LobbySelect.values().length; ++j)
                    		this.displayEnum(LobbySelect.values()[j], 0, 2 * j, this.backgrounds[i], this.selectPointers[i] == j);
                		
                		this.displayBorders(this.backgrounds[i]);
            		}
                	
                	this.startGame();
            	}
            	else if(this.currentDisplay == display.Level)
            	{	
            		if(System.currentTimeMillis() - this.prevTime > 150)
            		{
            			this.prevTime = System.currentTimeMillis();
            			for(int i = 0; i < 2; ++i)
            				this.handleLevelInput(this.players[i].getPlayerInput(), this.paddles[i]);
            		}
                    
            		for(int i = 0; i < 2; ++i)
            		{
            			this.displayLevel(this.backgrounds[i]);
            			
            			for(Entity paddle : this.paddles)
            				paddle.displayEntity(this.backgrounds[i]);
            			
            			this.ball.undoClipping(this.paddles[i], 3, false);
            			
            			this.handleBallTrajectory(this.ball, this.paddles[i]);
            			
            			this.ball.bounceOf(this.paddles[i]);
            			
            			this.ball.displayEntity(this.backgrounds[i]);
            		}
            	}
            	
            	for(int i = 0; i < 2; ++i)
            	{
            		this.players[i].setDisplay(this.backgrounds[i].getData());
            		if(this.players[i].isInterrupted())
                	{
                		this.players[i] = new ClientHandle(this.serverSocket);
                		this.players[i].setName("Player" + String.valueOf(i + 1));
                		this.players[i].start();
                		this.currentDisplay = display.Lobby;
                	}
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
    		for(ClientHandle player : this.players)
    			if(player != null)
    				player.end();
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