package hu.bme.mit.mdsd.packettracer.design;

import hu.bme.mit.mdsd.packettracer.Connection;

/**
 * The services class used by VSM.
 */
public class Services {
    
	private static String [] UNITS = {"Kb/s", "Mb/s", "Gb/s", "Tb/s", "Pb/s"};
	private static double DIVIDER = 1024.0;
    /**
    * See http://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.sirius.doc%2Fdoc%2Findex.html&cp=24 for documentation on how to write service methods.
    */
	
	public static String getBandwithWithUnit(Double bandwith) {
		int unitSelector = 1;
		while(bandwith >= DIVIDER && unitSelector < UNITS.length) {
			bandwith /= DIVIDER;
			unitSelector++;
		}
		unitSelector--;
		return String.format("%.2f " + UNITS[unitSelector], bandwith);
	}
	
	public static String getBandwithLabel(Connection connection) {
		double realBandwith =  Math.min(Math.min(connection.getBandwidth(), connection.getFrom().getBandwidth()),connection.getTo().getBandwidth());
		return getBandwithWithUnit(realBandwith) + " (" + getBandwithWithUnit(connection.getBandwidth()) + ")";
		
	}
   
}
