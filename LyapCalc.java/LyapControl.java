package awtLyapunov;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;




public class LyapControl extends Frame implements ActionListener
{	private TextField m_Convergence, m_PreIterations, m_IterationsPerUpdate;
	
	private class LyapCalcControl extends Frame //implements ActionListener
	{	
		
		LyapCalcControl()
		{ 	addWindowListener(new WindowAdapter()
			{	public void windowClosing(WindowEvent e)
				{	hide();
				}
			});
			Panel row, area;
			Checkbox checkbox;
			
			setLayout(new GridLayout(3, 1));

			CheckboxGroup cbg = new CheckboxGroup();
			//area = new Panel(new GridLayout(2,1));
			new GridLayout(3,1);
			row = new Panel(new FlowLayout(FlowLayout.LEFT));
			checkbox = new Checkbox(" Smart calculation, Convergence:", cbg, true);
			checkbox.addItemListener(new ItemListener()
			{	public void itemStateChanged(ItemEvent evt)
				{	m_Convergence.setEditable(true);
					m_PreIterations.setEditable(false);
					m_Convergence.requestFocus();
				}
			});
			row.add(checkbox);

			m_Convergence = new TextField("", 4);
			row.add(m_Convergence);
			add(row);
					
			row = new Panel(new FlowLayout(FlowLayout.LEFT));
			checkbox = new Checkbox(" Brute calculation, #PreIterations:", cbg, false);
			checkbox.addItemListener(new ItemListener()
			{	public void itemStateChanged(ItemEvent evt)
				{	m_Convergence.setEditable(false);
					m_PreIterations.setEditable(true);
					m_PreIterations.requestFocus();
				}
			});
			row.add(checkbox);
		
			m_PreIterations = new TextField("", 4);
			m_PreIterations.setEditable(false);
			row.add(m_PreIterations);
			add(row);
					
			row = new Panel(new FlowLayout(FlowLayout.LEFT));
			row.add(new Label("#Iterations per Screen update"));
			m_IterationsPerUpdate = new TextField("",4);
			row.add(m_IterationsPerUpdate);
			add(row);
			
			pack();
			//show();
			
		}	
	}
	
	// The Applet we want to control
	private LyapRun m_LyapRunWindow;
	private LyapCalcControl m_CalcControl;
			
	private Button m_StartStopButton, m_SaveButton, m_PrevButton, m_NextButton;
	private TextField m_UpperLeftA,
		m_UpperLeftB,
		m_LowerLeftA,
		m_LowerLeftB,
		m_LowerRightA,
		m_LowerRightB,
		m_UpperRightA,
		m_UpperRightB,
		m_PixelX,
		m_PixelY,
		m_ScalePixel,
		m_Sequence,
		m_Seed,
		m_addPara1,
		m_addPara2;

	private Label m_Function, m_Derivation;

	private Panel m_LogArea;

	private TextArea m_OutputLog;
	private Label m_ScalePixelText;


	private MenuItem makeMenuItem(String Label, String actionCommand, char Shortcut, boolean enabled)
	{	MenuItem menuitem  = new MenuItem(Label, new MenuShortcut((int) Shortcut));
		menuitem.addActionListener(this);
		menuitem.setActionCommand(actionCommand);
		menuitem.setEnabled(enabled);
				
		return menuitem;
	}


