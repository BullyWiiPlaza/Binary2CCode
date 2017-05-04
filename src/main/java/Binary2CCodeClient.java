import com.bullywiihacks.development.Binary2CCode;
import com.bullywiihacks.development.swing.Binary2CCodeGUI;

import javax.swing.*;

public class Binary2CCodeClient
{
	public static void main(String[] arguments) throws Exception
	{
		if (arguments.length == 0)
		{
			runGraphicalUserInterface();
		} else
		{
			Binary2CCode.runCommandLineInterface(arguments);
		}
	}

	private static void runGraphicalUserInterface() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		SwingUtilities.invokeLater(() ->
		{
			Binary2CCodeGUI binary2CCodeGUI = new Binary2CCodeGUI();
			binary2CCodeGUI.setVisible(true);
		});
	}
}