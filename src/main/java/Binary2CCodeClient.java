import com.bullywiihacks.development.swing.Binary2CCodeGUI;

import javax.swing.*;

public class Binary2CCodeClient
{
	public static void main(String[] arguments) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		SwingUtilities.invokeLater(() ->
		{
			Binary2CCodeGUI binary2CCodeGUI = new Binary2CCodeGUI();
			binary2CCodeGUI.setVisible(true);
		});
	}
}