package hu.bme.mit.mdsd.packettracer.design;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;

import hu.bme.mit.mdsd.packettracer.Connection;
import hu.bme.mit.mdsd.packettracer.Connector;
import hu.bme.mit.mdsd.packettracer.ConnectorType;
import hu.bme.mit.mdsd.packettracer.PacketTracerDiagram;
import hu.bme.mit.mdsd.packettracer.PackettracerFactory;

public class CreateConnection implements IExternalJavaAction {

	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {
		if (selections.size() > 2) {
			return false;
		}
		Set<ConnectorType> types = new HashSet<ConnectorType>();
		for(EObject selectedItem : selections) {
			if(!Connector.class.isInstance(selectedItem))
				return false;
			types.add(((Connector)selectedItem).getType());
		}
		
		return types.size() == 1;
	}

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> parameters) {
		Connector source = (Connector) parameters.get("source");
		Connector target = (Connector) parameters.get("target");
		
		PackettracerFactory factory = PackettracerFactory.eINSTANCE;
		
		Connection connection = factory.createConnection();
		connection.setBandwidth(0);
		connection.setType(source.getType());
		connection.setFrom(source);
		connection.setTo(target);
		PacketTracerDiagram diagram = (PacketTracerDiagram) source.eContainer().eContainer().eContainer();
		diagram.getConnections().add(connection);
	}

}
