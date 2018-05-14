package hu.bme.mit.mdsd.packettracer.design.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
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
		List<Endpoint> endpoints = HandlerUtils.getTwoSelectedEnpoint(event);
		if (endpoints != null) {
			try {
				Double minDelay = calculateShortestPath(endpoints.get(0), endpoints.get(1));
				MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Result",
						"Calculated delay  between " + endpoints.get(0).getName() + " and " + endpoints.get(1).getName()
								+ " is " + minDelay.intValue() + "ms!");
			} catch (ViatraQueryException e) {
				e.printStackTrace();
			}
		}
		return null;
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
				Vertex v = new Vertex(i1.toString());
				if (!graph.addVertex(v)) {
					System.err.println("Problem with " + i1.toString());
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
			Vertex v = new Vertex(eInterface.toString());
			interfaceToVertex.put(eInterface, v);
			graph.addVertex(v);
			graph.addBidirectionalEdge(sourceVertex, v, 0.0);
		}

		System.out.println("Target " + target.getName());
		for (NetworkInterface eInterface : target.getNetworkInterfaces()) {
			Vertex v = new Vertex(eInterface.toString());
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
							Vertex v = new Vertex(i1.toString());
							graph.addVertex(v);
							graph.addBidirectionalEdge(interfaceToVertex.get(i1), interfaceToVertex.get(i2), 0.0);
						} else {
							System.out.println("Cannot find vertex for " + i1.toString() + " or " + i2.toString());
						}
					}
				});
			}
		}

		DijkstraAlgorithm.computePaths(sourceVertex);
		return targetVertex.minDistance;
	}

}
