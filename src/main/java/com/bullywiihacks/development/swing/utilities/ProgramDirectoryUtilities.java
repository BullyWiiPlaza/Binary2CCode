package com.bullywiihacks.development.swing.utilities;

import java.io.File;
import java.net.URISyntaxException;

public class ProgramDirectoryUtilities
{
	public static String getJarName()
	{
		return new File(ProgramDirectoryUtilities.class.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath())
				.getName();
	}

	public static boolean isRunningFromJAR()
	{
		String jarName = getJarName();
		return !jarName.equals("classes");
	}

	public static String getProgramDirectory()
	{
		if (isRunningFromJAR())
		{
			return getCurrentJARDirectory();
		} else
		{
			return getCurrentProjectDirectory();
		}
	}

	private static String getCurrentProjectDirectory()
	{
		return new File("").getAbsolutePath();
	}

	private static String getCurrentJARDirectory()
	{
		try
		{
			return new File(ProgramDirectoryUtilities.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
		} catch (URISyntaxException exception)
		{
			exception.printStackTrace();
		}

		return null;
	}

	public static boolean isRunningFromIntelliJ()
	{
		String classPath = System.getProperty("java.class.path");
		return classPath.contains("idea_rt.jar");
	}
}