import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MC1606 extends Plugin {

	public static MC1606 getInstance;
	Server server = etc.getServer();
	MC1606Listener listener = new MC1606Listener(this);
	HashSet<House> houses = new HashSet<House>();
	HashMap<String,HashSet<PlayerBlock>> playerBlocks = new HashMap<String,HashSet<PlayerBlock>>();
	HashSet<String> loggedIn = new HashSet<String>();
	BuildHouse buildhouse;
	
	String pathHouses;
	String pathThis;
	
	KeepDayMOFO mofo = new KeepDayMOFO();
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public MC1606()
	{
		MC1606.getInstance = this;
		
		//Paths
		pathThis 	= "MC1606\\";
		pathHouses 	= "MC1606\\Houses\\";
	}	
	
    public void initialize()
    {
        etc.getLoader().addListener(PluginLoader.Hook.ARM_SWING, listener, this, PluginListener.Priority.HIGH);
        etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.CHAT, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.LOW);
        etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, listener, this, PluginListener.Priority.LOW);
        etc.getLoader().addListener(PluginLoader.Hook.BLOCK_CREATED, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.ITEM_PICK_UP, listener, this, PluginListener.Priority.MEDIUM);
    }
	
	public void enable()
    {
		log("Starting MC1606...");
		//Check Dirs
		IOcheckDir(pathThis);
		IOcheckDir(pathHouses);
		
		//Load Stuff
//		loadPlayerblocks();
    }

	public void disable()
    {
		log("Shuting down MC1606...");
		//Save Stuff
//		savePlayerblocks();
		
		//Deactivate Housethreads
		for (House house:houses)
			House.quit(house);
		
		mofo.running = false;
    }
	
	public class MC1606Listener extends PluginListener
    {
        Item cur;
        HitBlox blox;
        MC1606 game;
        
        public MC1606Listener(MC1606 game)
        {
        	this.game = game;
        }
        
        public void onLogin(Player player)
        {
        	List<Player> playerlist = server.getPlayerList();
            String Onlineplayers = "";
            
            //Currently Online:
            for (int i = 0; i < playerlist.size(); i++)
            {
            	Player tmpplayer = playerlist.get(i);
            	if(tmpplayer != player) 
            		Onlineplayers += tmpplayer.getName();
            }
            
            if (Onlineplayers != "") {player.sendMessage("Currently online: " + Onlineplayers);}
            
            //Login
            String inputLine = null;
            String path = ("http://www.clock-work.at/minecraft/user/"+player.getName()+".txt");
            try{
	        	URL yahoo = new URL(path);
	        	BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.openStream()));
	        	inputLine = in.readLine();
            }catch (Exception e)
            {}
            
            log (inputLine+"|"+player.getIP()+"|");
            
            if (inputLine.equals(player.getIP()) || player.getIP().equals("127.0.0.1"))
            {
            }else{
            	player.kick("Login on Site! http://www.clock-work.at/minecraft/");
            }
        }

        public void onDisconnect(Player player)
        {}
        
        public boolean onItemPickUp(Player player, Item item) {
        	if (item.getItemId()==319)
        	{
        		return true;
        	}
        		
        	
            return false;
        }
        
        public boolean onInventoryChange(Player player)
        {

        	
            return false;
        }
        
        public void onArmSwing(Player player)
        {	
        	if (player.getItemInHand()==319){
        		player.sendMessage("TEST");
        		return;}
        	
            blox = new HitBlox(player, 50, 0.1);
            if(blox.getTargetBlock() != null)
            {
            	Block tmpblock = blox.getCurBlock();
            	if (tmpblock.getType() == 58)
            	{
            		player.kick("Don't use Workbenches!");
            		server.setBlockAt(0, tmpblock.getX(), tmpblock.getY(), tmpblock.getZ());
                }

            }

            switch(player.getItemInHand())
            {

            //GetBlockData
            case 41:
                blox = new HitBlox(player, 300, 0.3);
                Block block = blox.getTargetBlock();
                if(block != null)
                {
                	player.sendMessage(""+block.getX()+" "+block.getY()+" "+block.getZ());
                }
            	break;
            
            case 337:
        		blox = new HitBlox(player, 300, 0.3);
                block = blox.getTargetBlock();
                player.sendMessage(""+block.getData());
            	break;
                
            case 339: 
        		blox = new HitBlox(player, 300, 0.3);
                block = blox.getTargetBlock();
        	 	Block tblock = server.getBlockAt(block.getX(), block.getY(), block.getZ());
            	if (tblock.getType() == 64)
            	{
            		
            		for (String str:MC1606.getAdjacentDoorBlocks(tblock.getX(),tblock.getY(),tblock.getZ()))
            		{
            			String splitar[] = str.split(":");
            			int x = Integer.valueOf(splitar[0]);
            			int y = Integer.valueOf(splitar[1]);
            			int z = Integer.valueOf(splitar[2]);
            			
            			tblock = server.getBlockAt(x, y, z);
            		}
            	}
            	break;
            	
            case 340: 
                Location playerLoc = new Location();
                Location tempLoc = player.getLocation();
                blox = new HitBlox(player, 300, 0.3);
                if(blox.getTargetBlock() != null)
                {
                    for(int i = 0; i < 100; i++)
                    {
                        int cur = etc.getServer().getBlockAt(blox.getCurBlock().getX(), blox.getCurBlock().getY() + i, blox.getCurBlock().getZ()).getType();
                        int above = etc.getServer().getBlockAt(blox.getCurBlock().getX(), blox.getCurBlock().getY() + i + 1, blox.getCurBlock().getZ()).getType();
                        if(cur == 0 && above == 0)
                        {
                            playerLoc.x = (double)blox.getCurBlock().getX() + 0.5;
                            playerLoc.y = blox.getCurBlock().getY() + i;
                            playerLoc.z = (double)blox.getCurBlock().getZ() + 0.5;
                            playerLoc.rotX = tempLoc.rotX;
                            playerLoc.rotY = tempLoc.rotY;
                            player.teleportTo(playerLoc);
                            i = 100;
                        }
                    }

                }
                break;

//            case 280: 
//            	//Steal
//                Player WatchTarget = (Player)getTargetInFront(player, 2, 1, 0);
//                if(WatchTarget != null)
//                {
//                    playerConsumeInHand(player);
//                    Player WatchBack = (Player)getTargetInFront(WatchTarget, 2, 3, 0);
//                    if (WatchBack == null)
//                    { 
//                    	player.sendMessage("YOU GOT RICH!");
//                    }else if(WatchBack.getName() == player.getName()){
//                        player.sendMessage("Oh god he spotted you...");
//                    }
//                }
//                break;
           
            }
        }

        public boolean onChat(Player player, String message)
        {
        	for (Player p:server.getPlayerList())
        	{
        		if (MC1606.isNear(p.getLocation(), player.getLocation(), 30)) {p.sendMessage("<"+player.getName()+"> "+message);}
        	}
            return true;
        }

        public boolean onCommand(Player player, String split[]) 
        {
        	
        	//GetHouse repots
        	if(split[0].equalsIgnoreCase("/houses"))
        	{
        		for(House house:houses)
        		{
        		  player.sendMessage(house.owner+" "+house.blockCount);
        		}
        		return true;
        	}
        	
        	if(split[0].equalsIgnoreCase("/get"))
        	{
        		int i = 0;
	        	for(House house:houses)
	        	{
	        		if (split.length > 1)
	        		{
		        		 if (i == Integer.valueOf(split[1])) {house.getReport(player);}
		        		 i++;
	        		}else{
	        			player.sendMessage(""+house.hashCode());
	        		}
	        	}
        		return true;
        	}
        	
        	//Set Time
        	if(split[0].equalsIgnoreCase("/time"))
        	{
        		if(!player.isAdmin())
        		{
        			player.sendMessage("\247cYou don't have permission to do that!");
        			return true;
        		}
        		else
        		{
        			return false;
        		}
        	}
        	
        	//New Wave DOES NOT WORK ATM
//        	if(split[0].equalsIgnoreCase("/west"))
//        	{
//        		//hd mob = (hd)monstor.getMob(); mob.f = somePlayer;
//        		//nto (gd) and use public boolean a(dx paramdx, int paramInt)
//        		Location loc = player.getLocation();
//        		loc.x += 10;
//        		loc.z += 10;
//        		
//        		Mob mob1 = new Mob("Zombie", loc);
//        		mob1.spawn();
//        		
//        		gb mob = (gb)mob1.getMob();
//        		mob.j = null;
//        		mob.k = null;
//        		
//        		if (mob.a(player.getUser(), 2))
//        		{
//        			player.sendMessage("treu");
//        		}else{
//        			player.sendMessage("False");
//        		}
//        		
//        		player.sendMessage(""+mob1.getHealth()+" ");
//        		return true;
//        	}
        	
        	if(split[0].equalsIgnoreCase("/yy"))
        	{
        		/*player.sendMessage(""+player.getY());
        		server.setBlockAt(20, LtB(player.getX()), (int)player.getY(), LtB(player.getZ()));*/
        		
                double rot_x = (player.getRotation() + 90) % 360;

                int Ox = (int) Math.round(1 * Math.cos(Math.toRadians(rot_x)));
                int Oz = (int) Math.round(1 * Math.sin(Math.toRadians(rot_x)));
                
                player.sendMessage(""+Ox+""+Oz);
        		return true;
        	}
        	
        	if(split[0].equalsIgnoreCase("/ww"))
        	{
        		//if (moveth != null) {moveth.running = false; testnpc.delete(); testnpc.untrack(player); freeze(500);}
        		//testnpc = new NPC("",playerCenter(player.getX()),player.getY(),playerCenter(player.getZ()),(float)player.getRotation(),(float)player.getPitch(),1);
        		//moveth = new ThreadNPCMovement(testnpc);
        		server.setBlockAt(12, 0, 80, 0);
        		server.setBlockAt(12, 0, 81, 0);
        		player.teleportTo(0.5,80,0.5,player.getRotation(),player.getPitch());
        		return true;
        	}
        	
        	//Get Current Pos
        	if(split[0].equalsIgnoreCase("/pos"))
        	{
        		player.sendMessage((new StringBuilder()).append(MC1606.LtB(player.getX())).append(" ").append(MC1606.LtB(player.getY())).append(" ").append(MC1606.LtB(player.getZ())).toString());
        		return true;
        	}
        	
        	return false;
        }
        
        public boolean onBlockCreate(Player player, Block blockPlaced, Block blockClicked, int itemInHand)
        {

        	
        	//Workbenches Trick
        	if (blockPlaced.getType() == 58){
        		player.kick("Don't build workbenches!");
        		return true;
        	}
        	
        	//Unlock&&Lock Doors
        	if (blockClicked.getType() == 64)
        	{
        		if (server.isTimerExpired("LockUnlockDoor-"+player.getName()))
        		{
        			House tmphouse = isPartOfAHouse(blockClicked,false);
        			if (tmphouse != null)
        			{
        				tmphouse.openDoors();
        				houses.remove(tmphouse);
        				if (tmphouse.job != null) {tmphouse.job.running = false;}
        				
        				player.sendMessage("Unlocking...");
        			}else{
                		int x = blockClicked.getX();
                		int y = blockClicked.getY();
                		int z = blockClicked.getZ();
                		
                		buildhouse = new BuildHouse(player,x,y,z);
        			}
        			server.setTimer("LockUnlockDoor-"+player.getName(),0);
        		}else{
        			server.setTimer("LockUnlockDoor-"+player.getName(),5);
        		}
        		
        	}
        	
        	//House protection
        	House house = isPartOfAHouse(blockClicked,true); 
        	if ( !(house == null) )
        	{
        		//AllowDoors?
        		if (!(blockClicked.getType() == 64) || house.getFlag("closed"))
        			return true;
        	}
        		
            
        	//Track PlayerBlocks
//        	addPlayerblock(player, blockPlaced);
        	
            return false;
        }

        public boolean onBlockDestroy(Player player, Block block)
        {        
        	//Chest test
//        	if (block.getType() == 54 && block.getStatus()==0) {
//        		Chest cblock = (Chest)server.getComplexBlock(block.getX(), block.getY(), block.getZ());
//        		String str = "I:";
//
////        		for (hj item:cblock.getArray())
////        		{
////        			if (item != null){
////        				str += " [Type:"+item.c+" Count:"+item.a+"] ";
////        			}
////        		}
//        		player.sendMessage(str);
//        	}
        	
        	//House protection
        	House house = isPartOfAHouse(block,true); 
        	if ( !(house == null) )
        	{
        		//AllowDoors?
//        		if ((block.getType() != 64 || !house.getFlag("enterable"))&&block.getStatus()!=0)
        			return true;
        	}
            
        	//Track Playerblocks...
//            PlayerBlock playerblock = getPlayerblock(block);
//            if(block.getStatus() == 3 && playerblock != null)
//            {
//                (playerBlocks.get(playerblock.owner)).remove(playerblock);
//            }
        	
        	//PseudoWaterFix
            int druber = server.getBlockAt(block.getX(), block.getY() + 1, block.getZ()).getType();
            if(block.getStatus() == 3 && (druber == 8 || druber == 9))
            {
                player.sendMessage((new StringBuilder()).append(druber).toString());
                server.setBlockAt(0, block.getX(), block.getY() + 1, block.getZ());
            }

            return false;
        }
        
    }
	
    public PlayerBlock getPlayerblock(Block block)
    {
        Set<String> player = playerBlocks.keySet();
        for (Iterator<String> it = player.iterator(); it.hasNext();)
        {
            String curPlayer = (String)it.next();
            HashSet<PlayerBlock> blocks = playerBlocks.get(curPlayer);
            for (Iterator<PlayerBlock> pit = blocks.iterator(); pit.hasNext();)
            {
                PlayerBlock curBlock = (PlayerBlock)pit.next();
                if(curBlock.x == block.getX() && curBlock.y == block.getY() && curBlock.z == block.getZ())
                    return curBlock;
            }

        }

        return null;
    }
	
	public void addPlayerblock(Player player, Block block)
    {
        if(playerBlocks.get(player.getName()) == null)
            playerBlocks.put(player.getName(), new HashSet<PlayerBlock>());
        int dur = 0;
        switch(block.getType())
        {
        case 1: // '\001'
            dur = 15;
            break;

        case 2: // '\002'
            dur = 5;
            break;

        case 3: // '\003'
            dur = 5;
            break;

        case 4: // '\004'
            dur = 15;
            break;

        case 5: // '\005'
            dur = 10;
            break;

        case 12: // '\f'
            dur = 8;
            break;

        case 13: // '\r'
            dur = 20;
            break;

        case 17: // '\021'
            dur = 10;
            break;

        case 45: // '-'
            dur = 50;
            break;
        }
        ((HashSet<PlayerBlock>)playerBlocks.get(player.getName())).add(new PlayerBlock(block, player, dur));
    }
	
    @SuppressWarnings("unused")
	private void loadPlayerblocks()
    {
        try
        {
            FileInputStream fis = new FileInputStream(pathThis+"playerblocks.dat");
            ObjectInputStream o = new ObjectInputStream(fis);

			@SuppressWarnings("unchecked")
			HashMap<String, HashSet<PlayerBlock>> load = (HashMap<String, HashSet<PlayerBlock>>) o.readObject();
            playerBlocks = load;
            o.close();
        }
        catch(Exception e)
        {
            log("No Playerblocks found...");
            playerBlocks = new HashMap<String, HashSet<PlayerBlock>>();
        }
    }
    
    @SuppressWarnings("unused")
	private void savePlayerblocks()
    {
        try
        {
            FileOutputStream f_out = new FileOutputStream(pathThis+"playerblocks.dat");
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(playerBlocks);
        }
        catch(Exception e)
        {
        	log("ERROR: Saving Playberlocks "+e.getMessage());
        }
    }
	
	static public void log(String str)
	{
		log.log(Level.INFO, str);
	}
	
	static public ArrayList<String> getAdjacentDoorBlocks(int x, int y, int z)
	{
		ArrayList<String> doors = new ArrayList<String>();
		y--; x--; z--;
		
		for (int fx=0;fx<=2;fx++)
		{
			for (int fz=0;fz<=2;fz++)
			{
				for (int fy=0;fy<=2;fy++)
				{
					if (etc.getServer().getBlockAt((x+fx), (y+fy), (z+fz)).getType() == 64) {doors.add((x+fx)+":"+(y+fy)+":"+(z+fz));}
				}
			}
		}
		
		return doors;
	}
	
	static public Block getAdjacentBlockType(int x, int y, int z, int t, int r)
	{
		Block tmpblock;
		x=Integer.valueOf(x-(r/2));
		y=Integer.valueOf(y-(r/2));
		z=Integer.valueOf(z-(r/2));
		r++;
		
		for (int i=0;i<r;i++)
		{
			tmpblock = etc.getServer().getBlockAt(x+i, y, z);
			if (tmpblock.getType()==t)
				return etc.getServer().getBlockAt(x+i, y, z);
			
			for (int j=0;j<r;j++)
			{
				tmpblock = etc.getServer().getBlockAt(x+i, y+j, z);
				if (tmpblock.getType()==t)
					return etc.getServer().getBlockAt(x+i, y+j, z);
				
				for (int k=0;k<r;k++)
				{
					tmpblock = etc.getServer().getBlockAt(x+i, y+j, z+k);
					if (tmpblock.getType()==t)
						return etc.getServer().getBlockAt(x+i, y+j, z+k);
				}
			}
		}
		
		return null;
	}
	
    static public int getHighestBlockY(int x, int z)
    {
		for (int i = 128; i > 0; i--)
		{
			if (isBlockSolid(etc.getServer().getBlockAt(x, i, z).getType()))
			{
    			return i+1;
			}
		}
		return 0;
    }
	
	public House isPartOfAHouse(Block block, boolean ignoreType)
	{
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		int t = block.getType();
		
		for (House house:houses)
		{
			if (ignoreType)
			{
				if (house.isPartOf(x, y, z)) {return house;}
			}else{
				if (house.isPartOf(x, y, z, t)) {return house;}
			}
		}
		return null;
	}
	
	static public boolean isNear(Location l1, Location l2, double i)
    {
        double x1 = l1.x;
        double y1 = l1.y;
        double z1 = l1.z;
        double x2 = l2.x;
        double y2 = l2.y;
        double z2 = l2.z;
        return Math.abs(x1 - x2) <= i && Math.abs(y1 - y2) <= i && Math.abs(z1 - z2) <= i;
    }
	
    static public boolean isBlockSolid(int t)
    {
    	if ( (t==0)||(t==50) )
    		return false;
    	
    	return true;
    }
    
    static public Block getBlock(String xyz)
    {
    	if (!xyz.isEmpty())
    	{
			String tbsplit[] = xyz.split(":");
			int x = Integer.valueOf(tbsplit[0]);
			int y = Integer.valueOf(tbsplit[1]);
			int z = Integer.valueOf(tbsplit[2]);
			
			return etc.getServer().getBlockAt(x, y, z);
    	}
    	
    	return null;
    }
    
    static public int LtB(double d)
    {
        return (int)Math.round(d - 0.5D);
    }
    
    static public double BtL(int i)
    {
        return (double)i + 0.5;
    }
    
	private void IOcheckDir(String path)
	{
		File dir = new File(path);
		if (!dir.exists())
		{
			dir.mkdir();
		}
	}

	public static int getMaximum(HashMap<Integer, Integer> list)
	{
    	int key = 0; int val = 0;
    	for( Map.Entry<Integer, Integer> entry : list.entrySet() )
    	{
    	  int tkey = entry.getKey();
    	  int tval = entry.getValue();
    	  if (tval > val)
    	  {
    		  key = tkey;
    		  val = tval;
    	  }
    	}
		return key;
	}

	public static int getDistance(Block b1, Block b2)
	{
		int max = 0;
		int maxstep;
		
		maxstep = Math.abs(b1.getX()-b2.getX()); 
		if (maxstep>max)
			max = maxstep;
		
		maxstep = Math.abs(b1.getZ()-b2.getZ()); 
		if (maxstep>max)
			max = maxstep;
		
		return max;
	}
}