	public LyapControl(LyapRun slave)
	{
		this.m_LyapRunWindow = slave;
		
		m_CalcControl = new LyapCalcControl();
		
		Panel row, area;

		super.setTitle("Lyapunov Control");

		addWindowListener(new WindowAdapter()
		{	public void windowClosing(WindowEvent e)
			{	System.exit(0);
			}
		});

		MenuBar menubar = new MenuBar();
		Menu menu, submenu;
		MenuItem menuitem;

		menu = new Menu("File");

		menu.add(makeMenuItem("Open ...", "FileOpen", 'O', true));
		menu.add(makeMenuItem("Save ...", "FileSave", 'S', true));
		menu.addSeparator();
		menu.add(makeMenuItem("Quit", "FileQuit", 'Q', true));
	
		menubar.add(menu);

		menu = new Menu("Options");

		submenu = new Menu("Color palette");
		submenu.add(makeMenuItem("black -> yellow -> white, gray -> black", "ColorPalette1", '1', true));
		submenu.add(makeMenuItem("black -> white, black ->white","ColorPalette2",'2', true));
		submenu.add(makeMenuItem("black -> yellow, black -> blue", "ColorPalette3", '3', true));
		submenu.add(makeMenuItem("white -> blue -> black, black -> green", "ColorPalette4", '4', true));
		submenu.addSeparator();
		submenu.add(makeMenuItem("Custom ...", "CustomColorPalette", ' ', false));
		menu.add(submenu);

		menu.add(makeMenuItem("Log (un)hide", "DisplayLog", 'L', true));
		menu.add(makeMenuItem("Advanced", "EditAdvanced", 'A', true));
		menu.add(makeMenuItem("Function", "EditFunction", 'F', false));
		
		menubar.add(menu);

		menu = new Menu("Help");
		menu.add(makeMenuItem("Content  ", "HelpContent", 'H', false));
		menu.add(makeMenuItem("About  ", "HelpAbout", 'A', true));
	
		menubar.add(menu);
		setMenuBar(menubar);

		setLayout(new BorderLayout()); // Standard bei Frames

		//		Top area - Input fields
		area = new Panel(new GridLayout(6, 1));
		
		row = new Panel(new FlowLayout(FlowLayout.LEFT));
		m_Function = new Label("f(Xn)="+"");
		row.add(m_Function);
		m_Derivation = new Label ("f'(Xn)="+"");
		row.add(m_Derivation);
		area.add(row);

		
		row = new Panel(new FlowLayout(FlowLayout.LEFT));
		row.add(new Label("Upper Left"));
		m_UpperLeftA = new TextField("", 12);
		row.add(m_UpperLeftA);
		m_UpperLeftB = new TextField("", 12);
		row.add(m_UpperLeftB);

		area.add(row);

		row = new Panel(new FlowLayout(FlowLayout.LEFT));
		row.add(new Label("Lower Left"));
		m_LowerLeftA = new TextField("", 12);
		row.add(m_LowerLeftA);
		m_LowerLeftB = new TextField("", 12);
		m_LowerLeftB.selectAll();
		row.add(m_LowerLeftB);

		area.add(row);

		row = new Panel(new FlowLayout(FlowLayout.LEFT));
		row.add(new Label("Resolution"));
		m_PixelX = new TextField("", 4);
		m_PixelX.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent evt)
			{	try
				{	int pixel = (new Integer(((TextField) evt.getSource()).getText())).intValue();
					if (pixel <= 1)	throw new NumberFormatException();
					m_PixelX.setText("" + pixel);
					m_PixelX.setForeground(Color.BLACK);
				}
				catch (NumberFormatException e)
				{	m_PixelX.requestFocus();
					m_PixelX.setForeground(Color.RED);
				}
			}
		});

		row.add(m_PixelX);

		m_PixelY = new TextField("", 4);
		m_PixelY.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent evt)
			{	try
				{	int pixel = (new Integer(((TextField) evt.getSource()).getText())).intValue();
					if (pixel <= 1)	throw new NumberFormatException();
					m_PixelY.setText("" + pixel);
					m_PixelY.setForeground(Color.BLACK);
				}
				catch (NumberFormatException e)
				{	m_PixelY.requestFocus();
					m_PixelY.setForeground(Color.RED);
				}
			}
		});

		row.add(m_PixelY);

		m_ScalePixelText = new Label("  <- Scale");
		row.add(m_ScalePixelText);

		m_ScalePixel = new TextField("1.0", 2);
		m_ScalePixel.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent evt)
			{	try
				{	double scale = (new Double(((TextField) evt.getSource()).getText())).doubleValue();
					if (scale <= 0) throw new NumberFormatException();
					m_ScalePixel.setForeground(Color.BLACK);

					m_PixelX.setText("" + (int) ((scale * (new Double(m_PixelX.getText()).doubleValue()) + 0.5)));
					m_PixelY.setText("" + (int) ((scale * (new Double(m_PixelY.getText()).doubleValue()) + 0.5)));
				}
				catch (NumberFormatException e)
				{	m_ScalePixel.requestFocus();
					m_ScalePixel.setForeground(Color.RED);
				}
			}
		});

		row.add(m_ScalePixel);

		area.add(row);

		row = new Panel(new FlowLayout(FlowLayout.LEFT));
		row.add(new Label("Sequence "));
		m_Sequence = new TextField("", 12);
		m_Sequence.addActionListener(new ActionListener()
		{	public void actionPerformed(ActionEvent evt)
			{	try
				{	String sequence = (((TextField) evt.getSource()).getText()).toUpperCase();
					m_Sequence.setForeground(Color.BLACK);
					for (int i = 0; i < sequence.length(); i++)
					{	if (sequence.charAt(i) != 'A' && sequence.charAt(i) != 'B')
							throw new NumberFormatException();
					}
				}
				catch (NumberFormatException e)
				{	m_Sequence.requestFocus();
					m_Sequence.setForeground(Color.RED);
				}
			}
		});
		row.add(m_Sequence);

		area.add(row);

		row = new Panel(new FlowLayout(FlowLayout.LEFT));
		row.add(new Label("Seed"));
		m_Seed = new TextField("", 4);
		row.add(m_Seed);

		row.add(new Label("     Add. Para"));
		m_addPara1 = new TextField("", 4);
		row.add(m_addPara1);
		m_addPara2 = new TextField("", 4);
		row.add(m_addPara2);

		area.add(row);

		add("North", area);

		//		Middle Area - Start/Stop Bottom
		area = new Panel(new GridLayout(2, 1));

		row = new Panel(new FlowLayout(FlowLayout.CENTER));
		m_StartStopButton = new Button("  Start  ");

		m_StartStopButton.addActionListener(this);
		m_StartStopButton.setActionCommand("StartStop");

		m_StartStopButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		m_StartStopButton.requestFocus();

		row.add(m_StartStopButton);

		area.add(row);

		row = new Panel(new FlowLayout(FlowLayout.CENTER));
		m_PrevButton = new Button(" < ");

		m_PrevButton.addActionListener(this);
		m_PrevButton.setActionCommand("Prev");

		m_PrevButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		m_PrevButton.setEnabled(false);
		row.add(m_PrevButton);

		m_SaveButton = new Button("Cache");

		m_SaveButton.addActionListener(this);
		m_SaveButton.setActionCommand("Cache");

		m_SaveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		m_SaveButton.setEnabled(false);

		row.add(m_SaveButton);

		m_NextButton = new Button(" > ");

		m_NextButton.addActionListener(this);
		m_NextButton.setActionCommand("Next");

		m_NextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		m_NextButton.setEnabled(false);
		row.add(m_NextButton);

		area.add(row);
		add("Center", area);

		//		Bottom Area - OutputLog
		m_LogArea = new Panel(new BorderLayout());

		row = new Panel(new FlowLayout(FlowLayout.LEFT));
		row.add(new Label("Output Log"));
		m_LogArea.add("North", row);

		row = new Panel(new FlowLayout(FlowLayout.CENTER));
		m_OutputLog = new TextArea("", 9, 37);
		m_OutputLog.setEditable(false);
		row.add(m_OutputLog);

		m_LogArea.add("Center", row);
		m_LogArea.setVisible(false);

		add("South", m_LogArea);

		pack();
		//setResizable(false);
		m_StartStopButton.requestFocus();
	}


	public void actionPerformed(ActionEvent evt)
	{

		if ("DisplayLog".equals(evt.getActionCommand()))
		{
			if (m_LogArea.isVisible())
			{	m_LogArea.setVisible(false); 
				pack();
			} 
			else
			{	m_LogArea.setVisible(true); 
				pack();
			} 
		}
				
		if ("Prev".equals(evt.getActionCommand()))
		{
			m_LyapRunWindow.getPrevious();
			setInputEditable(true);
			return;
		}

		if ("Next".equals(evt.getActionCommand()))
		{
			m_LyapRunWindow.getNext();
			setInputEditable(true);
			return;
		}

		if ("Cache".equals(evt.getActionCommand()))
		{
			m_LyapRunWindow.saveLyapunovCalculation();
			m_SaveButton.setEnabled(false);
			setInputEditable(true);
			return;
		}

		if ("FileSave".equals(evt.getActionCommand()))
		{
			FileDialog fd = new FileDialog(this, "Save current parameters and picture", FileDialog.SAVE);
			fd.setFilenameFilter(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return (name.endsWith(".png") || name.endsWith(".lyap"));
				}
			});
			fd.show();
			saveFiles(fd.getDirectory()+fd.getFile());
			
			return;
		}

		if ("FileOpen".equals(evt.getActionCommand()))
		{
			FileDialog fd = new FileDialog(this, "Open parameter file", FileDialog.LOAD);

			fd.setFilenameFilter(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return (name.endsWith(".lyap"));
				}
			});

			fd.show();
			openFile(fd.getDirectory()+fd.getFile());
			
			return;
		}

		if ("EditAdvanced".equals(evt.getActionCommand()))
		{	
			m_CalcControl.show();
		}

		if ("FileQuit".equals(evt.getActionCommand()))
		{
			System.exit(0);
			return;
		}

		if ("HelpAbout".equals(evt.getActionCommand()))
		{
			Frame about = new Frame("About...");

			about.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent evt)
				{
					Frame frame = (Frame) evt.getSource();
					frame.dispose();
				}
			});

			Panel area = new Panel(new BorderLayout());
			TextArea textarea = new TextArea("LyapKK (still searching for a good name...) ", 5, 36, TextArea.SCROLLBARS_NONE);
			textarea.append("was brought to you by Dr. Karsten K" + '\u00f6' + "tter. ");
			textarea.append("See http://www.KarstenKoetter.de/Lyapunov.html for details. ");
			textarea.append("Thanks to Malte Schmick for valuable feedback. ");
			textarea.append("Build 2004-09-19\n");
			textarea.setEditable(false);
			area.add("Center", textarea);

			about.add(area);
			about.pack();
			about.show();

			return;
		}

		if ("ColorPalette1".equals(evt.getActionCommand()))
		{
			m_LyapRunWindow.setColorPalette(1);
			return;
		}
		if ("ColorPalette2".equals(evt.getActionCommand()))
		{
			m_LyapRunWindow.setColorPalette(2);
			return;
		}
		if ("ColorPalette3".equals(evt.getActionCommand()))
		{
			m_LyapRunWindow.setColorPalette(3);
			return;
		}
		if ("ColorPalette4".equals(evt.getActionCommand()))
		{
			m_LyapRunWindow.setColorPalette(4);
			return;
		}

		double LL_A = Double.NaN, LL_B = Double.NaN, UL_A = Double.NaN, UL_B = Double.NaN, X0 = Double.NaN, addPara1 = Double.NaN, addPara2 = Double.NaN;
		int PixelX = -1, PixelY = -1;
		boolean validValues = true;
		String sequence = new String("");

		try
		{
			UL_A = (new Double(m_UpperLeftA.getText())).doubleValue();
			m_UpperLeftA.setForeground(Color.BLACK);
		}
		catch (NumberFormatException e)
		{
			m_UpperLeftA.requestFocus();
			m_UpperLeftA.setForeground(Color.RED);
			validValues = false;
		}

		try
		{
			UL_B = (new Double(m_UpperLeftB.getText())).doubleValue();
			m_UpperLeftB.setForeground(Color.BLACK);
		}
		catch (NumberFormatException e)
		{
			m_UpperLeftB.requestFocus();
			m_UpperLeftB.setForeground(Color.RED);
			validValues = false;
		}

		try
		{
			LL_A = (new Double(m_LowerLeftA.getText())).doubleValue();
			m_LowerLeftA.setForeground(Color.BLACK);
		}
		catch (NumberFormatException e)
		{
			m_LowerLeftA.requestFocus();
			m_LowerLeftA.setForeground(Color.RED);
			validValues = false;
		}

		try
		{
			LL_B = (new Double(m_LowerLeftB.getText())).doubleValue();
			m_LowerLeftB.setForeground(Color.BLACK);
		}
		catch (NumberFormatException e)
		{
			m_LowerLeftB.requestFocus();
			m_LowerLeftB.setForeground(Color.RED);
			validValues = false;
		}

		try
		{
			PixelX = (new Integer(m_PixelX.getText())).intValue();
			if (PixelX < 1)
				throw new NumberFormatException();
			m_PixelX.setText("" + PixelX);
			m_PixelX.setForeground(Color.BLACK);
		}
		catch (NumberFormatException e)
		{
			m_PixelX.requestFocus();
			m_PixelX.setForeground(Color.RED);
			validValues = false;
		}

		try
		{
			PixelY = (new Integer(m_PixelY.getText())).intValue();
			if (PixelY < 1)
				throw new NumberFormatException();
			m_PixelY.setText("" + PixelY);
			m_PixelY.setForeground(Color.BLACK);
		}
		catch (NumberFormatException e)
		{
			m_PixelY.requestFocus();
			m_PixelY.setForeground(Color.RED);
			validValues = false;
		}

		try
		{
			X0 = (new Double(m_Seed.getText())).doubleValue();
			m_Seed.setForeground(Color.BLACK);
		}
		catch (NumberFormatException e)
		{
			m_Seed.requestFocus();
			m_Seed.setForeground(Color.RED);
			validValues = false;
		}

		try
		{
			addPara1 = (new Double(m_addPara1.getText())).doubleValue();
			m_addPara1.setForeground(Color.BLACK);
		}
		catch (NumberFormatException e)
		{
			m_addPara1.requestFocus();
			m_addPara1.setForeground(Color.RED);
			validValues = false;
		}

		try
		{
			addPara2 = (new Double(m_addPara2.getText())).doubleValue();
			m_addPara2.setForeground(Color.BLACK);
		}
		catch (NumberFormatException e)
		{
			m_addPara2.requestFocus();
			m_addPara2.setForeground(Color.RED);
			validValues = false;
		}

		try
		{
			sequence = (m_Sequence.getText()).toUpperCase();
			;
			m_Sequence.setForeground(Color.BLACK);
			for (int i = 0; i < sequence.length(); i++)
			{
				if (sequence.charAt(i) != 'A' && sequence.charAt(i) != 'B')
					throw new NumberFormatException();
			}
		}
		catch (NumberFormatException e)
		{
			m_Sequence.requestFocus();
			m_Sequence.setForeground(Color.RED);
			validValues = false;
		}

		if ("StartStop".equals(evt.getActionCommand()))
		{

			if (m_LyapRunWindow.isCalculating())
				//&& !"  Stop  ".equals(m_StartStopButton.getLabel()))
			{
				m_StartStopButton.setLabel(" Wait ");
				m_StartStopButton.setEnabled(false);
				m_LyapRunWindow.stop();
				setInputEditable(true);
			}
			else if (m_LyapRunWindow.isFinished() && validValues)
			{
				setStartStopEnabled(false);
				setInputEditable(false);
				setPreviousEnabled(false);
				setNextEnabled(false);
				setSaveButtonEnabled(false);

				m_LyapRunWindow.setUpperLeft(UL_A, UL_B);
				m_LyapRunWindow.setLowerLeft(LL_A, LL_B);
				m_LyapRunWindow.setRaster(PixelX, PixelY);
				m_LyapRunWindow.setSeed(X0);
				m_LyapRunWindow.setParameter(addPara1, addPara2);
				m_LyapRunWindow.setSequence(sequence);

				m_LyapRunWindow.start();

				setPreviousEnabled(false);
				setNextEnabled(false);
				setStartStopEnabled(true);
				m_StartStopButton.setLabel("  Stop  ");
			}
		}

	}

	private void saveFiles(String Filename)
	{
		if (Filename == null)
			return;
		String ParaFilename = new String(Filename), PicFilename = new String(Filename);

		if (!Filename.endsWith(".lyap") && !Filename.endsWith(".png"))
		{
			ParaFilename = new String(Filename + ".lyap");
			PicFilename = new String(Filename + ".png");
		}
		else if (Filename.endsWith(".lyap"))
		{
			ParaFilename = new String(Filename);
			PicFilename = new String((Filename.substring(0, Filename.length() - 5)) + ".png");
		}
		else if (Filename.endsWith(".png"))
		{
			PicFilename = new String(Filename);
			ParaFilename = new String((Filename.substring(0, Filename.length() - 4)) + ".lyap");
		}

		FileWriter ParaFile, PicFile;

		try
		{ // maybe save (additionaly?) an equalized picture 
			ImageIO.write(m_LyapRunWindow.getScreenshot(), "png", new File(PicFilename));

			// Ensure that we save the parameters of the pictures 
			// (and not something potentially UI modified...)
			m_LyapRunWindow.setControlWindowParameters();
			ParaFile = new FileWriter(ParaFilename);
			ParaFile.write(
				"LyapKK 0.3 \n\n"
					+ "# More (and redundant) informations about the parameter plane would be nice for\n"
					+ "# documentation purposes, but this file is the wrong place. Please DO NOT EDIT\n"
					+ "# this file manually until you exactly know what you are doing.\n\n"
					+ "# Upper Left Corner\n"
					+ "UL " + (new Double(m_UpperLeftA.getText())).doubleValue()
					+ " "  + (new Double(m_UpperLeftB.getText())).doubleValue() + "\n"
					+ "# Lower Left Corner\n"
					+ "LL "+ (new Double(m_LowerLeftA.getText())).doubleValue()
					+ " " + (new Double(m_LowerLeftB.getText())).doubleValue() + "\n"
					+ "# Pixel resolution\n"
					+ "Pixel "+ (new Integer(m_PixelX.getText())).intValue()
					+ " " + (new Integer(m_PixelY.getText())).intValue()+ "\n"
					+ "# Seed\n"
					+ "Seed "+ (new Double(m_Seed.getText())).doubleValue()+ "\n"
					+ "# Sequence\n"
					+ "Sequence "+(new String(m_Sequence.getText()))+ "\n"
					+ "# Additional Parameter \n"
					+ "AddPara "+(new Double(m_addPara1.getText())).doubleValue()
					+ " "+(new Double(m_addPara2.getText())).doubleValue()+"\n"
					+ "# Function & derivation description\n"
					+ "FunctionText "+(new String(m_Function.getText()))+ "\n"
					+ "DerivationText "+(new String(m_Derivation.getText()))+ "\n"
					+ "\n# Lyapunov function hash code, completely experimental feature...\n"
					+ (new Integer(m_LyapRunWindow.getFunctionHashCode()))
					+ "\n");
			ParaFile.close();
			printLog("Saved '" + ParaFilename + "' and '" + PicFilename + "'\n");
		}
		catch (IOException e)
		{
			printLog(e.getMessage());
		}
	}

	private void openFile(String ParaFilename)
	{	String line;		
		
		int Sep1, Sep2, End,
			PixelX=0, PixelY=0;
		double ulA=0, ulB=0, llA=0, llB=0,
			   AddPara1=0, AddPara2=0, Seed=0;
		String Sequence="",
			   FunctionText="", DerivationText="";

		System.out.println(ParaFilename);
		try
		{ 	FileInputStream file = new FileInputStream (ParaFilename);
			InputStreamReader inReader = new InputStreamReader (file); 
			BufferedReader inBuffer = new BufferedReader (inReader);

			line = inBuffer.readLine();
			// ToDo: Version ?berpr?fen
			while (line != null)
			{ 	if (!line.startsWith("#"))
				{	Sep1 = line.indexOf(" ", 0);
					Sep2 = line.indexOf(" ", Sep1+1);
					End  = line.length();
					
					if (line.startsWith("UL"))
					{	ulA = new Double(line.substring(Sep1, Sep2)).doubleValue();
						ulB = new Double(line.substring(Sep2, End)).doubleValue();;				
					}
					if (line.startsWith("LL"))
					{	llA = new Double(line.substring(Sep1, Sep2)).doubleValue();
						llB = new Double(line.substring(Sep2, End)).doubleValue();;				
					}
					if (line.startsWith("Pixel"))
					{	PixelX = new Integer(new String(line.substring(Sep1, Sep2).trim())).intValue();
						PixelY = new Integer(new String(line.substring(Sep2, End ).trim())).intValue();				
					}
					if (line.startsWith("Seed"))
					{	Seed = new Double(line.substring(Sep1, End)).doubleValue();
					}					
					if (line.startsWith("Sequence"))
					{	Sequence = new String(line.substring(Sep1, End).trim());
					}					
					if (line.startsWith("AddPara1"))
					{	AddPara1 = new Double(line.substring(Sep1, Sep2)).doubleValue();
						AddPara2 = new Double(line.substring(Sep2, End)).doubleValue();;				
					}	
					if (line.startsWith("FunctionText"))
					{	FunctionText = new String(line.substring(Sep1, End).trim());
					}
					if (line.startsWith("DerivationText"))
					{	DerivationText = new String(line.substring(Sep1, End).trim());
					}		
				}
				line = inBuffer.readLine();
			}   

			printLog(" Opened '"+ParaFilename+"'\n");
			setAllCalcParameters(ulA, ulB, llA, llB, 
							     PixelX, PixelY, 
							     Seed, Sequence, 
							     AddPara1, AddPara2,
							     FunctionText, DerivationText);
		}
		catch (FileNotFoundException e) {printLog(e.getMessage());}
		catch (UnsupportedEncodingException e) {printLog(e.getMessage());}
		catch (IOException e) { printLog(e.getMessage());}
	}
		
		
	public void setStartStopEnabled(boolean enabled)
	{
		if (enabled == true)
		{
			m_StartStopButton.setLabel("Start");
			m_StartStopButton.setEnabled(enabled);
		}
	}

	public void setSaveButtonEnabled(boolean enabled)
	{
		m_SaveButton.setEnabled(enabled);
	}

	public void setPreviousEnabled(boolean enabled)
	{
		m_PrevButton.setEnabled(enabled);
	}

	public void setNextEnabled(boolean enabled)
	{
		m_NextButton.setEnabled(enabled);
	}

	public void setUpperLeft(double A, double B)
	{
		m_UpperLeftA.setText(String.valueOf(A));
		m_UpperLeftB.setText(String.valueOf(B));
		setInputEditable(false);
		m_StartStopButton.setEnabled(false);
		m_PrevButton.setEnabled(false);
		m_NextButton.setEnabled(false);
	}

	public void setLowerLeft(double A, double B)
	{
		m_LowerLeftA.setText(String.valueOf(A));
		m_LowerLeftB.setText(String.valueOf(B));
		setInputEditable(false);
	}

	public void setAllCalcParameters(double ulA, double ulB, double llA, 
								double llB, 
								int PixelX, int PixelY, 
								double Seed, String seq, 
								double p1, double p2,
								String Function, String Derivation)
	{
		m_UpperLeftA.setText(String.valueOf(ulA));
		m_UpperLeftB.setText(String.valueOf(ulB));
		m_LowerLeftA.setText(String.valueOf(llA));
		m_LowerLeftB.setText(String.valueOf(llB));
		m_PixelX.setText(String.valueOf(PixelX));
		m_PixelY.setText(String.valueOf(PixelY));
		m_Seed.setText(String.valueOf(Seed));
		m_addPara1.setText(String.valueOf(p1));
		m_addPara2.setText(String.valueOf(p2));
		m_Sequence.setText(seq);
		m_Function.setText("f(Xn)="+Function);
		m_Derivation.setText("f'(Xn)="+Derivation);
	}

	public void setRaster(int PixelX, int PixelY)
	{
		m_PixelX.setText(String.valueOf(PixelX));
		m_PixelY.setText(String.valueOf(PixelY));
		//printLog("   Screen Pixel (" + PixelX + "/" + PixelY + ")\n");
		setInputEditable(false);

		m_ScalePixel.setEnabled(true);
		m_ScalePixelText.setEnabled(true);
		m_ScalePixel.setEditable(true);

		m_StartStopButton.setEnabled(true);
		m_StartStopButton.requestFocus();
	}

	private void setInputEditable(boolean editable)
	{
		m_UpperLeftA.setEditable(editable);
		m_UpperLeftB.setEditable(editable);

		m_LowerLeftA.setEditable(editable);
		m_LowerLeftB.setEditable(editable);

		m_Seed.setEditable(editable);
		m_addPara1.setEditable(editable);
		m_addPara2.setEditable(editable);
		m_Sequence.setEditable(editable);

		m_PixelX.setEditable(editable);
		m_PixelY.setEditable(editable);
		m_ScalePixel.setEditable(editable);

	}

	public void printLog(String log)
	{
		m_OutputLog.append(log);
	}
}
