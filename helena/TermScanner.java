import java_cup.runtime.*;

public class TermScanner implements java_cup.runtime.Scanner
{
	final String scanMe;
	int pos;
	final int len;
	
	public TermScanner( String scanMe )
	{
		this.scanMe = scanMe;
		pos = 0;
		len = scanMe.length();
	}
	
	public Symbol next_token() throws java.lang.Exception
	{
		char ch = getch();
		
		switch( ch )
		{
			case 0: return new Symbol(sym.EOF);
			case '+': return new Symbol(sym.PLUS);
			case '-': return new Symbol(sym.MINUS);
			case '*': return new Symbol(sym.TIMES);
			case '/': return new Symbol(sym.DIVIDE);
			case '(': return new Symbol(sym.LPAREN);
			case ')': return new Symbol(sym.RPAREN);
			case 'x': return new Symbol(sym.VARIABLE);
			default:
				if( ch >= '0' && ch <= '9' )
				{
					boolean pointSeen=false;
					
					String nums = "";
					for( ; (ch >= '0' && ch <= '9') || (ch == '.' && !pointSeen); ch = getch() )
					{
						nums += ch;
						if( ch == '.' )
							pointSeen = true;
					}
					putch(ch);
					return new Symbol(sym.NUMBER,nums);
				}
				
				return new Symbol(sym.error);
		}
	}
	
	char getch()
	{
		if( pos >= len )
			return 0;
		else
			return scanMe.charAt(pos++);
	}
	
	void putch( char ch )
	{
		if( ch != 0 )
			--pos;
		if( pos < 0 )
			pos = 0;
	}
}
