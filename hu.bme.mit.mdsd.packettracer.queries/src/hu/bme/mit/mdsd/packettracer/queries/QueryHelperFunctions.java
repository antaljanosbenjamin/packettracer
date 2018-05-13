package hu.bme.mit.mdsd.packettracer.queries;

import org.apache.commons.net.util.SubnetUtils;
import org.eclipse.xtext.xbase.lib.Pure;

public class QueryHelperFunctions {

	@Pure
	public static boolean isInvalidIPAddress(String ipAddress) {
		String[] decimalBytes = ipAddress.split("\\.");
		if (decimalBytes.length != 4)
			return true;

		for (String decimalByte : decimalBytes) {
			try {
				Integer decimalValue = Integer.parseInt(decimalByte);
				return !(decimalValue >= 0 && decimalValue <= 255);
			} catch (Exception e) {
				return true;
			}
		}

		return false;
	}

	@Pure
	public static boolean isInSubnet(String subnetIP, Integer subnetMaskLength, String ipAddress) {
		SubnetUtils utils = new SubnetUtils(subnetIP + "/" + Integer.toString(subnetMaskLength));
		return utils.getInfo().isInRange(ipAddress);
	}

	@Pure
	public static Double min(Double d1, Double d2) {
		return d1 < d2 ? d1 : d2;
	}

	@Pure
	public static Double min(Double d1, Double d2, Double d3) {
		return min(min(d1, d2), d3);
	}

}
