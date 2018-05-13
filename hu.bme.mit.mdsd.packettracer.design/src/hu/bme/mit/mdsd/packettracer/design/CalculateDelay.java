package hu.bme.mit.mdsd.packettracer.design;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;

public class CalculateDelay implements IExternalJavaAction {

	@Override
	public void execute(Collection<? extends EObject> selections, Map<String, Object> parameters) {
		System.out.println("selection: " + selections.size());
		for(String param : parameters.keySet()) {
			System.out.println(param + ":" + parameters.get(param).toString());
		}
		System.out.println(parameters.size());
	}

	@Override
	public boolean canExecute(Collection<? extends EObject> selections) {
		System.out.println(selections.size());
		// TODO Auto-generated method stub
		return true;
	}

}
