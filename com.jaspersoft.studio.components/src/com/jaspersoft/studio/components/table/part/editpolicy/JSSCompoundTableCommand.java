/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 * 
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.components.table.part.editpolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jaspersoft.studio.JSSCompoundCommand;
import com.jaspersoft.studio.components.table.model.MTable;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.property.SetValueCommand;

import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.ColumnGroup;
import net.sf.jasperreports.components.table.StandardBaseColumn;
import net.sf.jasperreports.components.table.StandardColumnGroup;

/**
 * Command used to set the property of one or more table cells. It will do 
 * the autoresize of the table after executing the commands contained in this 
 * one, if tthe flag is enabled
 * 
 * Allow also to update the span of all the cells in the table when the command
 * is executed, undone o redone
 * 
 * @author Orlandin Marco
 *
 */
public class JSSCompoundTableCommand extends JSSCompoundCommand {

	/**
	 * The table containing the resized columns
	 */
	private MTable table;
	
	/**
	 * Flag used to know if on the execute the span of the cells should be updated
	 */
	private boolean updateTableSpan = false;
	
	/**
	 * Map to store the columns size before to set the flag, it is used on the undo
	 */
	private HashMap<BaseColumn, Integer> originalColumnsSize = null;
	
	/**
	 * This flag allow to layout the table content after the inner commands are executed
	 */
	private boolean layoutTableContent = false;
	
	/**
	 * Create the command for the resize
	 * 
	 * @param table a not null table containing the resized columns
	 * @param updateTableSpan if set to true will update the span of 
	 * all the cells in the table when the command is executed, undone o redone
	 */
	public JSSCompoundTableCommand(MTable table, boolean updateTableSpan){
		super("Change Cell Size", table);
		this.table = table;
		this.updateTableSpan = updateTableSpan;
	}
	
	/**
	 * Create the command for the resize
	 * 
	 * @param table a not null table containing the resized columns
	 */
	public JSSCompoundTableCommand(MTable table){
		this(table, false);
	}
	
	/**
	 * Create a general command
	 * 
	 * @param commandText the textual name of the command
	 * @param table a not null table containing the resized columns
	 * @param updateTableSpan if set to true will update the span of 
	 * all the cells in the table when the command is executed, undone o redone
	 */
	public JSSCompoundTableCommand(String commandText, MTable table, boolean updateTableSpan){
		super(commandText, table);
		this.table = table;
		this.updateTableSpan = updateTableSpan;
	}
	
	/**
	 * Set the width of a column
	 * 
	 * @param model the not null model of the column
	 * @param width the new width of the column
	 */
	public void setWidth(APropertyNode model, int width){
		SetValueCommand setCommand = new SetValueCommand();
		setCommand.setTarget(model);
		setCommand.setPropertyId(StandardBaseColumn.PROPERTY_WIDTH);
		setCommand.setPropertyValue(width);
		add(setCommand);
	}
	
	@Override
	public void execute() {
		if (table.hasColumnsAutoresizeProportional()){
			//Store the current column widths
			storeColumnsSize();
			//execute the innter command
			super.execute();
			//fill the space
			boolean changed = table.getTableManager().fillSpace(table.getValue().getWidth(), true);
			if (!changed){
				//The size was already right (probably because of the restoreColumnsSize) so the cells was not
				//layouted after the undo, trigger a manual layout
				JSSCompoundCommand layoutCommands = table.getTableManager().getLayoutCommand();
				layoutCommands.setReferenceNodeIfNull(table);
				layoutCommands.execute();
			}
		} else {
			super.execute();
			if (layoutTableContent){
				//The size was already right (probably because of the restoreColumnsSize) so the cells was not
				//layouted after the undo, trigger a manual layout
				JSSCompoundCommand layoutCommands = table.getTableManager().getLayoutCommand();
				layoutCommands.setReferenceNodeIfNull(table);
				layoutCommands.execute();
			}
		}
		if (updateTableSpan){
			table.getTableManager().updateTableSpans();
		}
	}

	
	@Override
	public void undo() {
		if (table.hasColumnsAutoresizeProportional()){
			//Undo the original command
			super.undo();
			//Restore the original size
			restoreColumnsSize(table.getStandardTable().getColumns());
			//If the table still doesn't fit the width then update it
			boolean changed = table.getTableManager().fillSpace(table.getValue().getWidth(), true);
			if (!changed){
				//The size was already right (probably because of the restoreColumnsSize) so the cells was not
				//layouted after the undo, trigger a manual layout
				JSSCompoundCommand layoutCommands = table.getTableManager().getLayoutCommand();
				layoutCommands.setReferenceNodeIfNull(table);
				layoutCommands.execute();
			}
		} else {
			super.undo();
			if (layoutTableContent){
				//The size was already right (probably because of the restoreColumnsSize) so the cells was not
				//layouted after the undo, trigger a manual layout
				JSSCompoundCommand layoutCommands = table.getTableManager().getLayoutCommand();
				layoutCommands.setReferenceNodeIfNull(table);
				layoutCommands.execute();
			}
		}
		if (updateTableSpan){
			table.getTableManager().updateTableSpans();
		}
	}
	
