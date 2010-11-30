import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class House implements Serializable
{
	
	String owner;
	
	int blockCount;
	int emptyCount;
	
	int mostUsedBlock;
	
	HashMap<Integer,Integer> blockList = new HashMap<Integer,Integer>();
	HashSet<String> flags = new HashSet<String>();
	
	ArrayList<String> entries = new ArrayList<String>();
	ArrayList<Room> rooms = new ArrayList<Room>();
    HashSet<String> doors = new HashSet<String>();
    HashSet<String> protect = new HashSet<String>();
    HouseJob job = null;
    
    public House() {}
	public House(String owner)
	{
		this.owner = owner;
		blockCount = 0;
		emptyCount = 0;
	}
	
	public void addRoom(Room room)
	{
		rooms.add(room);
		
		blockCount += room.blockCount;
		emptyCount += room.emptyCount;
		
		//Merge blockList
		for( Map.Entry<Integer, Integer> entry : room.blockList.entrySet() )
		{
		  int key = entry.getKey();
		  int val = entry.getValue();
		  
		  if (blockList.containsKey(key))
			  val += blockList.get(key);
		  
		  blockList.put(key, val);
		}
		
		this.mostUsedBlock = MC1606.getMaximum(blockList);
	}
	
	public void setEntries(ArrayList<String> entries)
	{
		this.entries = entries;
	}
	
	public void setDoors(HashSet<String> doors)
	{
		this.doors = doors;
	}
	
	public void getReport(Player player)
	{
		String tmp = " ";
		tmp += "["+rooms.size()+"] "+mostUsedBlock+" - ";
		
//		for (Room room:rooms)
//		{
//			//player.sendMessage("-> Free:"+room.emptyCount);
//			//player.sendMessage("-> Bloc"+room.blockCount);
//		}
		
		//player.sendMessage("Doors: "+doors.size());
		//player.sendMessage("Entries: "+entries.size());
		
		if (job != null){tmp += " "+job.toString();}else{tmp += "NoJob";}
		player.sendMessage(Colors.Gray+tmp);
	}
	
	public int hashCode()
	{
		return (rooms.hashCode()+doors.hashCode());
	}
	
	public boolean equals(Object o)
	{
		if (o.hashCode() == this.hashCode()) {return true;}
		return false;
	}
	
	public boolean isPartOf(int x, int y, int z, int t)
	{
		if (t == 64 && doors.contains(x+":"+y+":"+z))
			return true;
		
		if (protect.contains(x+":"+y+":"+z))
			return true;
		
		for (Room room:rooms)
		{
			if (room.blocks.contains(x+":"+y+":"+z+":"+t)) {return true;}
		}
		
		return false;
	}
	
	public int getBlockAmount(int t)
	{
		int amount = 0;
		for (Room room:rooms)
		{
			if (room.blockList.containsKey(t))
				amount += room.blockList.get(t);
		}
		return amount;
	}
	
	public int getItemAmount(int t)
	{
		int amount = 0;
		for (Room room:rooms)
		{
			if (room.itemList.containsKey(t))
				amount += room.itemList.get(t);
		}
		return amount;
	}
	
	public boolean getFlag(String str) {return flags.contains(str);}
	public void setFlag(String str) {flags.add(str);}
	
	public boolean isPartOf(int x, int y, int z)
	{
		if (doors.contains(x+":"+y+":"+z)) {return true;}
		for (Room room:rooms)
		{
			for (String str:room.blocks)
			{
				if (str.startsWith(x+":"+y+":"+z)) {return true;}
			}
		}
		
		return false;
	}
	
	public boolean isInside(Player player)
	{
		int x = LtB(player.getX());
		int y = (int) player.getY();
		int z = LtB(player.getZ());
		int t = etc.getServer().getBlockIdAt(x, y, z);
		
		for (Room room:rooms)
		{
			if (room.blocks.contains(x+":"+y+":"+z+":"+t)) {return true;}
		}
		return false;
	}
	
	public boolean isSomebodyInside()
	{
		for (Player player:etc.getServer().getPlayerList())
		{
			if (this.isInside(player)) {return true;}
		}
		return false;
	}
	
	public void closeDoors()
	{
		for (String dat:doors)
		{
			String split[] = dat.split(":");
			closeDoor(etc.getServer().getBlockAt( Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]) ));
		}
	}
	
	public void openDoors()
	{
		
	}
	
	static public void closeDoor(Block block)
	{
		int data = block.getData();
		int direction;
		if (etc.getServer().getBlockAt(block.getX()+1, block.getY(), block.getZ()).getType() == 0)
		{direction = 0;}else{direction = 1;}
		
		if (direction==0)
		{
	        switch(data)
	        {
	        case 14: data = 10; break;
	        case 4: data = 0; break;
	        case 12: data = 8; break;
	        case 6: data = 2; break;
	        case 1: data = 5; break;
	        case 9: data = 13; break;
	        case 3: data = 7; break;
	        case 11: data = 15; break;
	        }
		}
		
		if (direction==1)
		{
	        switch(data)
	        {
	        case 10: data = 14; break;
	        case 0: data = 4; break;
	        case 8: data = 12; break;
	        case 2: data = 6; break;
	        case 5: data = 1; break;
	        case 13: data = 9; break;
	        case 7: data = 3; break;
	        case 15: data = 11; break;
	        }
		}

		etc.getServer().setBlockData(block.getX(), block.getY(), block.getZ(), data);
	}
	
	static public boolean fullFillsRequirements(House house)
	{
		return false;
	}
	
	static public int LtB(double d)
    {
        return (int)Math.round(d - 0.5D);
    }
	
	private static final long serialVersionUID = -2473397400829399372L;

	public String getJobName()
	{
		if (job != null)
			return job.getClass().getName();
		
		return "";
	}
	
	static public void quit(House house)
	{
		MC1606.getInstance.houses.remove(house);
		if (house.job != null)
			house.job.running = false;
	}
}
