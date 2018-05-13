package hu.bme.mit.mdsd.packettracer.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractDiagramNodeEditPart;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

import hu.bme.mit.mdsd.packettracer.Endpoint;
import hu.bme.mit.mdsd.packettracer.NetworkDevice;
import hu.bme.mit.mdsd.packettracer.NetworkInterface;
import hu.bme.mit.mdsd.packettracer.PacketTracerDiagram;
import hu.bme.mit.mdsd.packettracer.design.utils.DijkstraAlgorithm;
import hu.bme.mit.mdsd.packettracer.design.utils.Graph;
import hu.bme.mit.mdsd.packettracer.design.utils.Vertex;
import hu.bme.mit.mdsd.packettracer.queries.ConnectedByConnectionMatcher;
import hu.bme.mit.mdsd.packettracer.queries.ConnectedEndpointWithInterfacesMatcher;
import hu.bme.mit.mdsd.packettracer.queries.util.ConnectedByConnectionProcessor;

public class CalculateDelayHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		System.out.println("EXECUTED");
		IWorkbenchWindow activeWindows = HandlerUtil.getActiveWorkbenchWindow(event);
		ISelection selection = activeWindows.getActivePage().getSelection();
		if (selection == null || !(selection instanceof IStructuredSelection)) {
			showError(activeWindows.getShell());
			return null;
		}
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			if (strucSelection.size() != 2) {
				showError(activeWindows.getShell());
				return null;
			}
			List<Endpoint> endpoints = new ArrayList<Endpoint>();
			IEditorPart activeEditor = activeWindows.getActivePage().getActiveEditor();
			if (activeEditor.getSite().getId()
					.equals("hu.bme.mit.mdsd.packettracer.presentation.PackettracerEditorID")) {
				for (Iterator<?> iterator = strucSelection.iterator(); iterator.hasNext();) {
					Object element = iterator.next();
					if (!Endpoint.class.isInstance(element)) {
						showError(activeWindows.getShell());
					}
					endpoints.add(Endpoint.class.cast(element));
				}
			} else if (activeEditor.getSite().getId()
					.equals("org.eclipse.sirius.diagram.ui.part.SiriusDiagramEditorID")) {
				for (Iterator<?> iterator = strucSelection.iterator(); iterator.hasNext();) {
					Object element = iterator.next();
					if (!AbstractDiagramNodeEditPart.class.isInstance(element)) {
						showError(activeWindows.getShell());
					}
					EObject eElement = AbstractDiagramNodeEditPart.class.cast(element).resolveDiagramElement()
							.getTarget();
					if (!Endpoint.class.isInstance(eElement)) {
						showError(activeWindows.getShell());
					}
					endpoints.add(Endpoint.class.cast(Endpoint.class.cast(eElement)));
				}
			} else {
				MessageDialog.openError(activeWindows.getShell(), "Unknown editor",
						"Ups, this command can not be applied in this editor!");
			}

			try {
				Double minDelay = calculateShortestPath(endpoints.get(0), endpoints.get(1));
				MessageDialog.openInformation(activeWindows.getShell(), "Result", "Calculated delay is " + minDelay);
			} catch (ViatraQueryException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void showError(Shell shell) {
		MessageDialog.openError(shell, "Wrong number of selected items!",
				"You must select 2 NetworkNode to compute the network delay between them!");

	}

	private Double calculateShortestPath(Endpoint source, Endpoint target) throws ViatraQueryException {
		PacketTracerDiagram diagram = PacketTracerDiagram.class.cast(source.eContainer());

		EMFScope scope = new EMFScope(diagram);
		final ViatraQueryEngine engine = ViatraQueryEngine.on(scope);

		ConnectedEndpointWithInterfacesMatcher directMathcer = ConnectedEndpointWithInterfacesMatcher.on(engine);
		if (directMathcer.countMatches(source, null, target, null) > 0)
			return 0.0;

		Map<NetworkInterface, Vertex> interfaceToVertex = new HashMap<NetworkInterface, Vertex>();
		Graph graph = new Graph();

		for (NetworkDevice device : diagram.getNetworkDevices()) {
			System.out.println("Device " + device.getControls().getNetworkPrefix());
			for (NetworkInterface i1 : device.getNetworkInterfaces()) {
				Vertex v = new Vertex(i1.getAddress());
				if (!graph.addVertex(v)) {
					System.err.println("Problem with " + i1.getAddress());
				} else {
					interfaceToVertex.put(i1, v);
				}
			}

			for (NetworkInterface i1 : device.getNetworkInterfaces()) {
				for (NetworkInterface i2 : device.getNetworkInterfaces()) {
					graph.addBidirectionalEdge(interfaceToVertex.get(i1), interfaceToVertex.get(i2),
							Double.valueOf(device.getDelay()));
				}
			}
		}

		Vertex targetVertex = new Vertex("Target_" + target.getName());
		Vertex sourceVertex = new Vertex("Source_" + source.getName());
		graph.addVertex(sourceVertex);
		graph.addVertex(targetVertex);

		System.out.println("Source " + source.getName());
		for (NetworkInterface eInterface : source.getNetworkInterfaces()) {
			Vertex v = new Vertex(eInterface.getAddress());
			interfaceToVertex.put(eInterface, v);
			graph.addVertex(v);
			graph.addBidirectionalEdge(sourceVertex, v, 0.0);
		}

		System.out.println("Target " + target.getName());
		for (NetworkInterface eInterface : target.getNetworkInterfaces()) {
			Vertex v = new Vertex(eInterface.getAddress());
			interfaceToVertex.put(eInterface, v);
			graph.addVertex(v);
			graph.addBidirectionalEdge(v, targetVertex, 0.0);
		}

		ConnectedByConnectionMatcher matcher = ConnectedByConnectionMatcher.on(engine);

		for (NetworkDevice device : diagram.getNetworkDevices()) {
			for (NetworkInterface eInterface : device.getNetworkInterfaces()) {
				System.out.println(
						"Device " + device.getControls().getNetworkPrefix() + " interface " + eInterface.getAddress());
				matcher.forEachMatch(matcher.newMatch(eInterface, null), new ConnectedByConnectionProcessor() {

					@Override
					public void process(NetworkInterface i1, NetworkInterface i2) {
						if (interfaceToVertex.containsKey(i1) && interfaceToVertex.containsKey(i2)) {
							Vertex v = new Vertex(i1.getAddress());
							graph.addVertex(v);
							graph.addBidirectionalEdge(interfaceToVertex.get(i1), interfaceToVertex.get(i2), 0.0);
						} else {
							System.out.println("Cannot find vertex for " + i1.getAddress() + " or " + i2.getAddress());
						}
					}
				});
			}
		}

		// for (NetworkInterface eInterface : source.getNetworkInterfaces()) {
		// matcher.forEachMatch(matcher.newMatch(eInterface, null), new
		// ConnectedDirectlyProcessor() {
		//
		// @Override
		// public void process(NetworkInterface i1, NetworkInterface i2) {
		// if (interfaceToVertex.containsKey(i2)) {
		// Vertex v = new Vertex(i2.getAddress());
		// graph.addVertex(v);
		// graph.addBidirectionalEdge(sourceVertex, v, 0.0);
		// graph.addBidirectionalEdge(v, interfaceToVertex.get(i2), 0.0);
		// }
		// }
		// });
		// }
		//
		// for (NetworkInterface eInterface : target.getNetworkInterfaces()) {
		// matcher.forEachMatch(matcher.newMatch(eInterface, null), new
		// ConnectedDirectlyProcessor() {
		//
		// @Override
		// public void process(NetworkInterface i1, NetworkInterface i2) {
		// if (interfaceToVertex.containsKey(i2)) {
		// Vertex v = new Vertex(i2.getAddress());
		// graph.addVertex(v);
		// graph.addBidirectionalEdge(interfaceToVertex.get(i2), v, 0.0);
		// graph.addBidirectionalEdge(v, targetVertex, 0.0);
		// }
		// }
		// });
		// }

		DijkstraAlgorithm.computePaths(sourceVertex);
		return targetVertex.minDistance;
	}

}
