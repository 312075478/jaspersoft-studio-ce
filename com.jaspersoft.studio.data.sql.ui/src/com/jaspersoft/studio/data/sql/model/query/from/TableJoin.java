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
package com.jaspersoft.studio.data.sql.model.query.from;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;

public class TableJoin implements Serializable {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	private MFromTable fromTable;
	private MFromTableJoin joinTable;

	public TableJoin(MFromTableJoin foreignTable, MFromTable primaryTable) {
		super();
		this.fromTable = primaryTable;
		this.joinTable = foreignTable;
		fromTable.addTableJoin(this);
	}

	public MFromTableJoin getJoinTable() {
		return joinTable;
	}

	public MFromTable getFromTable() {
		return fromTable;
	}

	public void setFromTable(MFromTable targetPrimaryKey) {
		this.fromTable = targetPrimaryKey;
	}

	public void setJoinTable(MFromTableJoin sourceForeignKey) {
		this.joinTable = sourceForeignKey;
	}

}
