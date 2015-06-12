package io.darkcraft.procsim.model.helper;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class KeyboardListener
{

	private static boolean	shift;
	private static boolean	ctrl;

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

	public static boolean isShiftDown()
	{
		return shift;
	}

	public static boolean isCtrlDown()
	{
		return ctrl;
	}

}