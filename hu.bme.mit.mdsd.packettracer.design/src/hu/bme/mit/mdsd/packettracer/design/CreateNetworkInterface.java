package hu.bme.mit.mdsd.packettracer.design;

import java.util.Collection;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;

import packettracer.NetworkInterface;
import packettracer.NetworkNode;
import packettracer.PackettracerFactory;

public class CreateNetworkInterface implements IExternalJavaAction {

	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {
		if (selections.size() != 1) {
			return false;
		}
		EObject selectedItem = selections.iterator().next();
		return NetworkNode.class.isInstance(selectedItem);
	}

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> arg1) {
		if (selections.size() != 1) {
			return;
		}
		EObject selectedItem = selections.iterator().next();
		if (!NetworkNode.class.isInstance(selectedItem)) {
			return;
		}
		NetworkNode node = NetworkNode.class.cast(selectedItem);
		PackettracerFactory factory = PackettracerFactory.eINSTANCE;
		NetworkInterface newInterface = factory.createNetworkInterface();
		newInterface.setAddress("0.0.0.0");
		node.getNetworkinterface().add(newInterface);
	}

}
