package functionalities.subFunctionalites;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public interface StringQuirks
{
	public boolean isAlphaNumeric(NativeKeyEvent e);
	
	public boolean isAlphaNumericInet(NativeKeyEvent e);
}
