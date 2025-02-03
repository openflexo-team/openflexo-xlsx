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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.FMLExcelModelSlot;
import org.openflexo.technologyadapter.excel.fml.reflect.XLSDataAreaRole;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;
import org.openflexo.toolbox.StringUtils;

/**
 * A {@link VirtualModelInstance} reflecting XLS sheets objects accessible through a {@link FMLExcelModelSlot} configured with a
 * {@link VirtualModel}<br>
 * 
 */
@ModelEntity
@ImplementationClass(XLSVirtualModelInstance.SEVirtualModelInstanceImpl.class)
@Imports(@Import(XLSFlexoConceptInstance.class))
@XMLElement
public interface XLSVirtualModelInstance extends VirtualModelInstance<XLSVirtualModelInstance, ExcelTechnologyAdapter> {

	@PropertyIdentifier(type = ExcelWorkbookResource.class)
	String EXCEL_WORKBOOK_RESOURCE = "excelWorkbookResource";
	@PropertyIdentifier(type = String.class)
	String EXCEL_WORKBOOK_URI = "excelWorkbookURI";

	@Getter(EXCEL_WORKBOOK_RESOURCE)
	public ExcelWorkbookResource getExcelWorkbookResource();

	@Setter(EXCEL_WORKBOOK_RESOURCE)
	public void setExcelWorkbookResource(ExcelWorkbookResource excelWorkbookResource);

	@Getter(EXCEL_WORKBOOK_URI)
	@XMLAttribute
	public String getExcelWorkbookURI();

	@Setter(EXCEL_WORKBOOK_URI)
	public void setExcelWorkbookURI(String excelWorkbook);

	/**
	 * Retrieve (build if not existant) {@link XLSFlexoConceptInstance} with supplied support object (a row in an excel workbook), asserting
	 * returned {@link XLSFlexoConceptInstance} object has supplied concept type and container<br>
	 * 
	 * This {@link XLSVirtualModelInstance} has an internal caching scheme allowing to store {@link XLSFlexoConceptInstance} relatively to
	 * their related {@link FlexoConcept} (their type) and their identifier
	 * 
	 * @param row
	 *            row in excel workbook
	 * @param container
	 *            container (eventually null) of returned {@link XLSFlexoConceptInstance}
	 * @param concept
	 *            type of returned {@link XLSFlexoConceptInstance}
	 * @return
	 */
	// public XLSFlexoConceptInstance getFlexoConceptInstance(Row row, FlexoConceptInstance container, XLSDataAreaRole dataAreaRole)
	// throws XLSMappingException;

	/**
	 * Instantiate and register a new {@link XLSFlexoConceptInstance}
	 * 
	 * @param pattern
	 * @return
	 */
	// @Override
	// public XLSFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept);

	/**
	 * Instantiate and register a new {@link XLSFlexoConceptInstance} in a container FlexoConceptInstance
	 * 
	 * @param pattern
	 * @return
	 */
	// @Override
	// public XLSFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept, FlexoConceptInstance container);

	/**
	 * Update all data area by connecting to the excel workbook
	 */
	public void updateData() throws XLSMappingException;

