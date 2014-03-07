/*******************************************************************************
 * Copyright (C) 2010 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, 
 * the following license terms apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jaspersoft Studio Team - initial API and implementation
 ******************************************************************************/
package com.jaspersoft.studio.components.table.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.components.table.action.EditStyleAction;
import com.jaspersoft.studio.components.table.action.RemoveTableStylesAction;
import com.jaspersoft.studio.components.table.messages.Messages;
import com.jaspersoft.studio.components.table.model.MTable;
import com.jaspersoft.studio.components.table.model.column.action.CreateColumnAfterAction;
import com.jaspersoft.studio.components.table.model.column.action.CreateColumnBeforeAction;
import com.jaspersoft.studio.components.table.model.column.action.CreateColumnBeginAction;
import com.jaspersoft.studio.components.table.model.column.action.CreateColumnCellAction;
import com.jaspersoft.studio.components.table.model.column.action.CreateColumnEndAction;
import com.jaspersoft.studio.components.table.model.column.action.DeleteColumnAction;
import com.jaspersoft.studio.components.table.model.column.action.DeleteColumnCellAction;
import com.jaspersoft.studio.components.table.model.column.action.DeleteRowAction;
import com.jaspersoft.studio.components.table.model.columngroup.action.CreateColumnGroupAction;
import com.jaspersoft.studio.components.table.model.columngroup.action.UnGroupColumnsAction;
import com.jaspersoft.studio.editor.gef.parts.JasperDesignEditPartFactory;
import com.jaspersoft.studio.editor.gef.parts.MainDesignerRootEditPart;
import com.jaspersoft.studio.editor.gef.rulers.ReportRuler;
import com.jaspersoft.studio.editor.gef.rulers.ReportRulerProvider;
import com.jaspersoft.studio.editor.outline.actions.CreateConditionalStyleAction;
import com.jaspersoft.studio.editor.outline.actions.CreateStyleAction;
import com.jaspersoft.studio.editor.outline.actions.CreateStyleTemplateReferenceAction;
import com.jaspersoft.studio.editor.outline.actions.ExportStyleAsTemplateAction;
import com.jaspersoft.studio.editor.outline.actions.ResetStyleAction;
import com.jaspersoft.studio.editor.report.AbstractVisualEditor;
import com.jaspersoft.studio.preferences.RulersGridPreferencePage;
import com.jaspersoft.studio.property.dataset.dialog.DatasetAction;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

/*
 * The Class TableEditor.
 * 
 * @author Chicu Veaceslav
 */
public class TableEditor extends AbstractVisualEditor {
	public TableEditor(JasperReportsConfiguration jrContext) {
		super(jrContext);
		setPartName(Messages.TableEditor_table);
		setPartImage(JaspersoftStudioPlugin.getInstance().getImage(
				MTable.getIconDescriptor().getIcon16()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().getControl().setBackground(ColorConstants.button);

		GraphicalViewer graphicalViewer = getGraphicalViewer();
		MainDesignerRootEditPart rootEditPart = new MainDesignerRootEditPart();

		graphicalViewer.setRootEditPart(rootEditPart);
		// set EditPartFactory
		graphicalViewer.setEditPartFactory(new JasperDesignEditPartFactory());

		// set rulers providers
		RulerProvider provider = new ReportRulerProvider(new ReportRuler(true,
				RulerProvider.UNIT_PIXELS));
		graphicalViewer.setProperty(RulerProvider.PROPERTY_HORIZONTAL_RULER,
				provider);

		provider = new ReportRulerProvider(new ReportRuler(false,
				RulerProvider.UNIT_PIXELS));
		graphicalViewer.setProperty(RulerProvider.PROPERTY_VERTICAL_RULER,
				provider);

		Boolean isRulerVisible = jrContext
				.getPropertyBoolean(RulersGridPreferencePage.P_PAGE_RULERGRID_SHOWRULER);

		graphicalViewer.setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY,
				isRulerVisible);

		createAdditionalActions();
		graphicalViewer.setKeyHandler(new GraphicalViewerKeyHandler(
				graphicalViewer));
	}

	@Override
	protected List<String> getIgnorePalleteElements() {
		List<String> lst = new ArrayList<String>();
		lst.add("com.jaspersoft.studio.components.crosstab.model.MCrosstab"); //$NON-NLS-1$
		return lst;
	}

	@Override
	protected void createEditorActions(ActionRegistry registry) {
		createDatasetActions(registry);
		
		IAction action = new CreateColumnEndAction(this);
		registry.registerAction(action);
		@SuppressWarnings("unchecked")
		List<String> selectionActions = getSelectionActions();
		selectionActions.add(CreateColumnEndAction.ID);

		action = new CreateColumnGroupAction(this);
		registry.registerAction(action);
		selectionActions.add(CreateColumnGroupAction.ID);
		
		action = new EditStyleAction(this);
		registry.registerAction(action);
		selectionActions.add(EditStyleAction.ID);
		
		action = new CreateColumnBeginAction(this);
		registry.registerAction(action);
		selectionActions.add(CreateColumnBeginAction.ID);

		action = new CreateColumnBeforeAction(this);
		registry.registerAction(action);
		selectionActions.add(CreateColumnBeforeAction.ID);

		action = new CreateColumnAfterAction(this);
		registry.registerAction(action);
		selectionActions.add(CreateColumnAfterAction.ID);

		action = new RemoveTableStylesAction(this);
		registry.registerAction(action);
		selectionActions.add(RemoveTableStylesAction.ID);

		action = new UnGroupColumnsAction(this);
		registry.registerAction(action);
		selectionActions.add(UnGroupColumnsAction.ID);

		action = new CreateColumnCellAction(this);
		registry.registerAction(action);
		selectionActions.add(CreateColumnCellAction.ID);

		action = new DeleteColumnAction(this);
		registry.registerAction(action);
		selectionActions.add(DeleteColumnAction.ID);

		action = new DeleteColumnCellAction(this);
		registry.registerAction(action);
		selectionActions.add(DeleteColumnCellAction.ID);
		
		action = new DeleteRowAction(this);
		registry.registerAction(action);
		selectionActions.add(DeleteRowAction.ID);

		action = new DatasetAction(this);
		registry.registerAction(action);
		selectionActions.add(action.getId());
		
		//Create the styles action
		action = new ResetStyleAction(this);
		registry.registerAction(action);
		selectionActions.add(ResetStyleAction.ID);
		
		action = new CreateStyleAction(this);
		registry.registerAction(action);
		selectionActions.add(CreateStyleAction.ID);

		action = new CreateStyleTemplateReferenceAction(this);
		registry.registerAction(action);
		selectionActions.add(CreateStyleTemplateReferenceAction.ID);
		
		action = new CreateConditionalStyleAction(this);
		registry.registerAction(action);
		selectionActions.add(CreateConditionalStyleAction.ID);
		
		action = new ExportStyleAsTemplateAction(this);
		registry.registerAction(action);
		selectionActions.add(ExportStyleAsTemplateAction.ID);
	}

	@Override
	public void contributeItemsToEditorTopToolbar(IToolBarManager toolbarManager) {
		toolbarManager.add(getActionRegistry().getAction(DatasetAction.ID));
		toolbarManager.add(new Separator());
		super.contributeItemsToEditorTopToolbar(toolbarManager);
	}
}
