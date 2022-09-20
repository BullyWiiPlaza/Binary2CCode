package com.bullywiihacks.development.swing;

import com.bullywiihacks.development.Binary2CCode;
import com.bullywiihacks.development.swing.utilities.SimpleProperties;
import com.bullywiihacks.development.swing.utilities.ValidationType;
import com.bullywiihacks.development.swing.utilities.WindowUtilities;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Binary2CCodeGUI extends JFrame
{
	private JPanel rootPanel;

	private JTextField sourceBinaryFilePathField;
	private JFormattedTextField lineBreaksIntervalField;
	private JTextField headerFileNameField;
	private JTextField bufferVariableNameField;
	// private JTextField targetFolderPathField;
	private JButton generateHeaderFileButton;
	private JButton aboutButton;
	private JCheckBox javaCheckBox;

	private SimpleProperties simpleProperties;

	public Binary2CCodeGUI()
	{
		setFrameProperties();

		addGenerateHeaderFileButtonListener();
		addAboutButtonListener();
		startComponentAvailabilitySetter();
		handlePersistentSettings();
	}

	private void handlePersistentSettings()
	{
		simpleProperties = new SimpleProperties();
		restorePersistentSettings();
		addPersistentSettingsBackupShutdownHook();
	}

	private void restorePersistentSettings()
	{
		String sourceBinaryFileName = simpleProperties.get(PersistentSetting.SOURCE_BINARY_FILE_PATH.toString());
		if (sourceBinaryFileName != null)
		{
			sourceBinaryFilePathField.setText(sourceBinaryFileName);
		}

		String headerFileName = simpleProperties.get(PersistentSetting.HEADER_FILE_NAME.toString());
		if (headerFileName != null)
		{
			headerFileNameField.setText(headerFileName);
		}

		String bufferVariableName = simpleProperties.get(PersistentSetting.BUFFER_VARIABLE_NAME.toString());
		if (bufferVariableName != null)
		{
			bufferVariableNameField.setText(bufferVariableName);
		}

		String lineBreakInterval = simpleProperties.get(PersistentSetting.LINE_BREAK_INTERVAL.toString());
		if (lineBreakInterval != null)
		{
			lineBreaksIntervalField.setText(lineBreakInterval);
		}

		/*String targetFolderPath = simpleProperties.get(PersistentSetting.TARGET_FOLDER_PATH.toString());
		if (targetFolderPath != null)
		{
			targetFolderPathField.setText(targetFolderPath);
		}*/
	}

	private void addPersistentSettingsBackupShutdownHook()
	{
		Thread thread = new Thread(() ->
		{
			simpleProperties.put(PersistentSetting.SOURCE_BINARY_FILE_PATH.toString(), sourceBinaryFilePathField.getText());
			simpleProperties.put(PersistentSetting.HEADER_FILE_NAME.toString(), headerFileNameField.getText());
			simpleProperties.put(PersistentSetting.BUFFER_VARIABLE_NAME.toString(), bufferVariableNameField.getText());
			simpleProperties.put(PersistentSetting.LINE_BREAK_INTERVAL.toString(), lineBreaksIntervalField.getText());
			// simpleProperties.put(PersistentSetting.TARGET_FOLDER_PATH.toString(), targetFolderPathField.getText());
			simpleProperties.writeToFile();
		});

		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(thread);
	}

	private void addAboutButtonListener()
	{
		aboutButton.addActionListener(actionEvent -> JOptionPane.showMessageDialog(rootPanel,
				"This application let's you convert a binary file to a C array\n" +
						"and generate the header file for it.\n\n" +
						"This is only meant to be used by C program developers\n" +
						"in order to integrate binary files directly in the source code.\n\n" +
						"Copyright \u00A9 2017 BullyWiiPlaza Productions",
				aboutButton.getText(),
				JOptionPane.INFORMATION_MESSAGE));
	}

	private void startComponentAvailabilitySetter()
	{
		Thread thread = new Thread(() ->
		{
			// Wait till the frame is set visible
			while (!isShowing())
			{
				try
				{
					Thread.sleep(10);
				} catch (InterruptedException exception)
				{
					exception.printStackTrace();
				}
			}

			while (isShowing())
			{
				boolean canGenerate = validateField(sourceBinaryFilePathField, ValidationType.FILE)
						// & validateField(targetFolderPathField, ValidationType.FOLDER)
						& validateField(headerFileNameField, ValidationType.VARIABLE_NAME)
						& validateField(bufferVariableNameField, ValidationType.VARIABLE_NAME)
						& validateField(lineBreaksIntervalField, ValidationType.INTEGER);

				generateHeaderFileButton.setEnabled(canGenerate);

				try
				{
					Thread.sleep(10);
				} catch (InterruptedException exception)
				{
					exception.printStackTrace();
				}
			}
		});

		thread.setName("Component Availability Setter");
		thread.start();
	}

	private boolean validateField(JTextField textField, ValidationType validationType)
	{
		boolean isValid;

		try
		{
			String text = textField.getText();
			isValid = validationType.isValid(text);
		} catch (Exception exception)
		{
			isValid = false;
		}

		textField.setBackground(isValid ? Color.GREEN : Color.RED);

		return isValid;
	}

	private void addGenerateHeaderFileButtonListener()
	{
		generateHeaderFileButton.addActionListener(actionEvent ->
		{
			try
			{
				String sourceBinaryFilePath = sourceBinaryFilePathField.getText();
				String headerFileName = headerFileNameField.getText();
				String bufferVariableName = bufferVariableNameField.getText();
				int lineBreakIndex = Integer.parseInt(lineBreaksIntervalField.getText());
				Binary2CCode binary2CCode = new Binary2CCode(sourceBinaryFilePath, headerFileName, bufferVariableName, lineBreakIndex, javaCheckBox.isSelected());
				File headerFile = binary2CCode.generate();
				JOptionPane.showMessageDialog(rootPanel,
						"Header file generated!",
						"Success",
						JOptionPane.INFORMATION_MESSAGE);
				Desktop desktop = Desktop.getDesktop();
				desktop.open(headerFile);
			} catch (Exception exception)
			{
				exception.printStackTrace();
			}
		});
	}

	public static final String APPLICATION_TITLE = "Binary2CCode";

	private void setFrameProperties()
	{
		add(rootPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle(APPLICATION_TITLE);
		WindowUtilities.setIconImage(this);
		setLocationRelativeTo(null);
		pack();
	}
}