import java.awt.*;

// This appears in Core Web Programming from
// Prentice Hall Publishers, and may be freely used
// or adapted. 1997 Marty Hall, hall@apl.jhu.edu.

/** A Panel with adjustable top/bottom insets value.
 *  Used to hold a Scrollbar in the Slider class
 */

public class ScrollbarPanel extends Panel {
  private Insets insets;

  public ScrollbarPanel(int margins) {
    setLayout(new BorderLayout());
    setMargins(margins);
  }

  public Insets insets() {
    return(insets);
  }

  public int getMargins() {
    return(insets.top);
  }
  
  public void setMargins(int margins) {
    this.insets = new Insets(margins, 0, margins, 0);
  }
}
