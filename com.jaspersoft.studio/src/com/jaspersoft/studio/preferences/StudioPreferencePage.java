/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved. http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.preferences;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;
import net.sf.jasperreports.eclipse.util.FilePrefUtil;
import net.sf.jasperreports.eclipse.util.FileUtils;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.compatibility.JRXmlWriterHelper;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.preferences.util.FieldEditorOverlayPage;
import com.jaspersoft.studio.statistics.BooleanLinkFieldEditor;
import com.jaspersoft.studio.utils.Misc;

public class StudioPreferencePage extends FieldEditorOverlayPage {
	public static final String PAGE_ID = "com.jaspersoft.studio.preferences.StudioPreferencePage.property"; //$NON-NLS-1$
	public static final String JSS_COMPATIBILITY_SHOW_DIALOG = "com.jaspersoft.studio.compatibility.showdialog"; //$NON-NLS-1$
	public static final String JSS_COMPATIBILITY_VERSION = "com.jaspersoft.studio.compatibility.version"; //$NON-NLS-1$
	public static final String REFERENCE_PREFIX = "net.sf.jasperreports.doc/docs/config.reference.html?cp=0_2#"; //$NON-NLS-1$
	public static final String JSS_TIMESTAMP_ONSAVE = "com.jaspersoft.studio.timestamp.onsave"; //$NON-NLS-1$

	public static final String JSS_SEND_USAGE_STATISTICS = "com.jaspersoft.studio.send_usage"; //$NON-NLS-1$

	public static final String CHECK_FOR_UPDATE = "show_update_dialog"; //$NON-NLS-1$

	public StudioPreferencePage() {
		super(GRID);
		setPreferenceStore(JaspersoftStudioPlugin.getInstance().getPreferenceStore());
		// setDescription("A demonstration of a preference page implementation");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new ComboFieldEditor(JSS_COMPATIBILITY_VERSION, Messages.StudioPreferencePage_versionLabel,
				JRXmlWriterHelper.getVersions(), getFieldEditorParent()));

		addField(new BooleanFieldEditor(JSS_COMPATIBILITY_SHOW_DIALOG, Messages.StudioPreferencePage_showCompDialog,
				getFieldEditorParent()));

		Label label = new Label(getFieldEditorParent(), SWT.WRAP);
		label.setText(Messages.StudioPreferencePage_message1);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		label = new Label(getFieldEditorParent(), SWT.WRAP);
		label.setText(Messages.StudioPreferencePage_message2);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		Label separator = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		addField(new BooleanFieldEditor(JSS_TIMESTAMP_ONSAVE, Messages.StudioPreferencePage_TimestampOnSave,
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(JRXmlWriter.PREFIX_EXCLUDE_PROPERTIES + "jss", //$NON-NLS-1$
				Messages.StudioPreferencePage_1, getFieldEditorParent()) {
			private Properties props;

			@Override
			protected void doStore() {
				boolean b = getBooleanValue();
				boolean exists = false;
				Properties newProps = new Properties();
				String pvalue = "com\\.jaspersoft\\.studio\\..*"; //$NON-NLS-1$
				for (Object item : props.keySet()) {
					String key = (String) item;
					String value = null;
					if (!exists && key.equals(getPreferenceName())) {
						if (b) {
							value = pvalue;
							exists = true;
						} else
							continue;
					} else
						value = props.getProperty(key);
					newProps.setProperty(key, value);
				}
				if (b && !exists)
					newProps.setProperty(getPreferenceName(), pvalue);
				getPreferenceStore().setValue(FilePrefUtil.NET_SF_JASPERREPORTS_JRPROPERTIES,
						FileUtils.getPropertyAsString(newProps));
			}

			@Override
			protected void doLoad() {
				Button checkBox = getChangeControl(getFieldEditorParent());
				if (checkBox != null) {
					try {
						props = FileUtils.load(getPreferenceStore().getString(FilePrefUtil.NET_SF_JASPERREPORTS_JRPROPERTIES));
						String value = props.getProperty(getPreferenceName());
						boolean b = !Misc.isNullOrEmpty(value);
						checkBox.setSelection(b);
						setWasSelected(b);
					} catch (IOException e) {
						UIUtils.showError(e);
					}
				}
			}

			private void setWasSelected(boolean value) {
				try {
					Field field = super.getClass().getSuperclass().getDeclaredField("wasSelected"); //$NON-NLS-1$
					field.setAccessible(true);
					field.setBoolean(this, value);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void doLoadDefault() {
				Button checkBox = getChangeControl(getFieldEditorParent());
				if (checkBox != null) {
					checkBox.setSelection(false);
					setWasSelected(false);
				}
			}
		});

		separator = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		addField(new BooleanFieldEditor(CHECK_FOR_UPDATE, Messages.StudioPreferencePage_checkForUpdates,
				getFieldEditorParent()));
		addField(new BooleanLinkFieldEditor(JSS_SEND_USAGE_STATISTICS,
				Messages.StudioPreferencePage_collectUsageStatistics, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

	public static void getDefaults(IPreferenceStore store) {
		store.setDefault(JSS_COMPATIBILITY_SHOW_DIALOG, "false"); //$NON-NLS-1$ 
		store.setDefault(JSS_COMPATIBILITY_VERSION, "last"); //$NON-NLS-1$
		store.setDefault(JSS_TIMESTAMP_ONSAVE, "true"); //$NON-NLS-1$
		store.setDefault(CHECK_FOR_UPDATE, "true"); //$NON-NLS-1$
		store.setDefault(JSS_SEND_USAGE_STATISTICS, "false"); //$NON-NLS-1$
	}

	@Override
	protected String getPageId() {
		return PAGE_ID;
	}

}
