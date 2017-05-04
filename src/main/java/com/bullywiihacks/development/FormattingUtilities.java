package com.bullywiihacks.development;

public class FormattingUtilities
{
	public static boolean shouldInsertLineSeparator(int currentIndex, int lineBreakIndex)
	{
		int divisionResult = currentIndex / lineBreakIndex;
		int multiplicationResult = divisionResult * lineBreakIndex;
		int currentIndexUpdated = currentIndex - multiplicationResult;

		return currentIndexUpdated == lineBreakIndex - 1;
	}
}