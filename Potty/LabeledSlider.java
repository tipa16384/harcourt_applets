import java.awt.*;

// This appears in Core Web Programming from
// Prentice Hall Publishers, and may be freely used
// or adapted. 1997 Marty Hall, hall@apl.jhu.edu.

/** A Slider with a label centered above it. */

public class LabeledSlider extends Panel {
  private Label label;
  private Slider slider;

  public Label getLabel() {
    return(label);
  }

  public Slider getSlider() {
    return(slider);
  }

  public LabeledSlider(String labelString,
		       int minValue, int maxValue,
		       int initialValue) {
    this(labelString, null,
	 minValue, maxValue, initialValue);
  }

  public LabeledSlider(String labelString,
		       Font labelFont,
		       int minValue, int maxValue,
		       int initialValue) {
    setLayout(new BorderLayout());
    label = new Label(labelString, Label.CENTER);
    if (labelFont != null)
      label.setFont(labelFont);
    add("West", label);
    slider =
      new Slider(minValue, maxValue, initialValue);
    add("Center", slider);
  }
}
