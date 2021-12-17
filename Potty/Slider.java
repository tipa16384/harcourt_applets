import java.awt.*;
import java.util.Vector;
import java.awt.event.*;
import Graph.*;

// This appears in Core Web Programming from
// Prentice Hall Publishers, and may be freely used
// or adapted. 1997 Marty Hall, hall@apl.jhu.edu.

/** A class that combines a horizontal Scrollbar and
 *  a TextField (to the right of the Scrollbar).
 *  The TextField shows the current scrollbar value,
 *  plus, if setEditable(true) is set, it can be used
 *  to change the value as well.
 *
 * @author Marty Hall (hall@apl.jhu.edu)
 */

public class Slider extends Panel {
  private LightScrollbar scrollbar;
  //private Sliderbar sliderbar;
  private TextField textfield;
  private ScrollbarPanel scrollbarPanel;
  private int preferredWidth = 120;

	boolean	peer_style = false;
	Vector	listeners = new Vector();			//array of those listening to me
	Vector	eventQueue = new Vector();			//queue of events to tell others to process
	boolean	processing = false;					//whether we are currently having events handled
    

  //----------------------------------------------------
  /** Construct a slider with the specified min, max
   *  and initial values. The "bubble" (thumb)
   *  size is set to 1/10th the scrollbar range.
   *  In JDK 1.1.x, it tries to adjust for the max
   *  value bug by adding the bubble thickness to
   *  the max value.
   */
  public Slider(int minValue, int maxValue,
                int initialValue) {
    this(minValue, maxValue, initialValue,
         (maxValue - minValue)/10);
  }

  /** Construct a slider with the specified min, max,
   *  and initial values, plus the specified "bubble"
   *  (thumb) value. This bubbleSize should be
   *  specified in the units that min and max use,
   *  not in pixels. Thus, if min is 20 and max is
   *  320, then a bubbleSize of 30 is 10% of the
   *  visible range.
   */
  public Slider(int minValue, int maxValue,
                int initialValue, int bubbleSize) {
    setLayout(new BorderLayout());
    maxValue = adjustFor1_1(maxValue, bubbleSize);
    scrollbarPanel = new ScrollbarPanel(0);
   	scrollbar = new LightScrollbar(Scrollbar.HORIZONTAL,
                              initialValue,
                              bubbleSize,
                              minValue, maxValue);
    scrollbarPanel.add("Center", scrollbar);
    //if( !peer_style )				//use fancy custom scrollbar
    //{
    //	scrollbar.setVisible(false);
	//}
    add("Center", scrollbarPanel);
    //add("Center", scrollbar);
    //System.out.println("Slider TextField( "+(numDigits(maxValue) + 1)+" )");
    //was textfield = new TextField(numDigits(maxValue) + 1);
    textfield = new TextField(numDigits(maxValue) -1);
    setFontSize(12);
    textfield.setEditable(true);
    setTextFieldValue();
    System.out.println("Slider TextField.getColums=="+textfield.getColumns());
    add("East", textfield);
  }

  
  //public void mouseReleased( MouseEvent e )
  //{
  	//scrollbar.processEvent( e );
  //}
  
  
  //----------------------------------------------------
  /** A place holder to override for action to be taken
   *  when scrollbar changes
   */
  public void doAction(int value)
  {
	if( listeners.size() == 0 )			//check for anyone listening
		return;

  	DataEvent e = new DataEvent(DataEvent.UPDATE);
  	
	boolean shouldWeProcess = false;

	synchronized( eventQueue )			//enQ this event
	{
		eventQueue.addElement( e );
		shouldWeProcess = !processing;
		processing = true;
	}
	if( shouldWeProcess )
	{
		doEventLoop: for(;;)
		{
			synchronized( eventQueue )
			{
				if( eventQueue.size() == 0 )
				{
					processing = false;
					break doEventLoop;
				}
				e = (DataEvent)eventQueue.firstElement();
				eventQueue.removeElement( e );
			}
			Vector v;					//keep a local copy
			int len;					// so we can run unsync
			synchronized( listeners )
			{
				v = (Vector)listeners.clone();
				len = v.size();
			}
			for( int i = 0; i < len; ++i )
			{
				((DataListener)v.elementAt(i)).processDataEvent( e );
			}
		}
	}
  }