	@Override
	public void redo() {
		if (table.hasColumnsAutoresizeProportional()){
			storeColumnsSize();
			super.redo();
			table.getTableManager().fillSpace(table.getValue().getWidth(), true);
		} else {
			super.redo();
			if (layoutTableContent){
				//The size was already right (probably because of the restoreColumnsSize) so the cells was not
				//layouted after the undo, trigger a manual layout
				JSSCompoundCommand layoutCommands = table.getTableManager().getLayoutCommand();
				layoutCommands.setReferenceNodeIfNull(table);
				layoutCommands.execute();
			}
		}
		if (updateTableSpan){
			table.getTableManager().updateTableSpans();
		}
	}
	
	/**
	 * Store the width of every column in the table in the width map
	 */
	protected void storeColumnsSize(){
		List<BaseColumn> columns = getAllColumns(table.getStandardTable().getColumns());
		originalColumnsSize = new HashMap<BaseColumn, Integer>();
		for(BaseColumn column : columns){
			originalColumnsSize.put(column, column.getWidth());
		}
	}
	
	/**
	 * Restore the width of every columns in the table with the one cached
	 * 
	 * @param columns the current set of columns, since it is a recursive method
	 */
	protected void restoreColumnsSize(List<BaseColumn> columns){
		if (originalColumnsSize != null){
			for(BaseColumn column : columns){
				if (column instanceof StandardColumnGroup){
					StandardColumnGroup groupColumn = (StandardColumnGroup)column;
					restoreColumnsSize(groupColumn.getColumns());
				}
				Integer originalWidth = originalColumnsSize.get(column);
				if (originalWidth != null){
					((StandardBaseColumn)column).setWidth(originalWidth);
				}
			}
		}
	}
	
	/**
	 * Return a list of every columns in the table, considering also the
	 * ColumnGroup
	 * 
	 * @param cols the current set of columns, it is a recursive method
	 * @return the list of columns contained in the passed parameter (considering
	 * also the subcolumns contained by the columns groups)
	 */
	protected List<BaseColumn> getAllColumns(List<BaseColumn> cols) {
		List<BaseColumn> lst = new ArrayList<BaseColumn>();
		for (BaseColumn bc : cols) {
			if (bc instanceof ColumnGroup){
				lst.addAll(getAllColumns(((ColumnGroup) bc).getColumns()));
			} 
			lst.add(bc);
		}
		return lst;
	}
	
	/**
	 * Set the layout table content flag. This flag allow to layout
	 * the table content after the inner commands are executed
	 * 
	 * @param value true if the contents should be layouted, false otherwise
	 */
	public void setLayoutTableContent(boolean value){
		this.layoutTableContent = value;
	}

	/**
	 * Can execute only if the table reference exist
	 */
	@Override
	public boolean canExecute() {
		return table != null;
	}
	
	/**
	 * Can undo only if the table reference exist
	 */
	@Override
	public boolean canUndo() {
		return table != null;
	}
}