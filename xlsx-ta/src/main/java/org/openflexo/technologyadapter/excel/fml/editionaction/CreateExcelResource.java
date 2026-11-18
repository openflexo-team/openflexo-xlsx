/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Excelconnector, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.excel.fml.editionaction;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.OpenflexoRuntimeException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.AbstractCreateResource;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rt.error.FMLTechnologyError;
import org.openflexo.foundation.fml.rt.error.FMLCancellation;
import org.openflexo.foundation.fml.rt.error.FMLInternalError;
import org.openflexo.foundation.fml.rt.error.FMLExecutionException;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResourceFactory;

/**
 * {@link EditionAction} used to create an empty Excel resource
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(CreateExcelResource.CreateExcelResourceImpl.class)
@FML("CreateExcelResource")
public interface CreateExcelResource extends AbstractCreateResource<BasicExcelModelSlot, ExcelWorkbook, ExcelTechnologyAdapter> {

	public static abstract class CreateExcelResourceImpl
			extends AbstractCreateResourceImpl<BasicExcelModelSlot, ExcelWorkbook, ExcelTechnologyAdapter> implements CreateExcelResource {

		private static final Logger logger = Logger.getLogger(CreateExcelResourceImpl.class.getPackage().getName());

		@Override
		public Type getAssignableType() {
			return ExcelWorkbook.class;
		}

		@Override
		public ExcelWorkbook execute(RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {

			ExcelTechnologyAdapter excelTA = getServiceManager().getTechnologyAdapterService()
					.getTechnologyAdapter(ExcelTechnologyAdapter.class);

			try {
				ExcelWorkbookResource newResource = createResource(excelTA, ExcelWorkbookResourceFactory.class, evaluationContext, ".xlsx",
						true);
				newResource.setIsModified();
				return newResource.getResourceData();
			} catch (FMLExecutionException e) {
				// Already typed (a null name or resource center...): rethrown unchanged (CORE-F-11)
				throw e;
			} catch (ModelDefinitionException e) {
				throw new FMLInternalError(e);
			} catch (ResourceLoadingCancelledException e) {
				throw new FMLCancellation("Loading of the new workbook was cancelled", e);
			} catch (FileNotFoundException | OpenflexoRuntimeException e) {
				throw new FMLTechnologyError("Cannot create the Excel workbook: " + e.getMessage(), e);
			}
		}
	}
}
