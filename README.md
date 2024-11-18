# PONG
Play the iconic retro game with your friends on command prompt

# Requirements
Currently only supports Windows OS. ASCII Engine, the game engine used to build pong currently only supports windows.

# How to play
- Start the game by double clicking Pong.exe on your PC.
- Navigate through the menu using UP, DOWN and ENTER Keys.
- To play with another player, one player should host the game and the other player should join using IP Address displayed for the host. MAKE SURE BOTH PLAYERS ARE CONNECTED TO SAME NETWORK.
- If both players are not connected to same network, then both players can install LogMeIn Hamachi (Note:- Using Hamachi comes with it's risks such as anonymous users joining your network. Use it cautiously). Hamachi connects both players PC through network tunneling.
- When ready both players can Toggle Ready, when both players are ready the game will automatically start after a 5 second countdown. If any player Toggle's Ready in the 5 second countdown, the countdown will be force stopped.
- After the 5 second countdown, both the players will enter the game. The player who hosted the game will be on the left side and the player who joined will be on the right side.
- The ball will initially start from centre of game and move towards the left. After any of the player misses the ball, the ball will be reset to the center and move towards the person who missed.
- Currently there is no end game, you can just close the game and the other person will be kicked to lobby.

# Known Minor Issues
- The player who hosted may sometimes be considered as player 2.
- Ball Trajectory may rarely not change after hitting the paddle.
- Game listens to key inputs even when game window is not focussed.

# Known Major Issues
- When the player who joined quits, the host player's game may very rarely crash.

# Future Considerations
- Add a end game.
- Right now the server sends entire video data to both players which is very inefficient and can cause bandwidth issues on weak networks. to optimise the logic and send only the necessary data to both players.
- Right now the server hosted on player 1's PC, to provide the ability to host server on another computer.
- Right now only supports LAN Multiplayer, to provide WAN Multiplayer.
