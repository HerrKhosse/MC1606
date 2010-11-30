public class HouseJobProduction extends HouseJob
{

	//Produce
	int productType;
	int productAmount;
	
	int requireType;
	int requireAmount;
	
	boolean wip = false;
	int worktime;
	int maxAcc;
	
	Chest chest;
	Sign sign;
	
	public HouseJobProduction(House house)
	{
		super(house);
		
		house.setFlag("closed");
		house.setFlag("holism");
		
		//Search for a chest near entries
		for (String str:house.entries)
		{
			if (chest==null)
			{
	    		String split[] = str.split(":");
	    		int x = Integer.valueOf(split[0]);
	    		int y = Integer.valueOf(split[1]);
	    		int z = Integer.valueOf(split[2]);
				
	    		Block tmpblock = MC1606.getAdjacentBlockType(x, y, z, 54, 4);
	    		
	    		if (tmpblock != null)
	    		{
	    			ComplexBlock chestblock = etc.getServer().getComplexBlock(tmpblock.getX(), tmpblock.getY(), tmpblock.getZ());
	    			chest = (Chest)chestblock;
	    			
	    			house.protect.add(tmpblock.getX()+":"+tmpblock.getY()+":"+tmpblock.getZ());
	    		}else{
	    			etc.getServer().getPlayer(house.owner).sendMessage("You need a Chest!");
	    		}
			}
		}
		
		//Search for a sign
		for (String str:house.entries)
		{
			String split[] = str.split(":");
			int x = Integer.valueOf(split[0]);
			int y = Integer.valueOf(split[1]);
			int z = Integer.valueOf(split[2]);

			Block tmpblock = MC1606.getAdjacentBlockType(x, y, z, 68, 6);

			if (tmpblock != null)
			{
				ComplexBlock signblock = etc.getServer().getComplexBlock(tmpblock.getX(), tmpblock.getY(), tmpblock.getZ());
				sign = (Sign) signblock;
				
				house.protect.add(tmpblock.getX()+":"+tmpblock.getY()+":"+tmpblock.getZ());
			}
		}
		
		if (chest != null)
		{
			running = true;
		}else{
			running = false;
		}
			
		if (checkFlags())
			this.start();
	}
	
    public void run()
    {
    	while (running)
    	{
    		freeze(1000);
    		if (running)
    		{
	    		if (chest.hasItem(17, 4, 100))
	    		{
	    			wip = true;
	    			updateSign();
	    			
	    			//etc.getServer().getPlayer("HerrKhosse").sendMessage("go");
	    			for (int i=1;i<=requireAmount;i++)
	    			{
	    				Item item = chest.getItemFromId(requireType);
	    				item.setAmount(item.getAmount()-1);
	    				chest.addItem(item);
	    				chest.update();
	    			}
	    			
	    			freeze(worktime);
	    			
	    			chest.addItem(new Item(productType,productAmount));
	    			chest.update();
	    			
	    			wip = false;
	    			updateSign();
	    		}
	    		//GetChest
	    		//GetItems
	    		//Wait worktime
	    		//give Chest Item
    		}
    	}		
    }
    
	public String getProduct() {return "Type:"+productType+" Amount:"+productAmount;}
    
    public void updateSign()
    {
    	if (sign!=null)
    	{
    		sign.setText(0, " ");
    		sign.setText(1, name);
    		if (wip)
    		{
    			sign.setText(2, "Working...");
    		}else{
    			sign.setText(2, " ");
    		}
    		sign.setText(3, " ");
    		sign.update();
    	}
    }
    
    public String toString() {return "JobProduction-"+name;}
}