	abstract class SEVirtualModelInstanceImpl extends VirtualModelInstanceImpl<XLSVirtualModelInstance, ExcelTechnologyAdapter>
			implements XLSVirtualModelInstance {

		private static final Logger logger = FlexoLogger.getLogger(XLSVirtualModelInstance.class.getPackage().toString());

		// Stores all FCIs related to their XLSDataAreaRole
		// private Map<FlexoConcept, List<XLSFlexoConceptInstance>> instances = new HashMap<>();
		// private Map<FlexoConcept, XLSDataArea<XLSFlexoConceptInstance>> instancesList = new HashMap<>();

		private Map<XLSDataAreaRole, XLSDataArea<XLSFlexoConceptInstance>> instances = new HashMap<>();

		private ExcelWorkbookResource wbResource;
		private String wbURI;

		@Override
		public ExcelWorkbookResource getExcelWorkbookResource() {
			if (wbResource == null && StringUtils.isNotEmpty(wbURI) && getServiceManager() != null
					&& getServiceManager().getResourceManager() != null) {
				wbResource = (ExcelWorkbookResource) getServiceManager().getResourceManager().getResource(wbURI, ExcelWorkbook.class);
				logger.info("Looked-up " + wbResource + " for " + wbURI);
			}
			return wbResource;
		}

		@Override
		public void setExcelWorkbookResource(ExcelWorkbookResource excelWorkbookResource) {
			this.wbResource = excelWorkbookResource;
		}

		@Override
		public String getExcelWorkbookURI() {
			if (wbResource != null) {
				return wbResource.getURI();
			}
			return wbURI;
		}

		@Override
		public void setExcelWorkbookURI(String excelWorkbookURI) {
			this.wbURI = excelWorkbookURI;
		}

		@Override
		public ExcelTechnologyAdapter getTechnologyAdapter() {
			if (getVirtualModelInstanceResource() != null) {
				return getVirtualModelInstanceResource().getTechnologyAdapter();
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<FlexoConceptInstance> getFlexoConceptInstances() {
			if (isSerializing()) {
				// FCI are not serialized
				return null;
			}
			return (List<FlexoConceptInstance>) performSuperGetter(FLEXO_CONCEPT_INSTANCES_KEY);
		}

		@Override
		public XLSVirtualModelInstanceModelFactory getFactory() {
			return (XLSVirtualModelInstanceModelFactory) super.getFactory();
		}

		@Override
		public <T> List<T> getFlexoActorList(FlexoRole<T> flexoRole) {
			if (flexoRole instanceof XLSDataAreaRole) {
				return (List<T>) instances.get(flexoRole);
			}
			return super.getFlexoActorList(flexoRole);
		}

		/**
		 * Retrieve (build if not existant) {@link XLSFlexoConceptInstance} with supplied support object (an excel row), asserting returned
		 * {@link XLSFlexoConceptInstance} object has supplied concept type and container<br>
		 * 
		 * This {@link XLSVirtualModelInstance} has an internal caching scheme allowing to store {@link XLSFlexoConceptInstance} relatively
		 * to their related {@link FlexoConcept} (their type) and their identifier
		 * 
		 * @param hbnMap
		 *            hibernate support object
		 * @param container
		 *            container (eventually null) of returned {@link XLSFlexoConceptInstance}
		 * @param concept
		 *            type of returned {@link XLSFlexoConceptInstance}
		 * @return
		 * @throws XLSMappingException
		 */
		/*@Override
		public XLSFlexoConceptInstance getFlexoConceptInstance(Row row, FlexoConceptInstance container, XLSDataAreaRole dataAreaRole)
				throws XLSMappingException {
		
			if (dataAreaRole == null) {
				logger.warning("Could not obtain XLSFlexoConceptInstance from null dataArea");
			}
			if (dataAreaRole.getFlexoConceptType() == null) {
				logger.warning("Could not obtain XLSFlexoConceptInstance from null FlexoConcept");
			}
		
			System.out.println("Nouvelle instance pour " + row.getRowNum());
		
			// String identifier = getIdentifier(row, concept);
			// System.out.println("Building object with: " + hbnMap + " id=" + identifier);
		
			//getFactory()
			
			List<XLSFlexoConceptInstance> mapForConcept = instances.computeIfAbsent(dataAreaRole.getFlexoConceptType(), (newConcept) -> {
				return new ArrayList<>();
			});
		
			XLSFlexoConceptInstance returned = mapForConcept.computeIfAbsent(row.getRowNum(), (newId) -> {
				return getFactory().newFlexoConceptInstance(this, container, row, dataAreaRole.getFlexoConceptType());
			});
		
			XLSDataArea<XLSFlexoConceptInstance> dataArea = instancesList.get(dataAreaRole.getFlexoConceptType());
			
			
			
			if (dataArea != null) {
				dataArea.add(returned);
			}
		
			return returned;
		}*/

		/**
		 * Instanciate and register a new {@link FlexoConceptInstance}
		 * 
		 * @param pattern
		 * @return
		 */
		/*@Override
		public XLSFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept) {
		
			return makeNewFlexoConceptInstance(concept, null);
		}*/

		/**
		 * Instantiate and register a new {@link FlexoConceptInstance} in a container FlexoConceptInstance
		 * 
		 * @param pattern
		 * @return
		 */
		/*@Override
		public XLSFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept, FlexoConceptInstance container) {
		
			XLSFlexoConceptInstance returned = getResource().getFactory().newInstance(XLSFlexoConceptInstance.class, concept);
		
			if (container != null) {
				container.addToEmbeddedFlexoConceptInstances(returned);
			}
			addToFlexoConceptInstances(returned);
			return returned;
		}*/

		@Override
		public XLSFlexoConceptInstance buildNewFlexoConceptInstance(FlexoConcept concept) {
			return getVirtualModelInstanceResource().getFactory().newInstance(XLSFlexoConceptInstance.class, concept);
		}

		@Override
		public void updateData() throws XLSMappingException {
			System.out.println("------------> Looking-up excel file: " + getExcelWorkbookResource() + " for " + getVirtualModel());
			List<XLSDataAreaRole> dataAreaRoles = getVirtualModel().getAccessibleProperties(XLSDataAreaRole.class);
			if (dataAreaRoles != null) {
				for (XLSDataAreaRole dataAreaRole : dataAreaRoles) {
					updateDataAreaRole(dataAreaRole);
				}
			}
		}

		private XLSDataArea<XLSFlexoConceptInstance> updateDataAreaRole(XLSDataAreaRole dataAreaRole) {

			XLSDataArea<XLSFlexoConceptInstance> returned = instances.get(dataAreaRole);

			if (returned == null) {
				returned = new XLSDataArea<>(dataAreaRole, this, null);
				instances.put(dataAreaRole, returned);
			}

			returned.update();

			return returned;

			/*Map<Integer, XLSFlexoConceptInstance> allFCI = instances.get(dataAreaRole);
			
			if (allFCI == null) {
				allFCI = new LinkedHashMap<>();
				instances.put(dataAreaRole.getFlexoConceptType(), allFCI);
			}
			ExcelCellRange matchingRange = getRange(dataAreaRole);
			// System.out.println("matchingRange=" + matchingRange);
			
			int startRowIndex = matchingRange.getTopLeftCell().getRowIndex();
			int endRowIndex = matchingRange.getBottomRightCell().getRowIndex();
			for (int currentIndex = startRowIndex; currentIndex <= endRowIndex; currentIndex++) {
				ExcelRow excelRow = matchingRange.getExcelSheet().getRowAt(currentIndex);
				int fciIndex = currentIndex - startRowIndex;
				if (currentIndex < allFCI.size()) {
					// Update existing instance using row
					XLSFlexoConceptInstance seFCI = allFCI.get(fciIndex);
					seFCI.setRowSupportObject(excelRow.getRow());
				}
				else {
					// New instance
					XLSFlexoConceptInstance seFCI = getFlexoConceptInstance(excelRow.getRow(), null, dataAreaRole);
					allFCI.put(excelRow.getRowIndex(), seFCI);
				}
			}
			// What about instances to be deleted
			for (Integer rowIndex : new ArrayList<>(allFCI.keySet())) {
				if (rowIndex < startRowIndex || rowIndex > endRowIndex) {
					XLSFlexoConceptInstance fciToRemove = allFCI.get(rowIndex);
					allFCI.remove(rowIndex);
					fciToRemove.delete();
				}
			}
			
			// Rebuilt the list of concept instances for this type
			XLSDataArea<XLSFlexoConceptInstance> newDataArea = new XLSDataArea<>(dataAreaRole, matchingRange, allFCI.values());
			instancesList.put(dataAreaRole.getFlexoConceptType(), newDataArea);
			
			return allFCI;*/
		}

	}
}
