import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Room implements Serializable
{
	int blockCount;
	int emptyCount;
	
	int mostUsedBlock;
	
	HashSet<String> blocks = new HashSet<String>();
	HashMap<Integer,Integer> itemList = new HashMap<Integer,Integer>();
	HashMap<Integer,Integer> blockList = new HashMap<Integer,Integer>();
	
	
	public Room() {}
    public Room(int emptyCount,HashSet<String> blocks,HashMap<Integer,Integer> blockList,HashMap<Integer,Integer> itemList)
    {
    	this.blocks = blocks;
    	this.itemList = itemList;
    	this.blockList = blockList;
    	
    	this.emptyCount = emptyCount;
    	this.blockCount = blocks.size();
    	this.mostUsedBlock = MC1606.getMaximum(blockList);
    }
    
	public int hashCode()
	{
		return blocks.hashCode();
	}
	
	private static final long serialVersionUID = 5830901926191605065L;
}
