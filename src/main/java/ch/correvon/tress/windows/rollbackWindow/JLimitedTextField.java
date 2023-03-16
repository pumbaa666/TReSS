package ch.correvon.tress.windows.rollbackWindow;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class JLimitedTextField extends JTextField
{
	public JLimitedTextField()
	{
		
	}
	
	public JLimitedTextField(int maxLength)
	{
		super();
		this.setMaxLenght(maxLength);
	}
	
	public void setMaxLenght(int maxLength)
	{
		this.maxLength = maxLength;
		AbstractDocument doc = (AbstractDocument)getDocument();
		doc.setDocumentFilter(new TextLimiter(maxLength));
	}
	
	public int getMaxLength()
	{
		return this.maxLength;
	}

	private static final long serialVersionUID = 1263655065973950494L;
	
	private class TextLimiter extends DocumentFilter
	{
		public TextLimiter(int max)
		{
			this.max = max;
		}

		@Override public void insertString(FilterBypass fb, int offset, String str, AttributeSet attr) throws BadLocationException
		{
			replace(fb, offset, 0, str, attr);
		}

		@Override public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet attrs) throws BadLocationException
		{
			int newLength = fb.getDocument().getLength() - length + str.length();

			if(newLength <= this.max)
				fb.replace(offset, length, str, attrs);
			else
				Toolkit.getDefaultToolkit().beep();
		}
		
		private int max;
	}

	private int maxLength;
}
