/*
 * Created on Aug 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package awtLyapunov;

/**
 * @author koetter
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class LyapFunctionTemplate 
{
	protected double UpperLeftA , UpperLeftB,
		   		     LowerLeftA, LowerLeftB ;
	protected int PixelX, PixelY;
	protected double Seed ;
	protected String Sequence;
	protected String FunctionText,
					 DerivationText;
	
	protected double AdditionalParameter1, AdditionalParameter2;
	
	public String[] getInitialParameters()
	{	String[] initialParameter = new String  [12];
	
		setDefaultInitialParameters();
		setInitialParameters();
		initialParameter[0] = (new Double(UpperLeftA)).toString(); 
		initialParameter[1] = (new Double(UpperLeftB)).toString();
		initialParameter[2] = (new Double(LowerLeftA)).toString(); 
		initialParameter[3] = (new Double(LowerLeftB)).toString();
		initialParameter[4] = (new Integer(PixelX)).toString(); 
		initialParameter[5] = (new Integer(PixelY)).toString();
		initialParameter[6] = (new Double(Seed)).toString();;
		initialParameter[7] = Sequence;
		initialParameter[8] = (new Double(AdditionalParameter1)).toString();;
		initialParameter[9] = (new Double(AdditionalParameter2)).toString();;
		initialParameter[10]= FunctionText;
		initialParameter[11]= DerivationText;
				
		return initialParameter;
	}
	
	LyapFunctionTemplate()
	{	
	}
	
	private void setDefaultInitialParameters()
	{	UpperLeftA = 0.0; UpperLeftB = 4.0;
		LowerLeftA = 0.0; LowerLeftB = 0.0;
		PixelX = 500; PixelY = 500;
		Seed = 0.5;
		Sequence = "ab";
		FunctionText = "Please enter the function source here";
		DerivationText = "Please enter the derivation source here";
		AdditionalParameter1 = 0;
		AdditionalParameter2 = 0;
	}

	protected void setInitialParameters()
	{
	}
	
	public double value(double Xn, double Rn, double p1, double p2)
	{	return Rn*Xn*(1.0-Xn);
	}
	public double deriv(double Xn, double Rn, double p1, double p2)
	{	return Rn*(1.0-2.0*Xn);
	}
	
	public String getFunctionText()
	{	return FunctionText;
	}

	public String getDerivationText()
	{	return DerivationText;
	}
}
