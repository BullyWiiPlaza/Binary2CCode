package com.bullywiihacks.development;

import com.bullywiihacks.development.swing.Binary2CCodeGUI;
import com.bullywiihacks.development.swing.utilities.ProgramDirectoryUtilities;
import com.bullywiihacks.development.swing.utilities.ValidationType;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Binary2CCode
{
	private final String binaryFilePath;
	private final String headerFileName;
	private final String bufferVariableName;
	private final int lineBreakInterval;
	private final boolean isJava;

	public Binary2CCode(String binaryFilePath,
	                    String headerFileName,
	                    String bufferVariableName,
	                    int lineBreakInterval,
	                    boolean isJava)
	{
		this.binaryFilePath = binaryFilePath;
		this.headerFileName = headerFileName;
		this.bufferVariableName = bufferVariableName;
		this.lineBreakInterval = lineBreakInterval;
		this.isJava = isJava;
	}

	public File generate() throws IOException
	{
		Path binaryFile = Paths.get(binaryFilePath);
		byte[] binaryFileBytes = Files.readAllBytes(binaryFile);

		Path headerFilePath = Paths.get(ProgramDirectoryUtilities.getProgramDirectory() + File.separator + headerFileName);
		byte[] generatedBytes = getHeaderFileBytes(binaryFileBytes);
		Files.write(headerFilePath, generatedBytes);

		return headerFilePath.toFile();
	}

	private byte[] getHeaderFileBytes(byte[] binaryFileBytes)
	{
		StringBuilder bufferBuilder = new StringBuilder();

		if (!ValidationType.VARIABLE_NAME.isValid(bufferVariableName))
		{
			throw new IllegalArgumentException("The buffer variable name " + bufferVariableName + " is not valid!");
		}

		if (isJava)
		{
			bufferBuilder.append("byte[] ");
			bufferBuilder.append(bufferVariableName);
		} else
		{
			bufferBuilder.append("static const unsigned char ");
			bufferBuilder.append(bufferVariableName);
			bufferBuilder.append("[]");
		}

		bufferBuilder.append(" = {");
		bufferBuilder.append(System.lineSeparator());

		for (int bytesIndex = 0; bytesIndex < binaryFileBytes.length; bytesIndex++)
		{
			byte singleByte = binaryFileBytes[bytesIndex];

			if (isJava && singleByte < 0)
			{
				bufferBuilder.append("(byte) ");
			}

			bufferBuilder.append("0x");
			bufferBuilder.append(String.format("%02X", singleByte));

			boolean isLastIndex = bytesIndex == binaryFileBytes.length - 1;

			if (!isLastIndex)
			{
				bufferBuilder.append(",");
			}

			if (lineBreakInterval < 0)
			{
				throw new IllegalArgumentException("The line break interval was " + lineBreakInterval + " but cannot be negative!");
			}

			if (FormattingUtilities.shouldInsertLineSeparator(bytesIndex, lineBreakInterval) && !isLastIndex)
			{
				bufferBuilder.append(System.lineSeparator());
			} else
			{
				bufferBuilder.append(" ");
			}
		}

		bufferBuilder.append(System.lineSeparator());
		bufferBuilder.append("};");

		if (!isJava)
		{
			bufferBuilder.append(System.lineSeparator());
			bufferBuilder.append(System.lineSeparator());
			bufferBuilder.append("static const unsigned int ");
			bufferBuilder.append(bufferVariableName);
			bufferBuilder.append("Length");
			bufferBuilder.append(" = ");
			bufferBuilder.append("0x");
			bufferBuilder.append(Integer.toHexString(binaryFileBytes.length).toUpperCase());
			bufferBuilder.append(";");
		}

		return bufferBuilder.toString().getBytes(Charset.defaultCharset());
	}

	public static void runCommandLineInterface(String[] arguments)
	{
		Options options = new Options();

		String inputBinaryArgument = "input-binary";
		Option inputOption = new Option("i", inputBinaryArgument, true, "Input binary file");
		inputOption.setRequired(true);
		options.addOption(inputOption);

		String headerFileNameArgument = "header-file-name";
		Option outputOption = new Option("h", headerFileNameArgument, true, "Header file name");
		outputOption.setRequired(true);
		options.addOption(outputOption);

		String lineBreakIntervalArgument = "line-break-interval";
		Option lineBreakIntervalOption = new Option("l", lineBreakIntervalArgument, true, "The amount of bytes per line");
		lineBreakIntervalOption.setRequired(true);
		options.addOption(lineBreakIntervalOption);

		String bufferVariableNameArgument = "buffer-variable-name";
		Option bufferVariableNameOption = new Option("b", bufferVariableNameArgument, true, "The buffer's variable name");
		bufferVariableNameOption.setRequired(true);
		options.addOption(bufferVariableNameOption);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine command;

		try
		{
			command = parser.parse(options, arguments);
		} catch (ParseException exception)
		{
			String errorMessage = exception.getMessage();
			System.out.println(errorMessage);

			String helpMessage = "java -jar ";
			if (ProgramDirectoryUtilities.isRunningFromJAR())
			{
				helpMessage += ProgramDirectoryUtilities.getJarName();
			} else
			{
				helpMessage += Binary2CCodeGUI.APPLICATION_TITLE + ".jar";
			}

			formatter.printHelp(helpMessage, options);

			System.exit(1);
			return;
		}

		String inputBinaryFilePath = command.getOptionValue(inputBinaryArgument);
		String outputHeaderFilePath = command.getOptionValue(headerFileNameArgument);
		String bufferVariableName = command.getOptionValue(bufferVariableNameArgument);
		int lineBreakIndex = Integer.parseInt(command.getOptionValue(lineBreakIntervalArgument));

		Binary2CCode binary2CCode = new Binary2CCode(inputBinaryFilePath, outputHeaderFilePath, bufferVariableName, lineBreakIndex, false);

		try
		{
			binary2CCode.generate();
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}
}