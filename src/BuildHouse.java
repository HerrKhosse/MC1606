import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

public class BuildHouse extends Thread
    {
    	//Thread Stuff
        boolean running = false;
        Player player;
        Server server;
        MC1606 game;
        
        House house;
        
        //Global Stuff
        ArrayList<HashSet<String>> rooms = new ArrayList<HashSet<String>>();
        HashSet<String> doors = new HashSet<String>();
        ArrayList<String> entries = new ArrayList<String>();

        HashSet<String> edges = new HashSet<String>();
        
        //Queue/Stack Stuff
        Stack<String> doorStack = new Stack<String>();
        
        public BuildHouse(Player player, int x, int y, int z)
        {
        	game = MC1606.getInstance;
        	server = etc.getServer();
        	
        	this.player = player;
        	house = new House(player.getName());
        	
        	//Begin
        	doorStack.push(XYZ(x,y,z));
            
            running = true;
            this.start();
        }
        
        public void run()
        {
        	step1Start();
        	step4Finalize();
        }

		private void step1Start()
		{
			//Do for every added Doorblock
        	while (!doorStack.empty())
        	{
        		String door = doorStack.pop(); 
        		
        		String split[] = door.split(":");
        		int x = Integer.valueOf(split[0]);
        		int y = Integer.valueOf(split[1]);
        		int z = Integer.valueOf(split[2]);
            	
            	//Try to fixate the whole door, not only one block
            	for (String Tdoor:MC1606.getAdjacentDoorBlocks(x,y,z)) {doors.add(Tdoor);}
            	
            	//Get the direction of the door and only process these rooms
            	int tx = 0, ty = 0, tz = 0, tt = 0;
            	if (!MC1606.isBlockSolid(server.getBlockAt(x, y, z+1).getType()) && !MC1606.isBlockSolid(server.getBlockAt(x, y, z-1).getType()))
            	{
            		tx = x; ty = y; tz = z+1; tt = server.getBlockAt(tx,ty,tz).getType();
            		if (!belongsToARoom(tx,ty,tz,tt)) {step2CreateRoom(door,tx,ty,tz);}
            		
            		tx = x; ty = y; tz = z-1; tt = server.getBlockAt(tx,ty,tz).getType();
            		if (!belongsToARoom(tx,ty,tz,tt)) {step2CreateRoom(door,tx,ty,tz);}
            	}
            	else if (!MC1606.isBlockSolid(server.getBlockAt(x+1, y, z).getType()) && !MC1606.isBlockSolid(server.getBlockAt(x-1, y, z).getType()))
            	{
            		tx = x+1; ty = y; tz = z; tt = server.getBlockAt(tx,ty,tz).getType();
            		if (!belongsToARoom(tx,ty,tz,tt)) {step2CreateRoom(door,tx,ty,tz);}
            		
            		tx = x-1; ty = y; tz = z; tt = server.getBlockAt(tx,ty,tz).getType();
            		if (!belongsToARoom(tx,ty,tz,tt)) {step2CreateRoom(door,tx,ty,tz);}
            	}
        	}
		}

		private void step2CreateRoom(String bdoor, int bx, int by, int bz)
        {        	
    		String bsplit[] = bdoor.split(":");
    		int dx = Integer.valueOf(bsplit[0]);
    		int dy = Integer.valueOf(bsplit[1]);
    		int dz = Integer.valueOf(bsplit[2]);
        	
        	boolean leaked = false;
        	
        	//Temp
        	String tmpEntry = bx+":"+by+":"+bz;
        	ArrayList<String> tmpDoors = new ArrayList<String>();
        	Stack<String> blockStack = new Stack<String>();
        	
        	//Globals
        	int blockCount = 0;
        	HashSet<String> blocks = new HashSet<String>();
        	HashMap<Integer,Integer> itemList = new HashMap<Integer,Integer>();
        	HashMap<Integer,Integer> blockList = new HashMap<Integer,Integer>();
        	
        	blockStack.push(XYZ(bx,by,bz));
        	
        	//Trying to fixate the whole door not only one doorblock
        	for (String Tdoor:MC1606.getAdjacentDoorBlocks(dx,dy,dz))
        	{
        		blocks.add(Tdoor+":64");
        		//Add Blocks Over and Below a doorblock
        		String split[] = Tdoor.split(":");
        		int x = Integer.valueOf(split[0]);
        		int y = Integer.valueOf(split[1]);
        		int z = Integer.valueOf(split[2]);
        		
        		blocks.add(XYZT(x,y+1,z,server.getBlockAt(x, y+1, z).getType()));
        		blocks.add(XYZT(x,y-1,z,server.getBlockAt(x, y-1, z).getType()));
        	}
        	
        	Step:
        	while (blockStack.size() > 0 && !leaked)
        	{
        		//Get Block-Coordinates
        		String block = blockStack.pop();
        		
        		String split[] = block.split(":");
        		int x = Integer.valueOf(split[0]);
        		int y = Integer.valueOf(split[1]);
        		int z = Integer.valueOf(split[2]);
        		int t = server.getBlockAt(x, y, z).getType();
        		
        		//Quit if block was already processed
        		if (blocks.contains(XYZT(x,y,z,t))) {continue Step;}
                blocks.add(XYZT(x,y,z,t));
                
                //If the block under current block is the highest, it has to be leaked, break the while
                if (server.getBlockAt(x, y-1, z).getY() == MC1606.getHighestBlockY(x, z))
                {
                	leaked = true;
                	break Step;
                }
                
                //Get if that Block is solid
                if (MC1606.isBlockSolid(t))
                {
                	//Add to blockList
                	int blockListCount = 0;
                	if (blockList.containsKey(t))
                	{
                		blockListCount = blockList.get(t);
                	}
                	blockListCount++;
                	blockList.put(t, blockListCount);
                	
                	//If its a door, add it in a temporary collection
                	if (t == 64 && server.getBlockAt(x, y-1, z).getType() != 64 && server.getBlockAt(x, y-1, z).getType() != 0)
                	{
                		if (server.getBlockAt(x, y+1, z).getType() == 64)
                		{
                			if (!doors.contains(XYZ(x,y,z)))
                			{
                				tmpDoors.add(XYZ(x,y,z));
                			}
                		}
                	}
                	
                	//If its a chest, add its contents to items
//                	if (t == 54)
//                	{
//                		Chest chest = (Chest)server.getComplexBlock(x,y,z);
//                		for (hj item:chest.getArray())
//                		{
//                			if (item != null){
//                            	int itemListCount = item.a;
//                            	if (itemList.containsKey(item.c))
//                            	{
//                            		itemListCount += itemList.get(item.c);
//                            	}
//                            	itemList.put(item.c, itemListCount);
//                			}
//                		}
//                	}
                	
                	continue Step;
                }
                
                //Add all blocks around that block if its a transparent one
                blockStack.push( XYZ((x+1),y,(z+1)) );
                blockStack.push( XYZ((x-1),y,(z+1)) );
                blockStack.push( XYZ((x+1),y,(z-1)) );
                blockStack.push( XYZ((x-1),y,(z-1)) );
                
                blockStack.push( XYZ((x+1),y,z) );
                blockStack.push( XYZ((x-1),y,z) );
                blockStack.push( XYZ(x,y,(z+1)) );
                blockStack.push( XYZ(x,y,(z-1)) );
                blockStack.push( XYZ(x,(y+1),z) );
                blockStack.push( XYZ(x,(y-1),z) );
                
                blockCount++;
                
        	}
        	
        	//If there is no leak
        	if (!leaked)
	        {
	        	//ping( "Room size: "+blockCount);
	        	rooms.add(blocks);
	        	house.addRoom( new Room(blockCount,blocks,blockList,itemList) );
	        		
	        	//Add the temporary doors finally to the doorqueue
	        	for (String tmpDoor:tmpDoors)
	        	{
	        		doorStack.push(tmpDoor);
	        	}
        	}else{
        		entries.add(tmpEntry);
        		//ping("Leaked!");
        	}
        }

        private void step4Finalize()
        {
        	if (house.blockCount > 1)
        	{
        		player.sendMessage("Locking...");
        		
        		house.setDoors(doors);
        		house.setEntries(entries);
        		
        		if (!house.isSomebodyInside())
        		{
        			step3SetJob();
        			
        			//Close door
        			house.closeDoors();
        			
	        		//Test a Random Block against other houses
	        		int size = house.rooms.get(0).blocks.size();
	        		int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
	        		int i = 0;
	        		
	        		String str = "";
	        		for(String obj : house.rooms.get(0).blocks)
	        		{
	        		    if (i == item)
	        		        str = obj;
	        		    i = i + 1;
	        		}
	        		
	        		if (str!="")
	        		{
		        		String tbsplit[] = str.split(":");
		        		int x = Integer.valueOf(tbsplit[0]);
		        		int y = Integer.valueOf(tbsplit[1]);
		        		int z = Integer.valueOf(tbsplit[2]);
		        		int t = Integer.valueOf(tbsplit[3]);
		        		
		        		for (House ehouse:game.houses)
		        		{
		        			if (ehouse.isPartOf(x, y, z, t))
		        			{
			        			game.houses.remove(ehouse);
		        			}
		        		}

		        		game.houses.add(house);
		        		house.getReport(player);
	        		}
        		}else{
        			player.sendMessage("Get out of there!");
        		}
        	}else{
        		player.sendMessage("No room");
        	}
		}
		
        private void step3SetJob()
        {
        	//Forced to add every extended Class per Hand, stupid java
    		if (Doorery.fits(house))
    			house.job = new Doorery(house);
			
    		if (Marketplace.fits(house))
    			house.job = new Marketplace(house);
		}

		public boolean belongsToARoom(int x, int y, int z, int t)
        {
        	for (HashSet<String> room : rooms) {if (room.contains(XYZT(x,y,z,t))) {return true;}}
        	return false;
        }
        
    	public String XYZ(int x,int y,int z)
    	{
    		return (x+":"+y+":"+z);
    	}

    	public String XYZT(int x,int y,int z,int t)
    	{
    		return (x+":"+y+":"+z+":"+t);
    	}
    }