package com.bullywiihacks.development.swing.utilities;

import javax.lang.model.SourceVersion;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public enum ValidationType
{
	FILE, FOLDER, VARIABLE_NAME, INTEGER;

	public boolean isValid(String input)
	{
		switch (this)
		{
			case FILE:
				return Files.isRegularFile(Paths.get(input));

			case FOLDER:
				return !Files.isRegularFile(Paths.get(input));

			case VARIABLE_NAME:
				return SourceVersion.isName(input);

			case INTEGER:
				return isInteger(input) && Integer.parseInt(input) > 0;
		}

		throw new IllegalStateException("Unhandled enumeration constant: " + this);
	}

	public static boolean isInteger(String input)
	{
		Scanner scanner = new Scanner(input.trim());
		if (!scanner.hasNextInt(10))
		{
			return false;
		}

		scanner.nextInt(10);
		return !scanner.hasNext();
	}
}