  //----------------------------------------------------
  /** When scrollbar changes, sets the textfield */
  
  public boolean handleEvent(Event event) {
    if (event.target == scrollbar &&
        isScrollEvent(event.id)) {
      setTextFieldValue();
      doAction(scrollbar.getValue());
      fixWindowsProblem(event.id);
      return(true);
    } else
      return(super.handleEvent(event));
  }

  //----------------------------------------------------
  /** When textfield changes, sets the scrollbar */
  
  public boolean action(Event event, Object object) {
    if (event.target == textfield) {
      String value = textfield.getText();
      int oldValue = getValue();
      try {
        setValue(Integer.parseInt(value.trim()));
      } catch(NumberFormatException nfe) {
        setValue(oldValue);
      }
      return(true);
    } else
      return(false);
  }
  
  //----------------------------------------------------
  /** Returns the Scrollbar part of the Slider. */
  
  public LightScrollbar getScrollbar() {
    return(scrollbar);
  }

  /** Returns the TextField part of the Slider */
  
  public TextField getTextField() {
    return(textfield);
  }

  //----------------------------------------------------
  /** Changes the preferredSize to take a minimum
   *  width, since super-tiny scrollbars are
   *  hard to manipulate.
   *
   * @see #getPreferredWidth
   * @see #setPreferredWidth
   */
  public Dimension preferredSize() {
    Dimension d = super.preferredSize();
    d.height = textfield.preferredSize().height;
    d.width = Math.max(d.width, preferredWidth);
    return(d);
  }

  /** This just calls preferredSize */
  
  public Dimension minimumSize() {
    return(preferredSize());
  }
  
  //----------------------------------------------------
  /** To keep scrollbars legible, a minimum width is
   *  set. This returns the current value (default is
   *  150).
   *
   * @see #setPreferredWidth
   */
  public int getPreferredWidth() {
    return(preferredWidth);
  }

  /** To keep scrollbars legible, a minimum width is
   *  set. This sets the current value (default is
   *  150).
   *
   * @see #getPreferredWidth
   */
  public void setPreferredWidth(int preferredWidth) {
    this.preferredWidth = preferredWidth;
  }

  //----------------------------------------------------
  /** This returns the current scrollbar value */
  
  public int getValue() {
    return(scrollbar.getValue());
  }

  /** This assigns the scrollbar value. If it is below
   *  the minimum value or above the maximum, the value
   *  is set to the min and max value, respectively.
   */
  public void setValue(int value) {
    scrollbar.setValue(value);
    setTextFieldValue();
  }

  //----------------------------------------------------
  /** Sometimes horizontal scrollbars look odd if they
   *  are very tall. So empty top/bottom margins
   *  can be set. This returns the margin setting.
   *  The default is four.
   *
   * @see setMargins
   */
  public int getMargins() {
    //return(scrollbarPanel.getMargins());
    return(this.getMargins());
  }
  
  /** Sometimes horizontal scrollbars look odd if they
   *  are very tall. So empty top/bottom margins
   *  can be set. This sets the margin setting.
   *
   * @see getMargins
   */
  public void setMargins(int margins) {
    //scrollbarPanel.setMargins(margins);
    this.setMargins(margins);
  }

  //----------------------------------------------------
  /** Returns the current textfield string. In most
   *  cases this is just the same as a String version
   *  of getValue, except that there may be padded
   *  blank spaces at the left.
   */
  public String getText() {
    return(textfield.getText());
  }

  /** This sets the TextField value directly. Use with
   *  extreme caution since it does not right-align
   *  or check if value is numeric.
   */
  public void setText(String text) {
    textfield.setText(text);
  }

  //----------------------------------------------------
  /** Returns the Font being used by the textfield.
   *  Courier bold 12 is the default.
   */
  
  public Font getFont() {
    return(textfield.getFont());
  }

  /** Changes the Font being used by the textfield. */
  
  public void setFont(Font textFieldFont) {
    textfield.setFont(textFieldFont);
  }

  //---------------------------------------------------
  /** The size of the current font */

  public int getFontSize() {
    return(getFont().getSize());
  }

