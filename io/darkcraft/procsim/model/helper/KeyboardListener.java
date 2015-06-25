package io.darkcraft.procsim.model.helper;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/**
 * A class which is used to detect whether or not the shift or control keys are being held down.
 * @author Shane Booth
 * Adapted from code at {@link}http://stackoverflow.com/questions/10064296/check-whether-shift-key-is-pressed
 *
 */
public class KeyboardListener
{

	private static boolean	shift	= false;
	private static boolean	ctrl	= false;

	static
	{
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher()
		{
			@Override
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				shift = e.isShiftDown();
				ctrl = e.isControlDown();
				return false;
			}
		});
	}

	/**
	 * @return true if shift is held down as far as the program knows.
	 */
	public static boolean isShiftDown()
	{
		return shift;
	}

	/**
	 * @return true if ctrl is held down as far as the program knows.
	 */
	public static boolean isCtrlDown()
	{
		return ctrl;
	}

	public static int getMultiplier(int max)
	{
		if (KeyboardListener.isCtrlDown())
			return KeyboardListener.isShiftDown() ? max : 100;
		else
			return KeyboardListener.isShiftDown() ? 10 : 1;
	}

}