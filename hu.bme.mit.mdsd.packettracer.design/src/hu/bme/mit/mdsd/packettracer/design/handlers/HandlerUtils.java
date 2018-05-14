package hu.bme.mit.mdsd.packettracer.design.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractDiagramNodeEditPart;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import hu.bme.mit.mdsd.packettracer.Endpoint;

public class HandlerUtils {

	public static List<Endpoint> getTwoSelectedEnpoint(ExecutionEvent event){
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
			
			return endpoints;
		}
		return null;
	}
	
	private static void showError(Shell shell) {
		MessageDialog.openError(shell, "Wrong number of selected items!",
				"You must select two Endpoints to compute the network delay or max bandwith between them!");
	}
}
