package awtLyapunov;
import java.text.*;


public class LyapCalc implements Cloneable
{
	private ParaPlane     m_ParaPlane;
	private LyapFunction  m_LyapFunc;
	
	private int m_SeqLength;
	private String m_Sequence;
		
	private int m_RasterX, m_RasterY;

	private double[][] m_Xn;
	private double[][] m_Lyap;
	private double[][] m_Conv;
	private double m_X0,
				   m_P1, m_P2;
	private int[][] m_Iterations;

	private long m_allCalcedIterations;
	private double m_averageConvergence;

	private boolean m_isUptodate,
					m_isClone;


	private double m_LyapWeight=0.02,
   				   m_ConvergenceMax=50.0;
	
	
	public LyapCalc()
	{	m_ParaPlane  = new ParaPlane();	
		m_LyapFunc   = new LyapFunction();
		m_isUptodate = false;
		m_isClone	 = false;
	}
	 
	public Object clone() 
	{ 	LyapCalc o = null;
		try 
		{ 	o = (LyapCalc)super.clone();
		   
		} catch (CloneNotSupportedException e) 
		{ 	e.printStackTrace();;
		}
		// Copy parameter plane class
		o.m_ParaPlane = (ParaPlane)o.m_ParaPlane.clone();

		// Free unnecessary data
		o.m_Xn   = null;
		o.m_Conv = null;
		o.m_Iterations = null;

		// Remember for reloading that I'm a clone a cannot be restarted (nobody likes zombies...) 
		o.m_isClone = true;
		
		return o;
	}
	 	 
	public void calcNext(int calcSequences, boolean calcSmart)
	{	int x, y, calcCounter, currentIterations;
		double Xn, dX, Rn, currentLyap, averageConv=0;
			
		for(x=0; x<m_RasterX; x++)
		{	for(y=0; y<m_RasterY; y++)
			{		
				currentLyap = 1.0;
				
				if (calcSmart == true )	
					currentIterations = (int)(calcSequences* m_Conv[x][y]/m_averageConvergence+1.0);	
				else
					currentIterations = calcSequences;

				currentIterations = currentIterations * m_SeqLength;

				if (currentIterations>0)
				{	m_ParaPlane.calc(x+0.5, y+0.5); 
					Xn=m_Xn[x][y];
				
					for( calcCounter=0; calcCounter < currentIterations; calcCounter++)
					{	Rn=(m_Sequence.charAt( (int)((m_Iterations[x][y]+calcCounter) % m_SeqLength)) == 'a' ? m_ParaPlane.getA(): m_ParaPlane.getB());
						Xn=m_LyapFunc.value(Xn, Rn, m_P1, m_P2);
						currentLyap=currentLyap*m_LyapFunc.deriv(Xn, Rn, m_P1, m_P2);		
					}					
					m_Xn[x][y]=Xn;

					currentLyap = Math.log(Math.abs(currentLyap));
					
					if (currentLyap == Double.NEGATIVE_INFINITY )
						currentLyap = -Double.MAX_VALUE;
					else if (currentLyap == Double.POSITIVE_INFINITY )
						currentLyap = Double.MAX_VALUE;
					
					currentLyap = currentLyap / currentIterations;
									
					m_Conv[x][y]= guessConvergence( currentLyap, m_Lyap[x][y]);
					m_Lyap[x][y]= weightLyapunov( currentLyap, currentIterations, m_Lyap[x][y], m_Iterations[x][y]);

					m_Iterations[x][y]+=currentIterations;
					m_allCalcedIterations += currentIterations;
						
				}
				averageConv+=m_Conv[x][y];
			}	
		}
		
		m_averageConvergence = averageConv/((float)m_RasterX*m_RasterY);
	}
	
	
	private double guessConvergence( double currentLyap, double previousLyap)
	{   //double Conv = Math.sqrt((previousLyap-currentLyap) *(previousLyap-currentLyap)/(currentLyap*currentLyap));
		double Conv = Math.sqrt((previousLyap-currentLyap) *(previousLyap-currentLyap))/(currentLyap*currentLyap*currentLyap*currentLyap);
		return (Conv < m_ConvergenceMax? Conv : m_ConvergenceMax);
	}	
	
