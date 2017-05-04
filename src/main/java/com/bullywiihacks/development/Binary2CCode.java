package com.bullywiihacks.development;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Binary2CCode
{
	private final String binaryFilePath;
	private final String baseFileName;
	private final String bufferVariableName;
	private final int lineBreakInterval;

	public Binary2CCode(String binaryFilePath,
	                    String baseFileName,
	                    String bufferVariableName,
	                    int lineBreakInterval)
	{
		this.binaryFilePath = binaryFilePath;
		this.baseFileName = baseFileName;
		this.bufferVariableName = bufferVariableName;
		this.lineBreakInterval = lineBreakInterval;
	}

	public File generate(String targetDirectory) throws IOException
	{
		Path binaryFile = Paths.get(binaryFilePath);
		byte[] binaryFileBytes = Files.readAllBytes(binaryFile);

		Path directoryPath = Paths.get(targetDirectory);
		Files.createDirectories(directoryPath);

		Path headerFilePath = directoryPath.resolve(baseFileName + ".h");
		byte[] generatedBytes = getHeaderFileBytes(binaryFileBytes);
		Files.write(headerFilePath, generatedBytes);

		return headerFilePath.toFile();
	}

	private byte[] getHeaderFileBytes(byte[] binaryFileBytes)
	{
		StringBuilder bufferBuilder = new StringBuilder();
		bufferBuilder.append("static const unsigned char ");
		bufferBuilder.append(bufferVariableName);
		bufferBuilder.append("[] = {");
		bufferBuilder.append(System.lineSeparator());

		for (int bytesIndex = 0; bytesIndex < binaryFileBytes.length; bytesIndex++)
		{
			byte singleByte = binaryFileBytes[bytesIndex];
			bufferBuilder.append("0x");
			bufferBuilder.append(String.format("%02X", singleByte));

			boolean isLastIndex = bytesIndex == binaryFileBytes.length - 1;

			if (!isLastIndex)
			{
				bufferBuilder.append(",");
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
		bufferBuilder.append(System.lineSeparator());
		bufferBuilder.append(System.lineSeparator());
		bufferBuilder.append("static const unsigned int ");
		bufferBuilder.append(bufferVariableName);
		bufferBuilder.append("Length");
		bufferBuilder.append(" = ");
		bufferBuilder.append("0x");
		bufferBuilder.append(Integer.toHexString(binaryFileBytes.length).toUpperCase());
		bufferBuilder.append(";");

		return bufferBuilder.toString().getBytes(Charset.defaultCharset());
	}
}