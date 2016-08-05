/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved. http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.widgets.framework.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.utils.Misc;
import com.jaspersoft.studio.utils.UIUtil;
import com.jaspersoft.studio.utils.inputhistory.InputHistoryCache;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.widgets.framework.IPropertyEditor;
import com.jaspersoft.studio.widgets.framework.IWItemProperty;
import com.jaspersoft.studio.widgets.framework.model.WidgetPropertyDescriptor;
import com.jaspersoft.studio.widgets.framework.model.WidgetsDescriptor;
import com.jaspersoft.studio.widgets.framework.ui.menu.IMenuProvider;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;

public class TextPropertyDescription<T> implements ItemPropertyDescription<T> {
	
	protected String name;
	
	protected String label;
	
	protected String description;
	
	protected boolean mandatory;
	
	protected T defaultValue;
	
	protected boolean readOnly;
	
	protected JasperReportsConfiguration jConfig;
	
	protected Text textExpression;
	
	protected IPropertyEditor propertyEditor;

	public TextPropertyDescription() {
		this(null);
	}
	
	public TextPropertyDescription(IPropertyEditor propertyEditor) {
		super();
		this.propertyEditor = propertyEditor;
	}

	public TextPropertyDescription(String name, String description, boolean mandatory, IPropertyEditor editor) {
		this(name, name, description, mandatory, null, editor);
	}

	public TextPropertyDescription(String name, String label, String description, boolean mandatory, IPropertyEditor editor) {
		this(name, label, description, mandatory, null, editor);
	}

	public TextPropertyDescription(String name, String label, String description, boolean mandatory, T defaultValue, IPropertyEditor editor) {
		super();
		this.name = name;
		this.label = label;
		this.description = description;
		this.mandatory = mandatory;
		this.defaultValue = defaultValue;
		this.propertyEditor = editor;
	}

	public void setjConfig(JasperReportsConfiguration jConfig) {
		this.jConfig = jConfig;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getDefaultValueString() {
		if (defaultValue != null)
			return defaultValue.toString();
		return ""; //$NON-NLS-1$
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void handleEdit(Control txt, IWItemProperty wiProp) {
		if (wiProp == null)
			return;
		if (txt instanceof Text) {
			String tvalue = ((Text) txt).getText();
			if (tvalue != null && tvalue.isEmpty())
				tvalue = null;
			if (wiProp.isExpressionMode())
				((JRDesignExpression) wiProp.getExpressionValue()).setText(tvalue);
			else
				wiProp.setValue(tvalue, null);
		}
	}

	// Flag used to overcome the problem of focus events in Mac OS X
	// - JSS Bugzilla 42999
	// - Eclipse Bug 383750
	// It makes sense only on E4 platform and Mac OS X operating systems.
	public Control createControl(final IWItemProperty wiProp, Composite parent) {
		textExpression = new Text(parent, SWT.BORDER);
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.verticalAlignment = SWT.CENTER;
		textData.grabExcessVerticalSpace = true;
		textExpression.setLayoutData(textData);
		InputHistoryCache.bindText(textExpression, name);
		textExpression.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if (UIUtil.isMacAndEclipse4()) {
					if (((Text) e.getSource()).isDisposed())
						return;
					wiProp.updateWidget();
				}
			}

		});
		textExpression.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (wiProp.isRefresh())
					return;
				Point p = ((Text) e.getSource()).getSelection();

				handleEdit(((Text) e.getSource()), wiProp);
				((Text) e.getSource()).setSelection(p);
			}
		});
		setupContextMenu(textExpression, wiProp);

		return textExpression;
	}

	protected void setupContextMenu(final Control c, final IWItemProperty wiProp) {
		IMenuProvider provider = wiProp.getContextualMenuProvider();
		if (provider != null){
			provider.setupMenu(wiProp, this, c);
		}
	}

	public void update(Control c, IWItemProperty wip) {
		if (c instanceof Text) {
			Text txtExpr = (Text) c;
			
			String txt;

			if (wip.isExpressionMode()){
				JRExpression expression = wip.getExpressionValue();
				txt = Misc.nvl(expression.getText());
			} else {
				txt = Misc.nvl(wip.getStaticValue());
			}
			Point oldSelection = txtExpr.getSelection();

			txtExpr.setText(txt);

			oldSelection.x = Math.min(txt.length(), oldSelection.x);
			oldSelection.y = Math.min(txt.length(), oldSelection.y);
			txtExpr.setSelection(oldSelection);

			String tooltip = "";
			if (!Misc.isNullOrEmpty(txt))
				tooltip += "\n\n" + txt;
			tooltip += "\n" + getToolTip();
			txtExpr.setToolTipText(tooltip.trim());
		}
	}
	
	public String getToolTip() {
		String tt = Misc.nvl(getDescription());
		tt += "\n" + (isMandatory() ? "Mandatory" : "Optional");
		if (!Misc.isNullOrEmpty(getDefaultValueString()))
			tt += "\nDefault: " + getDefaultValueString();
		return tt;
	}
	
	public IPropertyEditor getPropertyEditor(){
		return propertyEditor;
	}

	@Override
	public ItemPropertyDescription<T> clone(IPropertyEditor editor){
		TextPropertyDescription<T> result = new TextPropertyDescription<T>(editor);
		result.defaultValue = defaultValue;
		result.description = description;
		result.jConfig = jConfig;
		result.label = label;
		result.mandatory = mandatory;
		result.name = name;
		result.readOnly = readOnly;
		return result;
	}
	
	@Override
	public ItemPropertyDescription<?> getInstance(WidgetsDescriptor cd, WidgetPropertyDescriptor cpd, JasperReportsConfiguration jConfig, IPropertyEditor editor) {
		TextPropertyDescription<?> result = new TextPropertyDescription<String>(cpd.getName(), cd.getLocalizedString(cpd.getLabel()), cd.getLocalizedString(cpd.getDescription()), cpd.isMandatory(), cpd.getDefaultValue(), editor);
		result.setReadOnly(cpd.isReadOnly());
		return result;
	}
}