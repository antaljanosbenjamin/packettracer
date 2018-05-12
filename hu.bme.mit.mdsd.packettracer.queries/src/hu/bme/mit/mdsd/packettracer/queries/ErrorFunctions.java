package hu.bme.mit.mdsd.packettracer.queries;

import org.eclipse.xtext.xbase.lib.Pure;

public class ErrorFunctions {
	
	@Pure
	public static boolean isInvalidIPAddress(String ipAddress) {
		String[] decimalBytes = ipAddress.split("\\.");
		if (decimalBytes.length != 4)
			return true;
		
		for(String decimalByte : decimalBytes) {
			try{
				Integer decimalValue = Integer.parseInt(decimalByte);
				return !(decimalValue >= 0 && decimalValue <= 255);
			} catch (Exception e) {
				return true;
			}
		}
		
		return false;
	}

}