	private double weightLyapunov( double currentLyap, int currentIterations, double previousLyap, int previousIterations)
	{   return ( (1.0-m_LyapWeight)*previousIterations*previousLyap + (1.0+m_LyapWeight)*currentIterations*currentLyap) / 
				((1.0-m_LyapWeight)*previousIterations+(1.0+m_LyapWeight)*currentIterations);		
	}
		

	
	public int getSizeX()
	{	return m_RasterX;	
	}
	
	public int getSizeY()
	{	return m_RasterY;	
	}
	
	public double getSeed()
	{	return m_X0;
	}
	
	public String getSequence()
	{	return m_Sequence;
	}
	
	public double getAdditionalParameter1()
	{	return m_P1;
	}
	
	public double getAdditionalParameter2()
	{	return m_P2;
	}
		
	
	public double[][] getLyapExpo()
	{	return m_Lyap;
	}
	
	
	private double getLyapExpo(int i,int j)
	{	return m_Lyap[i][j];
	}	
	
	private int getCalcedIterations(int i,int j)
	{	return m_Iterations[i][j];
	}	
	
	private double getConvergence(int i,int j)
	{	return m_Conv[i][j];
	}	
	
	private int getIterations(int i,int j)
	{	return m_Iterations[i][j];
	}
	
	private double getMaxLyapunov()
	{	double maxLyap = -1.0e20;
		
		for(int x=0; x<m_RasterX; x++)
			for(int y=0; y<m_RasterY; y++)
				maxLyap=Math.max(maxLyap, m_Lyap[x][y]);
				
		return maxLyap;
	}
		
	private double getMinLyapunov()
	{	double minLyap = 1.0e20;
		
		for(int x=0; x<m_RasterX; x++)
			for(int y=0; y<m_RasterY; y++)
				minLyap=Math.min(minLyap, m_Lyap[x][y]);
				
		return minLyap;
	}
		
	private int getMinIterations()
	{	int minIterations = (int)1e7;
		
		for(int x=0; x<m_RasterX; x++)
			for(int y=0; y<m_RasterY; y++)
				minIterations=Math.min(minIterations, m_Iterations[x][y]);
				
		return minIterations;
	}
	
	private int getMaxIterations()
	{	int maxIterations = 0;
		
		for(int x=0; x<m_RasterX; x++)
			for(int y=0; y<m_RasterY; y++)
				maxIterations=Math.max(maxIterations, m_Iterations[x][y]);
				
		return maxIterations;
	}
	

	private double getMinConvergence()
	{	double minConvergence = 1e7;
		
		for(int x=0; x<m_RasterX; x++)
			for(int y=0; y<m_RasterY; y++)
				minConvergence=Math.min(minConvergence, m_Conv[x][y]);
				
		return minConvergence;
	}

	
	private double getMaxConvergence()
	{	double maxConvergence = 0;
		
		for(int x=0; x<m_RasterX; x++)
			for(int y=0; y<m_RasterY; y++)
				maxConvergence=Math.max(maxConvergence, m_Conv[x][y]);
				
		return maxConvergence;
	}
	
	
	public void reset() //throws ParameterException
	{	
		if (m_isUptodate && ! m_isClone ) return;
		
		
		m_averageConvergence=1.0;
		m_allCalcedIterations = 0;
			
		m_Xn   = new double[m_RasterX][];
		m_Lyap = new double[m_RasterX][];
		m_Conv = new double[m_RasterX][];
		m_Iterations = new int[m_RasterX][];
		
		for (int i=0; i<m_RasterX; i++)
		{	m_Xn[i]   = new double[m_RasterY];
			m_Lyap[i] = new double[m_RasterY];
			m_Conv[i] = new double[m_RasterY];
			m_Iterations[i] = new int[m_RasterY];
			
			for(int j=0; j<m_RasterY; j++)
			{	m_Xn[i][j]   = m_X0;
				m_Lyap[i][j] = 0.0;
				m_Conv[i][j] = 1;
				m_Iterations[i][j]=0;
			}
		}
		
//			for(i=0; i<m_SeqLength; i++)
//				if ( m_Sequence.charAt(i) != 'a' or m_Sequence.charAt(i) != 'b') 
//					throw new ParameterException();

		m_isUptodate = true;
	}


