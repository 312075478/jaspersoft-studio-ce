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

package com.jaspersoft.studio.editor.style;

import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignStaticText;

import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.gef.figures.ImageFigure;
import com.jaspersoft.studio.editor.gef.figures.StaticTextFigure;
import com.jaspersoft.studio.editor.gef.figures.borders.CornerBorder;
import com.jaspersoft.studio.editor.gef.figures.borders.ElementLineBorder;
import com.jaspersoft.studio.editor.gef.parts.FigureEditPart;
import com.jaspersoft.studio.editor.style.editpolicy.ElementEditPolicy;
import com.jaspersoft.studio.model.style.MStyle;
import com.jaspersoft.studio.preferences.DesignerPreferencePage;

public class StyleEditPart extends FigureEditPart {

	private StaticTextFigure textF;
	private ImageFigure imageF;
	private JRDesignStaticText textE;
	private JRDesignImage imageE;
	private GridData gd;

	@Override
	protected IFigure createFigure() {
		RectangleFigure rf = new RectangleFigure();
		rf.setBorder(new LineBorder(ColorConstants.lightGray));
		GridLayout lm = new GridLayout(2, false);
		lm.marginHeight = 20;
		lm.marginWidth = 20;
		lm.horizontalSpacing = 20;
		rf.setLayoutManager(lm);

		MStyle st = (MStyle) getModel();
		JRStyle style = (JRStyle) st.getValue();

		textE = new JRDesignStaticText();
		textE.setX(20);
		textE.setY(20);
		textE.setWidth(200);
		textE.setHeight(100);
		textE.setText("Static Text for style: " + style.getName());
		textE.setStyle(style);

		imageE = new JRDesignImage(null);
		imageE.setX(textE.getX() * 2 + textE.getWidth());
		imageE.setY(textE.getY());
		imageE.setWidth(100);
		imageE.setHeight(textE.getHeight());
		imageE.setStyle(style);

		rf.setSize(textE.getX() * 3 + textE.getWidth() + imageE.getWidth(), textE.getY() * 2 + textE.getHeight());

		textF = new StaticTextFigure();
		textF.setJRElement(textE, drawVisitor);

		imageF = new ImageFigure();
		imageF.setJRElement(imageE, drawVisitor);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = textE.getHeight() + 20;
		gd.widthHint = textE.getWidth() + 5;
		lm.setConstraint(textF, gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = imageE.getHeight() + 20;
		gd.widthHint = imageE.getWidth() + 5;
		lm.setConstraint(imageF, gd);

		rf.add(textF);
		rf.add(imageF);
		setPrefsBorder(rf);

		return rf;
	}

	public void setPrefsBorder(IFigure rect) {
		String pref = Platform.getPreferencesService().getString(JaspersoftStudioPlugin.getUniqueIdentifier(),
				DesignerPreferencePage.P_ELEMENT_DESIGN_BORDER_STYLE, "rectangle", null); //$NON-NLS-1$

		if (pref.equals("rectangle")) { //$NON-NLS-1$
			imageF.setBorder(new ElementLineBorder(ColorConstants.black));
			textF.setBorder(new ElementLineBorder(ColorConstants.black));
		} else {
			imageF.setBorder(new CornerBorder(ColorConstants.black, 5));
			textF.setBorder(new CornerBorder(ColorConstants.black, 5));
		}
	}

	@Override
	protected void setupFigure(IFigure rect) {
		LayoutManager lm = rect.getParent().getLayoutManager();
		Rectangle b = rect.getBounds();
		if (gd == null) {
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = b.height;
			gd.widthHint = b.width;
			lm.setConstraint(rect, gd);
		}
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ElementEditPolicy());
	}

}
