public class HouseJob extends Thread
{
	String name;
	House house;
	House repository;
	boolean running;
	
	public HouseJob(House house){
		this.house = house;
	}
    
    public void freeze(int i){
        try {Thread.sleep(i);}
        catch(InterruptedException interruptedexception) { }
    }
    
    public void tell(String str){
    	etc.getServer().getPlayer(house.owner).sendMessage(str);
    }
    
    public boolean checkFlags()
    {
    	//Holism
    	Holism:
    	if (house.getFlag("holism"))
    	{
    		for (String str:house.entries)
    		{
    			Block entryHouse = MC1606.getBlock(str);
    			for (House house:MC1606.getInstance.houses)
    			{
    				if (house.getJobName().equals("Marketplace"))
    				{
    					for (String stt:house.entries)
    					{
    						Block entryMarket = MC1606.getBlock(stt);
    						int distance = MC1606.getDistance(entryHouse, entryMarket);
	    					if ( (distance != 0)&&(distance <= Marketplace.radius) )
	    					{
	    						repository = house;
	    						break Holism;
	    					}
    					}
    				}
    			}
    		}
    	}
    	
    	if (repository == null)
    	{
    		tell("Holism required!");
    		House.quit(house);
    		return false;
    	}
    	
    	return true;
    }
    
    public String toString() {return "JobProduction-"+name;}
	static public boolean fits(House house) {return false;}
}
