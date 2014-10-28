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
package com.jaspersoft.studio.property.dataset.wizard;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.templates.JrxmlTemplateBundle;
import com.jaspersoft.studio.wizards.CongratulationsWizardPage;
import com.jaspersoft.studio.wizards.ContextHelpIDs;
import com.jaspersoft.studio.wizards.JSSWizard;
import com.jaspersoft.studio.wizards.ReportNewWizard;

public class WizardFieldsGroupByDynamicPage extends WizardFieldsDynamicPage {
	
	private Map<String, Object> settings = null;
	
	private JrxmlTemplateBundle containerBundle;

	public WizardFieldsGroupByDynamicPage(JrxmlTemplateBundle containerBundle) {
		super("groupfields"); //$NON-NLS-1$
		setTitle(Messages.WizardFieldsGroupByPage_group_by);
		setDescription(Messages.WizardFieldsGroupByPage_description);
		this.containerBundle = containerBundle;
	}

	/**
	 * Return the context name for the help of this page
	 */
	@Override
	protected String getContextName() {
		return ContextHelpIDs.WIZARD_SELECT_GROUP;
	}

	/**
	 * This procedure initialize the dialog page with the list of available objects.
	 * This implementation looks for object set in the map as DATASET_FIELDS
	 * if this is for real just the first time the page is shown.
	 * 
	 */
	public void loadSettings() {
		
		if (getSettings() == null) return;
		
		if (getSettings().containsKey( WizardDataSourceDynamicPage.DATASET_FIELDS))
		{
			setAvailableFields( (List<?>)(getSettings().get( WizardDataSourceDynamicPage.DATASET_FIELDS )) );
		}
		else
		{
			setAvailableFields(null);
		}
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		Button buttonCreate = new Button(mainComposite, SWT.CHECK | SWT.WRAP);
		buttonCreate.setText(Messages.WizardFieldsGroupByPage_createSortfields);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1);
		data.widthHint = 600;
		buttonCreate.setLayoutData(data);
		buttonCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean createSortFields = ((Button)e.getSource()).getSelection();
				getLocalSettings().put(WizardDataSourceDynamicPage.ORDER_GROUP, createSortFields);
			}
		});
		getLocalSettings().put(WizardDataSourceDynamicPage.ORDER_GROUP, false);
	}
	
	private Map<String, Object> getLocalSettings(){
		if (settings == null && getWizard() != null && getWizard() instanceof JSSWizard){
				settings = ((JSSWizard)getWizard()).getSettings();
		}
		return settings;
	}
	
	
	/**
	 * Every time a new selection occurs, the selected fields are stored in the settings map
	 * with the key WizardDataSourcePage.GROUP_FIELDS
	 */
	@Override
	public void storeSettings()
	{
			Map<String, Object> settings = getLocalSettings();
			if (settings == null) return;
			settings.put(WizardDataSourceDynamicPage.GROUP_FIELDS,  getSelectedFields() ); // the type is IPath
	}
	
	/**
	 * Return the congratulation page if it is available
	 * 
	 * @return the congratulation page or null if the congratulation page
	 * shouldn't be displayed
	 */
	@Override
	public IWizardPage getNextPage() {
		CongratulationsWizardPage congratPage = ((ReportNewWizard)getWizard()).getCongratulationsStep();
		congratPage.setWizard(getWizard());
		return (congratPage);
	}
	
	/**
	 * Return the second dynamic page
	 * 
	 * @return the page to configure the fields
	 */
	@Override
	public IWizardPage getPreviousPage() {
		containerBundle.getStep2().setWizard(getWizard());
		return containerBundle.getStep2();
	}
	
	/**
	 * To advance to the next page the page must have a status complete
	 * and there must be the congratulation page
	 */
	@Override
	public boolean canFlipToNextPage() {
		ReportNewWizard container = (ReportNewWizard)getWizard();
		return isPageComplete() && container.hasCongratulationStep();
	}
}