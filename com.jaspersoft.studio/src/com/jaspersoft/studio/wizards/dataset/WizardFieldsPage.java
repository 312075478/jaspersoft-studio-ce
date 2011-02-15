/*
 * Jaspersoft Open Studio - Eclipse-based JasperReports Designer. Copyright (C) 2005 - 2010 Jaspersoft Corporation. All
 * rights reserved. http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program is part of iReport.
 * 
 * iReport is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * iReport is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with iReport. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.wizards.dataset;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.design.JRDesignField;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.messages.Messages;

public class WizardFieldsPage extends WizardPage {
	private List<JRDesignField> inFields;
	private List<JRDesignField> outFields;
	private org.eclipse.swt.widgets.List rightField;
	private org.eclipse.swt.widgets.List leftField;

	public void setFields(List<JRDesignField> inFields) {
		if (inFields == null || this.outFields == null)
			fillTables(inFields, new ArrayList<JRDesignField>());
		else {
			// add fields if not exists inside
			for (JRDesignField f : inFields) {
				if (this.inFields.contains(f) || this.outFields.contains(f))
					continue;
				this.inFields.add(f);
				leftField.add(f.getName());
			}
			List<JRDesignField> tmp = new ArrayList<JRDesignField>();
			// remove fields from in
			for (JRDesignField f : this.inFields) {
				if (!inFields.contains(f))
					tmp.add(f);
			}
			removeFields(this.inFields, leftField, tmp);

			tmp.clear();
			// remove fields from out
			for (JRDesignField f : this.outFields) {
				if (!outFields.contains(f))
					tmp.add(f);
			}
			removeFields(this.outFields, rightField, tmp);
		}
	}

	public List<JRDesignField> getFields() {
		return outFields;
	}

	public WizardFieldsPage(String key) {
		super(key); //$NON-NLS-1$
		setTitle(Messages.common_fields);
		setDescription(Messages.WizardFieldsPage_description);
	}

	public WizardFieldsPage() {
		this("tablepage"); //$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		setControl(composite);

		leftField = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.MULTI);
		GridData gd = new GridData();
		gd.widthHint = 200;
		gd.heightHint = 300;
		leftField.setLayoutData(gd);

		Composite bGroup = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		bGroup.setLayout(layout);

		gd = new GridData();
		gd.widthHint = 50;
		gd.heightHint = 300;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		bGroup.setLayoutData(gd);

		final Button addField = new Button(bGroup, SWT.BORDER);
		addField.setText(">"); //$NON-NLS-1$
		addField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addField.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				moveFields(inFields, outFields, leftField, rightField, leftField.getSelectionIndices());
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Button addFields = new Button(bGroup, SWT.BORDER);
		addFields.setText(">>"); //$NON-NLS-1$
		addFields.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addFields.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				int[] index = new int[inFields.size()];
				for (int i = 0; i < inFields.size(); i++)
					index[i] = i;

				moveFields(inFields, outFields, leftField, rightField, index);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		final Button delField = new Button(bGroup, SWT.BORDER);
		delField.setText("<"); //$NON-NLS-1$
		delField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		delField.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				moveFields(outFields, inFields, rightField, leftField, rightField.getSelectionIndices());
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		final Button delFields = new Button(bGroup, SWT.BORDER);
		delFields.setText("<<"); //$NON-NLS-1$
		delFields.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		delFields.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				int[] index = new int[outFields.size()];
				for (int i = 0; i < outFields.size(); i++)
					index[i] = i;

				moveFields(outFields, inFields, rightField, leftField, index);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		rightField = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.MULTI);
		gd = new GridData();
		gd.widthHint = 200;
		gd.heightHint = 300;
		rightField.setLayoutData(gd);

		bGroup = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		bGroup.setLayout(layout);

		gd = new GridData();
		gd.widthHint = 100;
		gd.heightHint = 300;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		bGroup.setLayoutData(gd);

		final Button upField = new Button(bGroup, SWT.BORDER);
		upField.setText(Messages.common_up);
		upField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		upField.setEnabled(false);
		upField.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				int index = rightField.getSelectionIndex();
				if (index > 0) {
					JRDesignField f = outFields.get(index);

					outFields.remove(index);
					rightField.remove(index);

					index--;

					outFields.add(index, f);
					rightField.add(f.getName(), index);

					rightField.select(index);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		final Button downFields = new Button(bGroup, SWT.BORDER);
		downFields.setText(Messages.common_down);
		downFields.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		downFields.setEnabled(false);
		downFields.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				int index = rightField.getSelectionIndex();
				if (index < outFields.size() - 1) {
					JRDesignField f = outFields.get(index);

					outFields.remove(index);
					rightField.remove(index);

					index++;

					outFields.add(index, f);
					rightField.add(f.getName(), index);

					rightField.select(index);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		rightField.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
				upField.setEnabled(false);
				downFields.setEnabled(false);
			}

			public void focusGained(FocusEvent e) {
				upField.setEnabled(rightField.getSelectionIndex() > 0);
				downFields.setEnabled(rightField.getSelectionIndex() < rightField.getItemCount() - 1);
			}
		});
		rightField.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				boolean lsel = rightField.getSelectionCount() > 0 && rightField.isFocusControl();
				upField.setEnabled(lsel && rightField.getSelectionIndex() > 0);
				downFields.setEnabled(lsel && rightField.getSelectionIndex() < rightField.getItemCount() - 1);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

	}

	private void fillTables(List<JRDesignField> inlist, List<JRDesignField> outlist) {
		this.inFields = inlist;
		this.outFields = outlist;
		leftField.removeAll();
		rightField.removeAll();
		if (inFields != null)
			for (JRDesignField f : inFields)
				leftField.add(f.getName());
	}

	public void moveFields(List<JRDesignField> inlist, List<JRDesignField> outlist, org.eclipse.swt.widgets.List llist,
			org.eclipse.swt.widgets.List rlist, int[] index) {
		List<JRDesignField> f = new ArrayList<JRDesignField>();
		for (int i = 0; i < index.length; i++) {
			int ind = index[i];
			JRDesignField inF = inlist.get(ind);

			rlist.add(inF.getName());
			outlist.add(inF);
			f.add(inF);
		}
		removeFields(inlist, llist, f);
	}

	private void removeFields(List<JRDesignField> inlist, org.eclipse.swt.widgets.List llist, List<JRDesignField> f) {
		for (JRDesignField fil : f) {
			int i = inlist.indexOf(fil);
			llist.remove(i);
			inlist.remove(fil);
		}
	}

}
