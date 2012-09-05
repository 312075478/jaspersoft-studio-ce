/*
 * Jaspersoft Open Studio - Eclipse-based JasperReports Designer. Copyright (C) 2005 - 2010 Jaspersoft Corporation. All
 * rights reserved. http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program is part of Jaspersoft Open Studio.
 * 
 * Jaspersoft Open Studio is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Jaspersoft Open Studio is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Jaspersoft Open Studio. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.editor.action.layout;

import java.util.List;

import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.base.JRBaseElement;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.layout.ILayout;
import com.jaspersoft.studio.editor.layout.LayoutCommand;
import com.jaspersoft.studio.editor.layout.LayoutManager;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.IGraphicElement;
import com.jaspersoft.studio.model.IGraphicElementContainer;
import com.jaspersoft.studio.model.IGroupElement;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.model.MReport;
import com.jaspersoft.studio.property.SetValueCommand;

public class LayoutAction extends SelectionAction {

	/** The Constant ID. */
	public static final String ID = "LayoutGroup"; //$NON-NLS-1$

	private ILayout layout;

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *          The part for this action
	 */
	public LayoutAction(IWorkbenchPart part, Class<?> type) {
		super(part);
		setLazyEnablementCalculation(false);
		layout = LayoutManager.instLayout(type.getName());
		setText(layout.getName());
		setToolTipText(layout.getToolTip());
		setId(type.getName());
		setImageDescriptor(JaspersoftStudioPlugin.getImageDescriptor(layout.getIcon()));
		setDisabledImageDescriptor(JaspersoftStudioPlugin.getImageDescriptor(layout.getIcon()));
		setEnabled(false);
	}

	/**
	 * Returns <code>true</code> if the selected objects can be created. Returns <code>false</code> if there are no
	 * objects selected or the selected objects are not {@link EditPart}s.
	 * 
	 * @return if the command should be enabled
	 */
	protected boolean calculateEnabled() {
		Command cmd = createReorderCommand(getSelectedObjects());
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}

	/**
	 * Create a command to create the selected objects.
	 * 
	 * @param objects
	 *          The objects to be deleted.
	 * @return The command to remove the selected objects.
	 */
	public Command createReorderCommand(List<?> objects) {
		if (objects == null || objects.isEmpty())
			return null;
		Object obj = objects.get(0);
		if (obj instanceof EditPart) {
			ANode n = (ANode) ((EditPart) obj).getModel();
			if (!(n instanceof IGraphicElement))
				return null;

			JRElementGroup container = getContainer(n);
			if (container == null)
				return null;

			Dimension size = null;
			if (container instanceof JRDesignElement) {
				JRDesignElement c = (JRDesignElement) container;
				size = new Dimension(c.getWidth(), c.getHeight());
			} else if (container instanceof JRDesignBand) {
				int h = ((JRDesignBand) container).getHeight();
				JasperDesign jDesign = n.getJasperDesign();
				int w = jDesign.getPageWidth() - jDesign.getLeftMargin() - jDesign.getRightMargin();
				size = new Dimension(w, h);
			} else if (n instanceof IGraphicElementContainer) {
				size = ((IGraphicElementContainer) n).getSize();
				size.expand(((IGraphicElementContainer) n).getLeftPadding(), ((IGraphicElementContainer) n).getTopPadding());
			} else if (n.getParent() instanceof IGraphicElementContainer) {
				IGraphicElementContainer prnt = (IGraphicElementContainer) n.getParent();
				size = prnt.getSize();
				size.expand(prnt.getLeftPadding(), prnt.getTopPadding());
			}
			APropertyNode mcontainer = getContainerNode(n);
			CompoundCommand cc = new CompoundCommand("Layout Elements");
			if (mcontainer.getValue() instanceof JRPropertiesHolder) {
				JRPropertiesMap pmap = (JRPropertiesMap) mcontainer.getPropertyValue(MGraphicElement.PROPERTY_MAP);
				pmap.setProperty(ILayout.KEY, layout.getClass().getName());
				SetValueCommand c = new SetValueCommand();
				c.setTarget((IPropertySource) mcontainer);
				c.setPropertyId(MGraphicElement.PROPERTY_MAP);
				c.setPropertyValue(pmap);
				cc.add(c);
			} else if (mcontainer.getValue() instanceof JRBaseElement) {
				String uuid = ((JRBaseElement) mcontainer.getValue()).getUUID().toString();
				INode root = mcontainer.getRoot();
				if (root != null && n instanceof MReport) {
					MReport mrep = (MReport) n;
					JRPropertiesMap pmap = (JRPropertiesMap) mrep.getPropertyValue(MGraphicElement.PROPERTY_MAP);
					pmap.setProperty(ILayout.KEY + "." + uuid, layout.getClass().getName()); //$NON-NLS-1$
					SetValueCommand c = new SetValueCommand();
					c.setTarget((MReport) root);
					c.setPropertyId(MGraphicElement.PROPERTY_MAP);
					c.setPropertyValue(pmap);
					cc.add(c);
				}
			}
			cc.add(new LayoutCommand(container, layout, size));
			return cc;
		}
		return null;
	}

	private JRElementGroup getContainer(ANode n) {
		Object val = n.getValue();
		if (n instanceof IGroupElement)
			return ((IGroupElement) n).getJRElementGroup();
		if (val instanceof JRElementGroup)
			return (JRElementGroup) val;
		if (val instanceof JRDesignElement)
			return getContainer(n.getParent());
		return null;
	}

	private APropertyNode getContainerNode(ANode n) {
		Object val = n.getValue();
		if (n instanceof IGroupElement)
			return (APropertyNode) n;
		if (val instanceof JRElementGroup)
			return (APropertyNode) n;
		if (val instanceof JRDesignElement)
			return getContainerNode(n.getParent());
		return null;
	}

	/**
	 * Performs the create action on the selected objects.
	 */
	public void run() {
		execute(createReorderCommand(getSelectedObjects()));
	}

}
