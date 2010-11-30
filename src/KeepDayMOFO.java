public class KeepDayMOFO extends Thread
{

	public boolean running;
	
	public KeepDayMOFO()
	{
		running = true;
		this.start();
	}
	
	public void run()
	{
		while (running)
		{
			etc.getServer().setTime(2000);

	        try {Thread.sleep(10000);}
	        catch(InterruptedException interruptedexception) { }
		}
	}
	
}
