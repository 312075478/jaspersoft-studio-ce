/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package com.jaspersoft.studio.prm;

import java.io.ByteArrayInputStream;
import java.util.Map;

import net.sf.jasperreports.eclipse.util.CastorHelper;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.jface.preference.IPreferenceStore;
import org.exolab.castor.mapping.Mapping;
import org.w3c.tools.codec.Base64Decoder;
import org.w3c.tools.codec.Base64Encoder;
import org.w3c.tools.codec.Base64FormatException;
import org.xml.sax.InputSource;

import com.jaspersoft.studio.utils.Misc;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class ParameterSetProvider {
	private JasperReportsConfiguration jrConfig;
	private ParameterSet prmSet;

	public ParameterSetProvider(JasperReportsConfiguration jrConfig) {
		this.jrConfig = jrConfig;
		init();
	}

	public static Mapping getMapping() {
		Mapping mapping = new Mapping();
		mapping.loadMapping(
				new InputSource(ParameterSetProvider.class.getResourceAsStream("/com/jaspersoft/studio/prm/ParameterSet.xml")));
		return mapping;
	}

	private void init() {
		prmSet = null;
		String setName = jrConfig.getProperty(ParameterSet.PARAMETER_SET);
		prmSet = getParameterSet(setName, jrConfig.getPrefStore());
	}

	public static ParameterSet getParameterSet(String setName, IPreferenceStore pstore) {
		if (!Misc.isNullOrEmpty(setName)) {
			String tmp = pstore.getString(ParameterSet.PARAMETER_SET + "." + setName);
			if (!Misc.isNullOrEmpty(tmp)) {
				try {
					tmp = new Base64Decoder(tmp).processString();
				} catch (Base64FormatException e) {
					e.printStackTrace();
					return null;
				}
				try {
					tmp = "<?xml version=\"1.0\"?>\n" + tmp;
					return (ParameterSet) CastorHelper.read(new ByteArrayInputStream(tmp.getBytes()), getMapping());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void storeParameterSet(ParameterSet pset, IPreferenceStore pstore) {
		if (pset != null) {
			String prmset = new Base64Encoder(CastorHelper.write(pset, getMapping())).processString();
			pstore.setValue(ParameterSet.PARAMETER_SET + "." + pset.getName(), prmset);
		}
	}

	public void reset() {
		init();
		initParameterValues(jrConfig.getJRParameters());
	}

	public void initParameterValues(Map<String, Object> prmMap) {
		if (prmSet == null)
			return;
		// ExpressionUtil.getInterpreter(jrd, jConfig, jd), jConfig)
		// for (JRDesignParameter p : prmSet.getParameters())
		// prmMap.put(p.getName(), map.get(p));
	}

	public void addParameters(ParameterSet set, JasperDesign jd) {
		for (JRDesignParameter p : set.getParameters()) {
			if (!jd.getParametersMap().containsKey(p))
				try {
					jd.addParameter((JRParameter) p.clone());
				} catch (JRException e) {
					e.printStackTrace();
				}
		}
	}
}