  /** Rather than setting the whole font, you can
   *  just set the size (Courier bold will be used
   *  for the family/face).
   */
  public void setFontSize(int size) {
    setFont(new Font("Courier", Font.BOLD, size));
  }
  
  //----------------------------------------------------
  /** Determines if the textfield is editable. If it
   *  is, you can enter a number to change the
   *  scrollbar value. In such a case, entering a value
   *  outside the legal range results in the min or
   *  max legal value. A non-integer is ignored.
   *
   * @see #setEditable
   */
  public boolean isEditable() {
    return(textfield.isEditable());
  }

  /** Determines if you can enter values directly
   *  into the textfield to change the scrollbar.
   *
   * @see #isEditable
   */
  public void setEditable(boolean editable) {
    textfield.setEditable(editable);
  }

  //----------------------------------------------------
  // Sets a right-aligned textfield number.
  
  private void setTextFieldValue() {
    int value = scrollbar.getValue();
    int digits = numDigits(scrollbar.getMaximum());
    String valueString = padString(value, digits);
    textfield.setText(valueString);
  }

  //----------------------------------------------------
  // Repeated String concatenation is expensive, but
  // this is only used to add a small amount of
  // padding, so converting to a StringBuffer would
  // not pay off.
  
  private String padString(int value, int digits) {
    String result = String.valueOf(value);
    for(int i=result.length(); i<digits; i++)
      result = " " + result;
    //was return(result + " ");
    return(result);
  }
  
  //----------------------------------------------------
  // Determines the number of digits in a decimal
  // number.
  
  private static final double LN10 = Math.log(10.0);
  
  private static int numDigits(int num) {
    return(1 + 
           (int)Math.floor(Math.log((double)num)/LN10));
  }

  //----------------------------------------------------
  // Since several implementations generate extraneous
  // scrollbar events, you shouldn't just check
  // the event target, but verify a correct
  // event type also. Used by handleEvent.
  
  private boolean isScrollEvent(int eventID) {
    return(eventID == Event.SCROLL_LINE_UP ||
           eventID == Event.SCROLL_LINE_DOWN ||
           eventID == Event.SCROLL_PAGE_UP ||
           eventID == Event.SCROLL_PAGE_DOWN ||
           eventID == Event.SCROLL_ABSOLUTE);
  }

  //----------------------------------------------------
  // KLUDGE ALERT!
  // Many Windows 95 Java implementations (including
  // most browsers and Sun's JDK through version 1.1.3)
  // fail when you drag the "thumb", often bouncing back
  // to their original location when the button is
  // released. Enforcing short pauses between the events
  // appears to solve the problem in many cases, but
  // it slows things down, the exact sleep amount
  // needed depends on the system speed, and there is
  // absolutely no guarantee that this will always work.
  // The only "real" solution is to get the vendors to
  // fix the implementations.
  
  private void fixWindowsProblem(int eventID) {
    if (eventID == Event.SCROLL_ABSOLUTE)
      pause(100);
  }

  private void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch(InterruptedException ie) {}
  }
  
  //----------------------------------------------------
  // KLUDGE ALERT!
  // In all or most Java 1.02 implementations (JDK,
  // Netscape 2, 3, 4, Internet Explorer 3.01),
  // the max value is the largest possible value that
  // can be set. But in JDK 1.1.1-1.1.3 on Unix and
  // Windows, you can only set values as big
  // as max-bubble (ie getMaximum() *minus*
  // getVisible()), despite what getMaximum()
  // returns. This adjusts for that, but of course
  // has the problem that a 1.1 implementation that
  // *was* consistent with 1.02 would no longer work.
  // Trying to fix bugs on a per-implementation basis
  // is a risky proposition indeed; this is not the
  // high point of Java's platform independence.
  
  private int adjustFor1_1(int max, int bubble) {
    String version = System.getProperty("java.version");
    if (version.startsWith("1.1"))
      return(max+bubble);
    else
      return(max);
  }


	public void addDataListener( DataListener dl )
	{
		if( !listeners.contains(dl) )
			listeners.addElement( dl );
	}

	public void removeDataListener( DataListener dl )
	{
		if( listeners.contains(dl) )
			listeners.removeElement( dl );
	}

}
