package ch.correvon.tress.windows;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import ch.correvon.utils.helpers.ComponentHelper;

/**
 * Bind a AbstractButton(JRadioButton, JCheckBox, ...) to a JComponent.
 * 
 * When activating the AbstractButton (enabling the box), give the focus to the JTextField.
 * When the JTextField got the focus, activate the AbstractButton.
 * 
 * @author lco
 *
 */
public class BindFocus
{
	/* ------------------------------------------------------------ *\
	|* 		  				Méthodes publiques						*|
	\* ------------------------------------------------------------ */
	public static void bind(AbstractButton radio, JSpinner spin)
	{
		bind(radio, ComponentHelper.extractTextField(spin), true, true);
	}

	public static void bind(AbstractButton radio, JComponent component)
	{
		bind(radio, component, true, true);
	}

	public static void bind(AbstractButton radio, JSpinner spin, boolean firstCallSecond, boolean secondCallFirst)
	{
		bind(radio, ComponentHelper.extractTextField(spin), firstCallSecond, secondCallFirst);
	}

	public static void bind(AbstractButton radio, JComponent component, boolean firstCallSecond, boolean secondCallFirst)
	{
		final AbstractButton finalRadio = radio;
		final JComponent finalComponent = component;
		finalRadio.setVerifyInputWhenFocusTarget(firstCallSecond&secondCallFirst); // Astuce qui permet d'éviter le blocage du mutex entre une paire d'objet non-bidirectionel

		if(firstCallSecond)
			finalRadio.addFocusListener(new FocusAdapter()
			{
				@Override public void focusGained(FocusEvent e)
				{
					// PaireObject paire = findRelated(finalRadio);
					if(finalRadio.getVerifyInputWhenFocusTarget())
						mutex = false;
					finalComponent.requestFocus();
					if(finalRadio instanceof JRadioButton)
						finalRadio.setSelected(true);
					else
						finalRadio.setSelected(!finalRadio.isSelected());
				}
			});

		if(secondCallFirst)
		{
			Object relatedRadio = findComponent(finalComponent);
			if(relatedRadio == null)
			{
				listToListen.add(new PaireObject(finalRadio, finalComponent));
				finalComponent.addFocusListener(new FocusAdapter()
				{
					@Override public void focusGained(FocusEvent e)
					{
						if(mutex)
							finalRadio.setSelected(true);
						else
							mutex = true;
					}
				});
			}
		}
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Méthodes privées						*|
	\* ------------------------------------------------------------ */
	private static Object findComponent(Object object)
	{
		for(PaireObject paire:listToListen)
			if(paire.getO2() == object)
				return paire.getO1();
		return null;
	}

	/* ------------------------------------------------------------ *\
	|* 		  				Attributs privés						*|
	\* ------------------------------------------------------------ */
	private static ArrayList<PaireObject> listToListen = new ArrayList<PaireObject>(100);
	private static boolean mutex = true;
}
