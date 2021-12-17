// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AcidsBases.java

import ckc.awt.*;
import ckc.awt.ErrFrame.*;
import java.applet.Applet;
import java.awt.*;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

public class AcidsBases extends Applet
{

    public AcidsBases()
    {
    	Utility.setApplet(this);
        topRectColor = new Color(255, 153, 26);
        bottRectColor = topRectColor;
        paperWidth = 14;
        topRectHeight = 70;
        bottRectHeight = 30;
        bottRectBeakerAbove = 20;
        errDialogHelper = ErrFrame.makeErrDialogHelper();
    }

    public boolean action(Event e, Object arg)
    {
        Object target = e.target;
        if(target == acidBaseChoice)
        {
            noteSelectedAcidOrBaseAB();
            return true;
        }
        if(target == dipButton)
        {
            doDipAB();
            return true;
        }
        if(target == clearHistoryButton)
        {
            historyArea.setText("");
            return true;
        } else
        {
            return false;
        }
    }

    int bottRectInitTopAB()
    {
        int y = beakerImageLab.location().y - bottRectBeakerAbove - bottRectHeight;
        return y;
    }

    Color calcColorPhAB(double pH)
    {
        if(pH <= 3D)
            return new Color(125, 3, 46);
        if(pH > 3D && pH <= 5D)
            return new Color(255, 3, 46);
        if(pH > 5D && pH <= 7D)
            return new Color(205, 100, 33);
        if(pH == 7D)
            return new Color(255, 153, 26);
        if(pH > 7D && pH <= 9D)
            return new Color(0, 195, 67);
        else
            return new Color(46, 44, 255);
    }

    double calcPHChemAB(Chemical chemical, double currConc)
    {
        double sc = currConc;
        double tenMinusSeven = 1.0000000000000002E-007D;
        double twoPointThree = 2.3025851000000004D;
        double pH;
        if(chemical.name == "HCl")
        {
            double x = sc + tenMinusSeven;
            pH = -Math.log(x) / twoPointThree + 0.0001D;
        } else
        if(chemical.name == "HNO2")
        {
            double ka = 0.0004500000000000001D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            pH = -Math.log(x) / twoPointThree;
        } else
        if(chemical.name == "HF")
        {
            double ka = 0.00072000000000000015D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            pH = -Math.log(x) / twoPointThree;
        } else
        if(chemical.name == "HOAc")
        {
            double ka = 0.00018000000000000004D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            pH = -Math.log(x) / twoPointThree;
        } else
        if(chemical.name == "HCN")
        {
            double ka = 4.0000000000000001E-010D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            pH = -Math.log(x) / twoPointThree;
        } else
        if(chemical.name == "HOC6H5")
        {
            double ka = 1.3000000000000002E-010D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            pH = -Math.log(x) / twoPointThree;
        } else
        if(chemical.name == "NaOH")
        {
            double x = sc + tenMinusSeven;
            double oh = -Math.log(x) / twoPointThree;
            pH = 14D - oh;
        } else
        if(chemical.name == "NH3")
        {
            double ka = 1.8E-005D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            double oh = -Math.log(x) / twoPointThree;
            pH = 14D - oh;
        } else
        if(chemical.name == "(CH3)2NH2")
        {
            double ka = 0.0007400000000000001D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            double oh = -Math.log(x) / twoPointThree;
            pH = 14D - oh;
        } else
        if(chemical.name == "C5H5N")
        {
            double ka = 1.5E-009D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            double oh = -Math.log(x) / twoPointThree;
            pH = 14D - oh;
        } else
        if(chemical.name == "C6H5NH2")
        {
            double ka = 4.2000000000000005E-010D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            double oh = -Math.log(x) / twoPointThree;
            pH = 14D - oh;
        } else
        if(chemical.name == "(CH3)NH2")
        {
            double ka = 0.00050000000000000001D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            double oh = -Math.log(x) / twoPointThree;
            pH = 14D - oh;
        } else
        if(chemical.name == "(CH3)3N")
        {
            double ka = 7.400000000000001E-005D;
            double ch = quadMeAB(ka, sc);
            double x = ch + tenMinusSeven;
            double oh = -Math.log(x) / twoPointThree;
            pH = 14D - oh;
        } else
        {
            System.out.println("calcPHChemAB(): Case not handled: " + chemical.name);
            return 7D;
        }
        if(pH > 7D && pH < 7.0099999999999998D)
            pH = 7D;
        return pH;
    }

