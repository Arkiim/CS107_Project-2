<h1> EPFL - Computer Science BA1 | Java Project n°2 </h1>
<h2> Zelda/Pokemon like grid-game </h2>
	
CONTROLS :

	Play : 
	    - arrows : move the player
	    - tab : change inventory item
	    - space : use item
	    - E : interact from view/go to next dialog window
	    - A/B : make choice when talking to the mayor
	
	Test / Debug : 
	 (Maintain:) SHIFT+ (Press:) O+P : Enable debug Mode to use the following features :
	    - L : make a LogMonster appear faced Up, in front of the player
	    - K : make a FlameSkull appear faced Up, in front of the player
	    - B : make a Bomb appear faced Up, in front of the player
	    - F : make Fire appear faced Up, in front of the player
	    - Z : make a Zombie appear near you,faced Up, in front of the player
	    - I : add sword, bow, arrow, bomb, castle key to the inventory	
	    		
________________________________________________________________________________________________
	
PLAYER INFO :

    - starts with only a sword in the inventory
    - max hp : 6.0f
				
WEAPON INFO :

	- Arrow (thrown by bow) : 
        - attack type : PHYSICAL
        - damage : 1.5f
				  
	- Sword : 
        - attack type : PHYSICAL
	    - damage : 2.0f
		
	- Magic Projectile (thrown by staff) : 
	    - attack type : MAGICAL
	    - damage : 2.0f
	    
	- Bomb :
	    - attack type : PHYSICAL
	    - damage : 4.0f 
	    (can hurt the player if he stays to close to it)
			
											   
________________________________________________________________________________________________

MONSTER INFO :

    - FlameSkull : 
            - hp : 2.0f
	        - Vulnerabilities : PHYSICAL, MAGICAL
	        - damage : 0.5f
	        - dies after a certain time
					 
    - LogMonster :  
            - hp : 6.0f
	        - Vulnerabilities : PHYSICAL, FIRE
	        - damage : 2.0f
            - runs into a straight line after seeing you, 
	        - has period of inactivity
	        - drops a coin after bein defeated

	- DarkLord : 
	        - hp : 35.0f
            - Vulnerability : MAGICAL
            - can't be attacked in closed fight (teleports)
	        - you need the staff to defeat him
            - makes FireSpell (makes you 0.5f when you step on it and by cycle when standing on it) and FlameSkull appear
					 
	- Zombie : 
            - hp : 8.0f 
            - Vulnerabilities : PHYSICAL, MAGICAL
            - damage : 1.0f 
	        - when reaching 2 or less hp, becomes a static grave for a given time before healing and moving again as a zombie
	        - you need to destroy the grave to kill the zombie
            - as a grave it prevents some attacks at random depending on a fixed probability (0.2 chance to hurt it)
	        - drops a heart after being defeated
				   
________________________________________________________________________________________________

WALKTHROUGH :

		- you start in Village, you cannot move already
		because the mayor comes to talk to you
		- you can choose to help the mayor after he's finished talking (A : yes B : no)
		- if you accept you win 400 gold
		- if you don't the mayor becomes a zombie, (we do not recommand this option)
		- then you need to go (up) to Ferme, enter the home that's near the zombie
		- open the box in the house, collect the items it releases by walking on them
		(1 bow, 45 arrows, 5 bombs)
		- you should save the arrows as you'll need them later
		- go up then right to Route
		- you'll find there some monsters you're not forced to fight
		- go down use the bow to throw an arrow at the orb
		- that will make a bridge appear that will allow you to go across the river
		- walk on the bridge
		- continue right you'll reach RouteTemple
		- enter the building you'll find there, you'll reach Temple
		- go on the tile with the drawing (it is a teleporter
		- press E to activate the lever to your right, you'll transported to
		GrotteDonjon where you'll find 4 zombies you need to defeat
		- defeating them will trigger a signal that will make a box appear
		- open the box with E/Space bar
		- the box drops the staff, collect it by walking on it
		- go back next to lever, use it by pressing E, you'll be transported back to the temple
		- go back to RouteTemple, then Route, go up, take the stone bridge
		- you'll reach RouteChateau
		- there you'll find DarkLord, 3 zombies
		- use the staff to attack and defeat the DarkLord 
		(staff is the only effective weapon against him)
		- a key will be dropped where he standed when you defeated him
		- walk on the key to collect it, it will allow to open the castle door
		- open the castle door by pressing E, then you can step inside
		- you'll reach the castle
		- you just finished the game
		
________________________________________________________________________________________________
		
ADDED SPRITES :

	https://imgur.com/r/FNaFb/aOtkv
	https://opengameart.org/content/zelda-like-tilesets-and-sprites
	https://vryell.itch.io/tiny-adventure-pack
	
	other added sprites have been made by us or are edits of the sprites of the "maquette"
