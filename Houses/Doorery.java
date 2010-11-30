public class Doorery extends HouseJobProduction
{
	
	public Doorery(House house)
	{
		super(house);
		
		name = "Doorery";
		
		//Production
		productType = 324; //Wooddoor
		productAmount = 1;
		
		//Require
		requireType = 17; //Tree
		requireAmount = 4;
		
		worktime = 10000;
		
		updateSign();
	}
	
	static public boolean fits(House house)
	{
		if (house.mostUsedBlock == 3)
			return true;
		
//			if (house.getItemAmount(10) >= 1)
//				return true;
		
		return false;
	}
	
}
