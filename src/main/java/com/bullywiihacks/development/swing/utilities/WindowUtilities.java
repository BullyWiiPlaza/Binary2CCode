package com.bullywiihacks.development.swing.utilities;

import java.awt.*;

public class WindowUtilities
{
	public static void setIconImage(Window window)
	{
		window.setIconImage(Toolkit.getDefaultToolkit().getImage(WindowUtilities.class.getResource("/Icon.jpg")));
	}
}