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
import hu.bme.mit.mdsd.packettracer.NetworkNode;
import hu.bme.mit.mdsd.packettracer.PacketTracerDiagram;
import hu.bme.mit.mdsd.packettracer.design.Services;
import hu.bme.mit.mdsd.packettracer.design.utils.Graph;
import hu.bme.mit.mdsd.packettracer.design.utils.MaxFlow;
import hu.bme.mit.mdsd.packettracer.design.utils.Vertex;
import hu.bme.mit.mdsd.packettracer.queries.ConnectedDirectlyWithBandwithMatcher;
import hu.bme.mit.mdsd.packettracer.queries.util.ConnectedDirectlyWithBandwithProcessor;

public class CalculateMaxFlowHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<Endpoint> endpoints = HandlerUtils.getTwoSelectedEnpoint(event);
		if (endpoints != null) {
			try {
				Double maxFlow = calculateMaxFlow(endpoints.get(0), endpoints.get(1));
				MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Result",
						"Calculated max bandwith between " + endpoints.get(0).getName() + " and "
								+ endpoints.get(1).getName() + " is " + Services.getBandwithWithUnit(maxFlow) + "!");
			} catch (ViatraQueryException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Map<NetworkInterface, Vertex> getVertexesFromNetworkNode(NetworkNode node) {
		Map<NetworkInterface, Vertex> vertices = new HashMap<NetworkInterface, Vertex>();

		for (NetworkInterface eInterface : node.getNetworkInterfaces()) {
			vertices.put(eInterface, new Vertex(eInterface.toString()));
		}

		return vertices;
	}

	private Double calculateMaxFlow(Endpoint source, Endpoint target) throws ViatraQueryException {
		PacketTracerDiagram diagram = PacketTracerDiagram.class.cast(source.eContainer());

		EMFScope scope = new EMFScope(diagram);
		final ViatraQueryEngine engine = ViatraQueryEngine.on(scope);

		Map<NetworkInterface, Vertex> i2v = new HashMap<NetworkInterface, Vertex>();
		for (NetworkDevice device : diagram.getNetworkDevices()) {
			i2v.putAll(getVertexesFromNetworkNode(device));
		}
		for (Endpoint device : diagram.getEndpoints()) {
			i2v.putAll(getVertexesFromNetworkNode(device));
		}

		Graph graph = new Graph();
		graph.getVertexes().addAll(i2v.values());

		ConnectedDirectlyWithBandwithMatcher matcher = ConnectedDirectlyWithBandwithMatcher.on(engine);

		matcher.forEachMatch(new ConnectedDirectlyWithBandwithProcessor() {

			@Override
			public void process(NetworkInterface n1, NetworkInterface n2, Double bandwith) {
				graph.addBidirectionalEdge(i2v.get(n1), i2v.get(n2), bandwith);
			}
		});

		Vertex sourceVertex = new Vertex("Source_" + source.getName());
		Vertex targetVertex = new Vertex("Target_" + target.getName());
		graph.addVertex(sourceVertex);
		graph.addVertex(targetVertex);

		for (NetworkInterface eInterface : source.getNetworkInterfaces()) {
			graph.addBidirectionalEdge(sourceVertex, i2v.get(eInterface), Double.MAX_VALUE);
		}
		for (NetworkInterface eInterface : target.getNetworkInterfaces()) {
			graph.addBidirectionalEdge(targetVertex, i2v.get(eInterface), Double.MAX_VALUE);
		}

		MaxFlow maxFlow = new MaxFlow();

		return maxFlow.calculateMaxFlow(graph, sourceVertex, targetVertex);
	}

}
