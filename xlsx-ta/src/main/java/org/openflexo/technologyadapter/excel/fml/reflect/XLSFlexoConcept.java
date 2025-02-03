/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.excel.fml.reflect;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;

/**
 * Specialization of a {@link FlexoConcept} used in a {@link XLSVirtualModel} to define a FML-contractualized access to an
 * {@link ExcelWorkbook}
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(XLSFlexoConcept.XLSFlexoConceptImpl.class)
@XMLElement
// TODO : refactor !
@Deprecated // We should use "classical" FlexoConcept with specific annotations
public interface XLSFlexoConcept extends FlexoConcept {

	@Override
	public XLSVirtualModel getOwningVirtualModel();

	public ExcelWorkbookResource getTemplateExcelWorkbookResource();

	@Override
	public XLSFlexoConceptInstanceType getInstanceType();

	public static abstract class XLSFlexoConceptImpl extends FlexoConceptImpl implements XLSFlexoConcept {

		private static final Logger logger = Logger.getLogger(XLSFlexoConceptImpl.class.getPackage().getName());

		private final XLSFlexoConceptInstanceType instanceType = new XLSFlexoConceptInstanceType(this);

		@Override
		public XLSVirtualModel getOwningVirtualModel() {
			return (XLSVirtualModel) super.getOwningVirtualModel();
		}

		@Override
		public ExcelWorkbookResource getTemplateExcelWorkbookResource() {
			if (getOwningVirtualModel() != null) {
				return getOwningVirtualModel().getTemplateExcelWorkbookResource();
			}
			return null;
		}

		@Override
		public XLSFlexoConceptInstanceType getInstanceType() {
			return instanceType;
		}
	}
}