    void constrain(Container container, Component component, int gridX, int gridY, int gridWidth, int gridHeight, int fill, 
            int anchor, double weightX, double weightY, int top, int left, 
            int bottom, int right, int iPadX, int iPadY)
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.fill = fill;
        gbc.anchor = anchor;
        gbc.weightx = weightX;
        gbc.weighty = weightY;
        if(top + bottom + left + right > 0)
            gbc.insets = new Insets(top, left, bottom, right);
        gbc.ipadx = iPadX;
        gbc.ipady = iPadY;
        ((GridBagLayout)container.getLayout()).setConstraints(component, gbc);
        container.add(component);
    }

    Chemical currChemAB()
    {
        return currChem;
    }

    double currConcAB()
    {
        try
        {
            String valStr = concTextField.getText();
            Double valDoubleObj = new Double(valStr);
            double valDouble = valDoubleObj.doubleValue();
            return valDouble;
        }
        catch(NumberFormatException _ex)
        {
            return (0.0D / 0.0D);
        }
    }

    void dipInAB()
    {
        for(; bottRect.y < beakerImageLab.location().y; waitDipAnimateStepAB())
        {
            bottRect.translate(0, 10);
            drawPhPaper(getGraphics());
        }

    }

    void dipOutAB(double pH)
    {
        bottRectColor = calcColorPhAB(pH);
        for(int bottRectInitTop = bottRectInitTopAB(); bottRect.y > bottRectInitTop;)
        {
            bottRect.translate(0, -10);
            waitDipAnimateStepAB();
            drawPhPaper(getGraphics());
        }

    }

    void doDipAB()
    {
        double currConc = currConcAB();
        if(Double.isNaN(currConc))
        {
            errDialogHelper.showDialog("Invalid concentration. Type a number like 0.1");
            return;
        }
        if(currConc <= 0.0D || currConc > 18D)
        {
            errDialogHelper.showDialog("Invalid concentration. Must be between 0 and 18");
            return;
        } else
        {
            dipInAB();
            double pH = calcPHChemAB(currChemAB(), currConc);
            dipOutAB(pH);
            String phString = StringUtil.valueOfWithPrecision(pH, 2);
            pHLabel.setText("pH = " + phString);
            historyString = acidBaseChoice.getSelectedItem() + "   " + concTextField.getText() + "M   pH = " + phString + "\n";
            historyArea.append(historyString);
            return;
        }
    }

    void drawPhPaper(Graphics g)
    {
        Graphics gCopy = g.create();
        Rectangle pathRect = new Rectangle(bottRect.x, bottRectInitTopAB() - topRectHeight, bottRect.width, topRectHeight + bottRectHeight + bottRectBeakerAbove);
        gCopy.clearRect(pathRect.x, pathRect.y, pathRect.width, pathRect.height);
        gCopy.setColor(bottRectColor);
        gCopy.fillRect(bottRect.x, bottRect.y, bottRect.width, bottRect.height);
        Rectangle topRect = new Rectangle(bottRect.x, bottRect.y - topRectHeight, bottRect.width, topRectHeight);
        gCopy.setColor(topRectColor);
        gCopy.fillRect(topRect.x, topRect.y, topRect.width, topRect.height);
    }

    public void init()
    {
        Font labelFont = new Font("TimesRoman", 0, 10);
        Color labelColor = Color.darkGray;
        historyPanel = new Panel();
        historyPanel.setLayout(new GridBagLayout());
        historyArea = new TextArea("", 5, 2, 0);
        historyArea.setEditable(false);
        constrain(historyPanel, historyArea, 0, 1, 1, 1, 1, 15, 1.0D, 1.0D, 0, 0, 0, 0, 0, 20);
        clearHistoryButton = new Button("Clear history");
        constrain(historyPanel, clearHistoryButton, 0, 0, 1, 1, 0, 11, 0.0D, 0.0D, 0, 0, 10, 0, 0, 0);
        GridBagLayout gridBag = new GridBagLayout();
        setLayout(gridBag);
        initChemicalsAB();
        initPanelAB(labelFont, labelColor);
        setBackground(Color.white);
        noteSelectedAcidOrBaseAB();
        validate();
        initPhRect();
        resize(350, 315);
        validate();
    }

    void initChemicalsAB()
    {
        String acidBaseNames[] = {
            "HCl", "HF", "HCN", "HNO2", "HOC6H5", "HOAc", "NaOH", "(CH3)3N", "(CH3)2NH2", "(CH3)NH2", 
            "C6H5NH2", "C5H5N", "NH3"
        };
        acidsBases = new Vector();
        for(int index = 0; index < acidBaseNames.length; index++)
        {
            String name = acidBaseNames[index];
            acidsBases.addElement(new Chemical(name));
        }

    }

    void initPanelAB(Font labelFont, Color labelColor)
    {
        acidBaseChoice = new Choice();
        for(Enumeration tempEnum = acidsBases.elements(); tempEnum.hasMoreElements(); acidBaseChoice.addItem(((Chemical)tempEnum.nextElement()).name));
        acidBaseChoice.select(0);
        concTextField = new TextField("0.1");
        dipButton = new Button("Dip");
        choiceLabel = new Label();
        choiceLabel.setAlignment(0);
        pHLabel = new Label("pH = ");
        pHLabel.setAlignment(1);
        pHLabel.setForeground(Color.blue);
        beakerImageLab = new ImageLabel(Utility.getImage(this,"beaker.GIF"));
        constrain(this, new Label("pH paper"), 0, 0, 1, 1, 0, 16, 0.0D, 0.0D, 10, 0, 0, 0, 0, 0);
        constrain(this, new Label("Acids & Bases:", 2), 1, 0, 1, 1, 2, 16, 1.0D, 0.0D, 10, 0, 0, 0, 0, 0);
        constrain(this, acidBaseChoice, 2, 0, 1, 1, 0, 16, 0.0D, 0.0D, 10, 0, 0, 0, 0, 0);
        constrain(this, new SpacerCanvas(paperWidth, topRectHeight + bottRectHeight + bottRectBeakerAbove), 0, 1, 1, 3, 0, 16, 0.0D, 0.0D, 0, 0, 0, 0, 0, 0);
        constrain(this, new Label("Concentration (M):", 2), 1, 1, 1, 1, 2, 16, 1.0D, 0.0D, 0, 0, 0, 0, 0, 0);
        constrain(this, concTextField, 2, 1, 1, 1, 0, 16, 0.0D, 0.0D, 0, 0, 0, 0, 0, 0);
        constrain(this, dipButton, 1, 2, 1, 1, 0, 10, 0.0D, 0.0D, 0, 0, 0, 0, 0, 0);
        constrain(this, choiceLabel, 2, 2, 1, 1, 2, 18, 1.0D, 0.0D, 0, 0, 0, 0, 0, 0);
        constrain(this, pHLabel, 1, 3, 2, 1, 2, 18, 1.0D, 0.0D, 0, 0, 0, 0, 0, 0);
        constrain(this, beakerImageLab, 0, 4, 1, 1, 0, 10, 0.0D, 0.0D, 0, 0, 0, 0, 0, 0);
        constrain(this, historyPanel, 1, 4, 2, 1, 1, 18, 1.0D, 0.0D, 0, 10, 0, 0, 0, 0);
    }

    void initPhRect()
    {
        int beakerCenterX = beakerImageLab.location().x + beakerImageLab.size().width / 2;
        int x = beakerCenterX - paperWidth / 2;
        int y = bottRectInitTopAB();
        bottRect = new Rectangle(x, y, paperWidth, bottRectHeight);
    }

    public Insets insets()
    {
        return new Insets(10, 10, 10, 10);
    }

    public Dimension minimumSize()
    {
        return new Dimension(350, 315);
    }

    void noteSelectedAcidOrBaseAB()
    {
        int index = acidBaseChoice.getSelectedIndex();
        currChem = (Chemical)acidsBases.elementAt(index);
        choiceLabel.setText(currChem.name);
    }

    public void paint(Graphics g)
    {
        FramedPanel.drawFrame(g, this);
        drawPhPaper(g);
    }

    public Dimension preferredSize()
    {
        return minimumSize();
    }

    double quadMeAB(double ka, double sc)
    {
        double a = 1.0D;
        double b = ka;
        double c = ka * sc;
        double topPart = Math.sqrt(b * b + 4D * a * c);
        double ans1 = (-b + topPart) / 2D;
        double ans2 = (-b - topPart) / 2D;
        if(ans1 > 0.0D)
            return ans1;
        else
            return ans2;
    }

    void waitDipAnimateStepAB()
    {
        try
        {
            Thread.currentThread();
            Thread.sleep(50L);
        }
        catch(InterruptedException _ex) { }
    }

    Vector acidsBases;
    Chemical currChem;
    Choice acidBaseChoice;
    TextField concTextField;
    ImageLabel beakerImageLab;
    Button dipButton;
    Label choiceLabel;
    Label pHLabel;
    Color topRectColor;
    Color bottRectColor;
    int paperWidth;
    int topRectHeight;
    int bottRectHeight;
    int bottRectBeakerAbove;
    Rectangle bottRect;
    ckc.awt.ErrFrame.ErrDialogHelper errDialogHelper;
    Panel historyPanel;
    TextArea historyArea;
    String historyString;
    Button clearHistoryButton;
}
