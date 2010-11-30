public class Marketplace extends HouseJob
{
	static public int radius = 100;
	
	public Marketplace(House house)
	{
		super(house);
		
		name = "Marketplace";
	}
	
	static public boolean fits(House house)
	{
		if (house.mostUsedBlock == 4)
			return true;
		
		return false;
	}
}
