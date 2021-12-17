import java.awt.*;
import java.awt.event.*;

public class SamplePanel extends Panel
						 implements ActionListener
{
	static final int [] sampleSizes = { 25, 50, 100, 200 };
	
	Choice sampleSize;
	GraphInfo info;
	Fishy fishy;
	Button sampleButton;
	Panel choicePanel;
	
	public SamplePanel( Fishy fishy, GraphInfo info )
	{
		//super( new BorderLayout() );
		
		sampleButton = new Button("Sample");
		sampleButton.addActionListener( this );
		
		choicePanel = new Panel( new BorderLayout() );
		choicePanel.add( new XLabel("Number of samples: "), BorderLayout.CENTER );
		
		add( sampleButton );
		
		this.fishy = fishy;
		this.info = info;
		
		int i;
		
		sampleSize = new Choice();
		
		for( i=0; i<sampleSizes.length; ++i )
		{
			sampleSize.add( Integer.toString(sampleSizes[i]) );
		}
		
		choicePanel.add( sampleSize, BorderLayout.EAST );
	}
	
	public void actionPerformed( ActionEvent e )
	{
		fishy.sample( sampleSizes[sampleSize.getSelectedIndex()] );
	}
	
	public Component getChoice()
	{
		return choicePanel;
	}
}
