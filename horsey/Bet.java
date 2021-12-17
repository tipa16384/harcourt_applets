import java.awt.*;

public class Bet
{
	public static final String WIN = "WIN";
	public static final String PLACE = "PLACE";
	public static final String SHOW = "SHOW";
	
	Horse horse;
	int amount;
	String position;
	double payoff;
	GraphInfo info;
	
	public Bet( GraphInfo info, Horse horse, int amount, String position )
	{
		this.info = info;
		this.horse = horse;
		this.amount = amount;
		this.position = position;
	}
	
	public String toString()
	{
		return "Bet "+Main.formatMoney(amount)+" on "+horse.getName()+" to "+position;
	}
	
	public Horse getHorse()
	{
		return horse;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public String getPosition()
	{
		return position;
	}
	
	public double getPayoff()
	{
		return payoff;
	}
}
