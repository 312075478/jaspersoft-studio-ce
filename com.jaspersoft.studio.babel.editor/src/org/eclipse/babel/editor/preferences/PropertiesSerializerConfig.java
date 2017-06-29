/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 *    Alexej Strelzow - moved code to here
 ******************************************************************************/
package org.eclipse.babel.editor.preferences;

import org.eclipse.babel.core.message.resource.ser.IPropertiesSerializerConfig;

/**
 * The concrete implementation of {@link IPropertiesSerializerConfig}.
 * 
 * @author Alexej Strelzow
 */
public class PropertiesSerializerConfig implements IPropertiesSerializerConfig {
    // Moved from MsgEditorPreferences, to make it more flexible.
    

    /**
     * Gets whether to escape unicode characters when generating file.
     * 
     * @return <code>true</code> if escaping
     */
    public boolean isUnicodeEscapeEnabled() {
        return MsgEditorPreferences.getUnicodeEscapeEnabled();
    }

    /**
     * Gets the new line type to use when overwriting system (or Eclipse)
     * default new line type when generating file. Use constants to this effect.
     * 
     * @return new line type
     */
    public int getNewLineStyle() {
        return MsgEditorPreferences.getNewLineStyle();
    }

    /**
     * Gets how many blank lines should separate groups when generating file.
     * 
     * @return how many blank lines between groups
     */
    public int getGroupSepBlankLineCount() {
    	return MsgEditorPreferences.getGroupSeparatorBlankLineCount();
    }

    /**
     * Gets whether to print "Generated By..." comment when generating file.
     * 
     * @return <code>true</code> if we print it
     */
    public boolean isShowSupportEnabled() {
    	return MsgEditorPreferences.getShowSupportEnabled();
    }

    /**
     * Gets whether keys should be grouped when generating file.
     * 
     * @return <code>true</code> if keys should be grouped
     */
    public boolean isGroupKeysEnabled() {
    	return MsgEditorPreferences.getGroupKeysEnabled();
    }

    /**
     * Gets whether escaped unicode "alpha" characters should be uppercase when
     * generating file.
     * 
     * @return <code>true</code> if uppercase
     */
    public boolean isUnicodeEscapeUppercase() {
    	return MsgEditorPreferences.getUnicodeEscapeEnabled();
    }

    /**
     * Gets the number of character after which lines should be wrapped when
     * generating file.
     * 
     * @return number of characters
     */
    public int getWrapLineLength() {
    	return MsgEditorPreferences.getWrapLineLength();
    }



    /**
     * Gets the number of spaces to use for indentation of wrapped lines when
     * generating file.
     * 
     * @return number of spaces
     */
    public int getWrapIndentLength() {
    	return MsgEditorPreferences.getWrapIndentLength();
    }

    /**
     * Gets whether there should be spaces around equals signs when generating
     * file.
     * 
     * @return <code>true</code> there if should be spaces around equals signs
     */
    public boolean isSpacesAroundEqualsEnabled() {
    	return MsgEditorPreferences.getSpacesAroundEqualsEnabled();
    }

    /**
     * Gets whether new lines are escaped or printed as is when generating file.
     * 
     * @return <code>true</code> if printed as is.
     */
    public boolean isNewLineNice() {
    	return MsgEditorPreferences.getNewLineNice();
    }

    /**
     * Gets how many level deep keys should be grouped when generating file.
     * 
     * @return how many level deep
     */
    public int getGroupLevelDepth() {
    	return MsgEditorPreferences.getGroupLevelDeepness();
    }

    /**
     * Gets key group separator.
     * 
     * @return key group separator.
     */
    public String getGroupLevelSeparator() {
    	return MsgEditorPreferences.getGroupLevelSeparator();
    }

    /**
     * Gets whether equals signs should be aligned when generating file.
     * 
     * @return <code>true</code> if equals signs should be aligned
     */
    public boolean isAlignEqualsEnabled() {
    	return MsgEditorPreferences.getAlignEqualsEnabled();
    }

    /**
     * Gets whether equal signs should be aligned within each groups when
     * generating file.
     * 
     * @return <code>true</code> if equal signs should be aligned within groups
     */
    public boolean isGroupAlignEqualsEnabled() {
    	return MsgEditorPreferences.getAlignEqualsEnabled();
    }

    /**
     * Gets whether to sort keys upon serializing them.
     * 
     * @return <code>true</code> if keys are to be sorted.
     */
    public boolean isKeySortingEnabled() {
    	return MsgEditorPreferences.getSortkeys();
    }

	@Override
	public boolean isWrapLinesEnabled() {
		return MsgEditorPreferences.isWrapLinesEnabled();
	}

	@Override
	public boolean isWrapAlignEqualsEnabled() {
		return MsgEditorPreferences.isWrapAlignEqualsEnabled();
	}

}