	public boolean isInitialized()
	{	return m_isUptodate;
	}
	

	public String getPointInfo(int x, int y)
	{	return new String( "Point #:"     + getIterations(x,y) +
						   " Lyap: " + getLyapExpo(x,y) +
						   " Conv: " + getConvergence(x,y) +
						   " Point (" + getA(x,y) +"/"+ getB(x,y)+")" );
	}
		

	public String getDynamicInfo()
	{	DecimalFormat p= new DecimalFormat("#.###E00");
		
		return new String( "#: "  + m_allCalcedIterations/(m_RasterX*m_RasterY)+
							" ( " + getMinIterations() +
							" - " + getMaxIterations() +
							" )   Lyap: ( " + p.format(getMinLyapunov()) + 
							" - " 		   + p.format(getMaxLyapunov()) +
							" )   Conv: "  + p.format(m_averageConvergence) +
							" ( " 		   + p.format(getMinConvergence()) +
							" - "		   + p.format(getMaxConvergence()) + 
							" )" );
	}
		

	public String getStaticInfo()
	{	return new String("Static Info\n"+
						  "   Screen Pixel ("+ m_RasterX+"/"+m_RasterY+")\n"+
						  "   Upper Left:  ("+getUpperLeftA() +"/"+getUpperLeftB() +")\n"+
						  "   Lower Left:  ("+getLowerLeftA() +"/"+getLowerLeftB() +")\n"+
						  "   Lower Right: ("+getLowerRightA()+"/"+getLowerRightB()+")\n"+
						  "   Upper Right: ("+getUpperRightA()+"/"+getUpperRightB()+")\n"+
						  "   Scale: "+m_ParaPlane.getScale()+" AB/Pixel   Theta: "+180.0*m_ParaPlane.getAngle()/Math.PI+" degrees\n"+
						  "   Sequence: "+m_Sequence+"\n"+ 
						  "   X0: "+m_X0);
	}
	
	public double getUpperLeftA()
	{	m_ParaPlane.calc(0, m_RasterY);				
		return m_ParaPlane.getA(); 
	}
	
	public double getUpperLeftB()
	{	m_ParaPlane.calc(0, m_RasterY);				
		return m_ParaPlane.getB(); 
	}
	
	public double getLowerLeftA()
	{	m_ParaPlane.calc(0, 0);						
		return m_ParaPlane.getA(); 
	}
		
	public double getLowerLeftB()
	{	m_ParaPlane.calc(0, 0);						
		return m_ParaPlane.getB();
	}
	
	public double getLowerRightA()
	{	m_ParaPlane.calc(m_RasterX, 0);						
		return m_ParaPlane.getA(); 
	}
		
	public double getLowerRightB()
	{	m_ParaPlane.calc(m_RasterX, 0);						
		return m_ParaPlane.getB();
	}
	public double getUpperRightA()
	{	m_ParaPlane.calc(m_RasterX, m_RasterY);						
		return m_ParaPlane.getA(); 
	}
		
	public double getUpperRightB()
	{	m_ParaPlane.calc(m_RasterX, m_RasterY);						
		return m_ParaPlane.getB();
	}	
	
	public int getFunctionHashCode()
	{	return m_LyapFunc.hashCode();
	}
	
	public double getA(int x, int y)
	{	m_ParaPlane.calc(x+0.5, y+0.5);
		return m_ParaPlane.getA();
	}
	
	public double getB(int x, int y)
	{	m_ParaPlane.calc(x+0.5,y+0.5);
		return m_ParaPlane.getB();
	}
	
	public String getFunctionText()
	{	return m_LyapFunc.getFunctionText();
	}
	
	public String getDerivationText()
	{	return m_LyapFunc.getDerivationText();
	}
	
