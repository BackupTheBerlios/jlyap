package awtLyapunov;




/* 
	If you want to use your own function, simply override the methods 
	value(), deriv() and (optionally) setInititalParamters(). Expect 
	this way for changing your own functions to undergo heavy changes
	(as soon as multiple functions will be supported).
*/


public class LyapFunction extends LyapFunctionTemplate
{	protected void setInitialParameters()
	{
		UpperLeftA = 2; UpperLeftB = 4;
		LowerLeftA = 2; LowerLeftB = 2;
		PixelX = 500; PixelY = 500;

		Seed = 0.51;

		FunctionText = "Rn*Xn*(1.0-Xn)";
		DerivationText = "Rn*(1.0-2.0*Xn)";
	}

	public double value(double Xn, double Rn, double p1, double p2)
	{	return Rn*Xn*(1.0-Xn);
	}
	
	public double deriv(double Xn, double Rn, double p1, double p2)
	{	return Rn*(1.0-2.0*Xn);
	}	
}


//public class LyapFunction extends LyapFunctionTemplate
//{	protected void setInitialParameters()
//	{
//		UpperLeftA = 0; UpperLeftB = 40;
//		LowerLeftA = -40; LowerLeftB = 0;
//		PixelX = 500; PixelY = 500;
//
//		Seed = 0.51;
//		AdditionalParameter1 = 1.2;
//	}
//
//	public double value(double Xn, double Rn, double p1, double p2)
//	{ 	return Rn*Math.exp(-(Xn-p1)*(Xn-p1));
//	}
//
//	public double deriv(double Xn, double Rn, double p1, double p2)
//	{	return -Rn*2*(Xn-p1)*Math.exp(-(Xn-p1)*(Xn-p1));
//	}
//}

