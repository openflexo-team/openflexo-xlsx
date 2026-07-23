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

/*
 * (c) Copyright 2013- Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.excel;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.reflect.ReflectedFMLRTModelSlot;
import org.openflexo.foundation.fml.rt.reflect.ReflectedFMLRTModelSlotInstance;
import org.openflexo.foundation.resource.StreamIODelegate;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.excel.fml.reflect.rt.XLSVirtualModelInstance;
import org.openflexo.technologyadapter.excel.fml.reflect.rt.XLSVirtualModelInstanceBuilder;
import org.openflexo.technologyadapter.excel.fml.reflect.rt.XLSVirtualModelInstanceModelFactory;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;

/**
 * An implementation of a {@link ModelSlot} providing basic access to a set of data stored in an excel workbook, and reflected as FML
 * instances objects<br>
 * 
 * This {@link ModelSlot} is contract-based, as it is configured with a {@link VirtualModel} modelling data beeing accessed through this
 * {@link ModelSlot}. It means that data stored in database is locally reflected as {@link FlexoConceptInstance}s in a
 * {@link VirtualModelInstance} (instance of contract {@link VirtualModel})
 * 
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(FMLExcelModelSlot.FMLExcelModelSlotImpl.class)
// TODO : it would be nice to inherits from super declaration
@DeclareActorReferences({ ReflectedFMLRTModelSlotInstance.class })
@FML("FMLExcelModelSlot")
public interface FMLExcelModelSlot
		extends ReflectedFMLRTModelSlot<XLSVirtualModelInstance, ExcelWorkbookResource, ExcelWorkbook, ExcelTechnologyAdapter> {

	abstract class FMLExcelModelSlotImpl
			extends ReflectedFMLRTModelSlotImpl<XLSVirtualModelInstance, ExcelWorkbookResource, ExcelWorkbook, ExcelTechnologyAdapter>
			implements FMLExcelModelSlot {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(FMLExcelModelSlotImpl.class.getPackage().getName());

		@Override
		public Class<ExcelTechnologyAdapter> getTechnologyAdapterClass() {
			return ExcelTechnologyAdapter.class;
		}

		@Override
		public ExcelTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (ExcelTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

		@Override
		public ReflectedFMLRTModelSlotInstance<XLSVirtualModelInstance, ExcelWorkbookResource, ExcelWorkbook, ExcelTechnologyAdapter> connectTo(
				ExcelWorkbookResource resource, FlexoConceptInstance context) {

			try {
				XLSVirtualModelInstanceModelFactory factory = new XLSVirtualModelInstanceModelFactory(resource,
						getServiceManager().getEditingContext(), getServiceManager().getTechnologyAdapterService());
				XLSVirtualModelInstance xmlVmi = factory.newInstance(XLSVirtualModelInstance.class);
				xmlVmi.setReflectedModelFactory(factory);

				System.out.println("Built VMI: " + xmlVmi);
				System.out.println("Factory: " + xmlVmi.getReflectedModelFactory());
				// System.out.println("Resource: " + xmlVmi.getReflectedModelFactory().getResource());
				System.out.println("VM: " + getAccessedVirtualModel());

				if (xmlVmi.getReflectedModelFactory().getReflectedResource() != null
						&& xmlVmi.getReflectedModelFactory().getReflectedResource().getIODelegate() instanceof StreamIODelegate) {

					XLSVirtualModelInstanceBuilder builder = new XLSVirtualModelInstanceBuilder(factory, getAccessedVirtualModel());
					builder.buildVirtualModelInstance(xmlVmi);
				}

				ReflectedFMLRTModelSlotInstance<XLSVirtualModelInstance, ExcelWorkbookResource, ExcelWorkbook, ExcelTechnologyAdapter> modelSlotInstance;
				modelSlotInstance = makeActorReference(xmlVmi, context);
				context.addToActors(modelSlotInstance);
				return modelSlotInstance;

			} catch (ModelDefinitionException e) {
				logger.warning("Unexpected ModelDefinitionException: " + e);
				e.printStackTrace();
				return null;
			}
		}

		/*@Override
		public ReflectedFMLRTModelSlotInstance<XMLVirtualModelInstance<RD>, XMLResource<RD, ?>, RD, XMLTechnologyAdapter> connectTo(
				XMLResource<RD, ?> resource, FlexoConceptInstance context) {
		
			try {
				XMLVirtualModelInstanceModelFactory<RD> factory = new XMLVirtualModelInstanceModelFactory<RD>(resource,
						getServiceManager().getEditingContext(), getServiceManager().getTechnologyAdapterService());
				XMLVirtualModelInstance<RD> xmlVmi = factory.newInstance(XMLVirtualModelInstance.class);
				xmlVmi.setReflectedModelFactory(factory);
		
				//System.out.println("Built VMI: " + xmlVmi);
				//System.out.println("Factory: " + xmlVmi.getReflectedModelFactory());
				//System.out.println("Resource: " + xmlVmi.getReflectedModelFactory().getResource());
				//System.out.println("VM: " + getAccessedVirtualModel());
		
				if (xmlVmi.getReflectedModelFactory().getReflectedResource() != null
						&& xmlVmi.getReflectedModelFactory().getReflectedResource().getIODelegate() instanceof StreamIODelegate) {
		
					FMLXMLModelBuilder builder = new FMLXMLModelBuilder(factory, getAccessedVirtualModel());
					builder.setModelContext(xmlVmi);
					builder.deserialize(
							((StreamIODelegate) xmlVmi.getReflectedModelFactory().getReflectedResource().getIODelegate()).getInputStream());
					builder.resetModelContext();
				}
		
				ReflectedFMLRTModelSlotInstance<XMLVirtualModelInstance<RD>, XMLResource<RD, ?>, RD, XMLTechnologyAdapter> modelSlotInstance;
				modelSlotInstance = makeActorReference(xmlVmi, context);
				context.addToActors(modelSlotInstance);
				return modelSlotInstance;
		
			} catch (ModelDefinitionException e) {
				logger.warning("Unexpected ModelDefinitionException: " + e);
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				logger.warning("Unexpected IOException: " + e);
				e.printStackTrace();
				return null;
			}
		
		}		*/

	}

}
