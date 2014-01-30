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
package com.jaspersoft.studio.server.wizard.resource.page.olap;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.server.model.MResource;
import com.jaspersoft.studio.server.wizard.resource.APageContent;
import com.jaspersoft.studio.server.wizard.resource.page.selector.SelectorOlapConnection;

public class OlapConnectionContent extends APageContent {
	protected SelectorOlapConnection scompo;

	public OlapConnectionContent(ANode parent, MResource resource, DataBindingContext bindingContext) {
		super(parent, resource, bindingContext);
	}

	public OlapConnectionContent(ANode parent, MResource resource) {
		super(parent, resource);
	}

	@Override
	public String getName() {
		return "OLAP Connection";
	}

	@Override
	public String getPageName() {
		return "com.jaspersoft.studio.server.page.olapConnection";
	}

	@Override
	public String getHelpContext() {
		return "com.jaspersoft.studio.doc.editOlapConnection";
	}

	@Override
	public Control createContent(Composite parent) {
		scompo = new SelectorOlapConnection();
		scompo.addPageCompleteListener(this);
		rebind();
		return scompo.createControls(parent, pnode, res);
	}

	@Override
	protected void rebind() {
	}

	@Override
	public boolean isPageComplete() {
		return scompo != null && scompo.isPageComplete();
	}

}
