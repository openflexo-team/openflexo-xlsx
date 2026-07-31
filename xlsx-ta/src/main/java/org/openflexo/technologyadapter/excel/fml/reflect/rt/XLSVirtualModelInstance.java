/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
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
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
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

package org.openflexo.technologyadapter.excel.fml.reflect.rt;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.reflect.ReflectedVirtualModelInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.FMLExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;

/**
 * A {@link VirtualModelInstance} reflecting XLS sheets objects accessible through a {@link FMLExcelModelSlot} configured with a
 * {@link VirtualModel}<br>
 * 
 */
@ModelEntity
@ImplementationClass(XLSVirtualModelInstance.XLSVirtualModelInstanceImpl.class)
@Imports(@Import(XLSFlexoConceptInstance.class))
@XMLElement
public interface XLSVirtualModelInstance
		extends ReflectedVirtualModelInstance<XLSVirtualModelInstance, ExcelWorkbookResource, ExcelWorkbook, ExcelTechnologyAdapter> {

	/**
	 * Explicitly add a new reflected instance of the concept named <code>conceptName</code> to this reflected
	 * {@link XLSVirtualModelInstance}: a new row is appended to the sheet declared by the concept's <code>@DataRange</code>
	 * meta-data, and a {@link XLSFlexoConceptInstance} backed by that new row is created and registered.<br>
	 * Setting a typed property on the returned instance writes back to the corresponding cell. Returns null when the
	 * concept, its sheet or the workbook cannot be resolved.
	 */
	XLSFlexoConceptInstance createReflectedInstance(String conceptName);

	abstract class XLSVirtualModelInstanceImpl
			extends ReflectedVirtualModelInstanceImpl<XLSVirtualModelInstance, ExcelWorkbookResource, ExcelWorkbook, ExcelTechnologyAdapter>
			implements XLSVirtualModelInstance {

		private static final Logger logger = FlexoLogger.getLogger(XLSVirtualModelInstance.class.getPackage().toString());

		@Override
		public XLSFlexoConceptInstance createReflectedInstance(String conceptName) {
			VirtualModel vm = getVirtualModel();
			if (vm == null) {
				logger.warning("Cannot create reflected instance: no virtual model bound to " + this);
				return null;
			}
			FlexoConcept concept = vm.getFlexoConcept(conceptName);
			if (concept == null) {
				logger.warning("Cannot create reflected instance: no concept '" + conceptName + "' in " + vm);
				return null;
			}
			String sheetName = XLSVirtualModelInstanceBuilder.getSheetName(concept);
			ExcelWorkbook workbook = (getReflectedResource() != null ? getReflectedResource().getExcelWorkbook() : null);
			if (sheetName == null || workbook == null) {
				logger.warning("Cannot create reflected instance for " + concept + ": sheet=" + sheetName + " workbook=" + workbook);
				return null;
			}
			ExcelSheet sheet = workbook.getExcelSheetByName(sheetName);
			if (sheet == null) {
				logger.warning("Cannot create reflected instance for " + concept + ": sheet '" + sheetName + "' not found");
				return null;
			}
			ExcelRow newRow = sheet.createNewRow();
			try {
				XLSVirtualModelInstanceModelFactory factory = (XLSVirtualModelInstanceModelFactory) getReflectedModelFactory();
				return factory.makeNewFlexoConceptInstance(concept, newRow.getRow(), this, this, null, null);
			} catch (FMLExecutionException e) {
				logger.warning("Could not instantiate " + concept + " on new row: " + e.getMessage());
				return null;
			}
		}

	}
}