	public String[] getInitialParameters()
	{	return m_LyapFunc.getInitialParameters();
	}
	
	public void setRaster( int x, int y)
	{	if ( m_RasterX != x ||	m_RasterY != y)
		{   m_ParaPlane.setLeftAxisRaster(y);
			m_RasterX = x;
			m_RasterY = y;
		
			m_isUptodate = false;
		}
	}
		

	public void setSequence( String seq)
	{	seq  = seq.toLowerCase();
		
		if ( !seq.equals(m_Sequence))
		{	m_Sequence = seq;
			m_SeqLength = m_Sequence.length(); 
			m_isUptodate = false;
		}
	}
	

	public void setUpperLeft( double A, double B)
	{ 	if (m_ParaPlane.setUpperLeft(A, B))
		{	m_isUptodate = false;
		} 
	}


	public void setLowerLeft( double A, double B)
	{	
		if (m_ParaPlane.setLowerLeft(A, B))
		{	m_isUptodate = false;
		}
	}
	
		
	public void setSeed(double X0) 
	{	if (m_X0 != X0)
		{	m_X0 = X0; 
			m_isUptodate = false;
		}		
	}
	
	public void setParameter(double p1, double p2) 
	{	if (m_P1 != p1 || m_P2 != p2)
		{	m_P1 = p1; 
			m_P2 = p2; 
			m_isUptodate = false;
		}		
	}
		
//			public class ParameterException extends Exception
//			{	int ExceptionCode;
//				String ExceptionText;
//		
//				public void ParameterException( )
//				{
//				}
//		
//				public int getErrorCode()
//				{	return ExceptionCode;
//				}
//		
//				public String getErrorText()
//				{	return ExceptionText;
//				}
//			}

}	


class ParaPlane implements Cloneable
{	protected double m_current_A, 	  m_current_B,
					 m_UL_B, m_LL_B, m_UL_A, m_LL_A,
					 m_Angle, m_Scale;
	private double m_scale_sin_theta, m_scale_cos_theta;
	private int	m_y;
	
	
	public ParaPlane()
	{	
	}
	
	public Object clone() 
	{ 	ParaPlane o = null;
		try 
		{ 	o = (ParaPlane)super.clone();
		} catch (CloneNotSupportedException e) 
		{ 	e.printStackTrace();;
		}
		return o;
	}
	
		
	public void calc(double x, double y)
	{	
		m_current_A = m_LL_A + (  x*m_scale_cos_theta + y*m_scale_sin_theta );
		m_current_B = m_LL_B + ( -x*m_scale_sin_theta + y*m_scale_cos_theta ) ;
	}

		
	public double getA()
	{	return m_current_A;
	}
	
	public double getAngle()
	{	return m_Angle;
	}		

		
	public double getB()
	{	return m_current_B;
	}

	public double getScale()
	{	return m_Scale;
	}

				
	private void init() //throws ParameterException
	{	m_Angle = Math.atan2( m_UL_A-m_LL_A, m_UL_B-m_LL_B );
		m_Scale = Math.sqrt( (m_UL_B-m_LL_B)*(m_UL_B-m_LL_B) + (m_UL_A-m_LL_A)*(m_UL_A-m_LL_A)) / m_y;
		m_scale_sin_theta = m_Scale*Math.sin(m_Angle);
		m_scale_cos_theta = m_Scale*Math.cos(m_Angle);
	}

		
	public boolean setLeftAxisRaster( int y)
	{	if ( m_y != y)
		{	m_y=y;
			init();
			return true;
		}
		else return false;
	}
		
	public boolean setLowerLeft( double A, double B)
	{	if (m_LL_A != A || m_LL_B !=B)
		{	m_LL_A = A; m_LL_B=B;
			init();
			return true;
		}
		else return false;
	}
	
	public boolean setUpperLeft( double A, double B)
	{	if (m_UL_A != A || m_UL_B !=B)
		{	m_UL_A = A; m_UL_B=B;
			init();
			return true;
		}
		else return false;
	}
}

