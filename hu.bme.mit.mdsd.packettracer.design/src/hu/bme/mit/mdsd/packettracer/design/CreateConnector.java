package hu.bme.mit.mdsd.packettracer.design;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;

import hu.bme.mit.mdsd.packettracer.Connector;
import hu.bme.mit.mdsd.packettracer.ConnectorType;
import hu.bme.mit.mdsd.packettracer.NetworkInterface;
import hu.bme.mit.mdsd.packettracer.PackettracerFactory;

public class CreateConnector implements IExternalJavaAction {

	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {
		if (selections.size() != 1) {
			return false;
		}
		EObject selectedItem = selections.iterator().next();
		return NetworkInterface.class.isInstance(selectedItem);
	}

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> arg1) {
		if (selections.size() != 1) {
			return;
		}
		EObject selectedItem = selections.iterator().next();
		if (!NetworkInterface.class.isInstance(selectedItem)) {
			return;
		}
		NetworkInterface networkInterface = NetworkInterface.class.cast(selectedItem);
		PackettracerFactory factory = PackettracerFactory.eINSTANCE;
		Connector newConnector = factory.createConnector();
		newConnector.setType(ConnectorType.RJ45);
		networkInterface.getConnectors().add(newConnector);
	}

}
