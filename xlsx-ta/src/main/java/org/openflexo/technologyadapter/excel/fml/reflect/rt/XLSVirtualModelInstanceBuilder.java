/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Xmlconnector, a component of the software infrastructure 
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


package org.openflexo.technologyadapter.excel.fml.reflect.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.md.FMLMetaData;
import org.openflexo.foundation.fml.md.MultiValuedMetaData;
import org.openflexo.foundation.fml.md.SingleMetaData;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.visitor.FlexoConceptVisitor;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelRow;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;
import org.openflexo.toolbox.StringUtils;

/**
 * A builder for a {@link XLSVirtualModelInstance}<br>
 *
 * This builder reflects an excel workbook (the reflected resource) as a {@link XLSVirtualModelInstance} conform to a given
 * {@link VirtualModel} (the reflected virtual model).<br>
 *
 * Mapping between the workbook and the {@link VirtualModel} is expressed using FML meta-data:
 * <ul>
 * <li>a {@link FlexoConcept} declaring a <code>@DataRange(sheet="Sheet1",rows="1,*",cols="0,4")</code> meta-data is instantiated for each
 * significative row of matching area (a <code>*</code> value indicates that the area is not bounded, and is then computed from the contents
 * of the workbook)</li>
 * <li>a {@link FlexoProperty} declaring a <code>@Property(col="2")</code> meta-data is bound to matching cell of the row supporting the
 * {@link XLSFlexoConceptInstance} (see {@link XLSFlexoConceptInstance#getFlexoActor(org.openflexo.foundation.fml.FlexoRole)})</li>
 * </ul>
 *
 * @author sylvain
 */
public class XLSVirtualModelInstanceBuilder {

	private static final Logger logger = Logger.getLogger(XLSVirtualModelInstanceBuilder.class.getPackage().getName());

	private static final String DATA_RANGE = "DataRange";
	private static final String SHEET = "sheet";
	private static final String ROWS = "rows";
	private static final String COLS = "cols";
	static final String PROPERTY = "Property";
	static final String COL = "col";

	/** Value used in a <code>@DataRange</code> meta-data to indicate an unbounded start or end index */
	private static final String UNBOUNDED = "*";

	private final XLSVirtualModelInstanceModelFactory factory;
	private final VirtualModel reflectedVM;

	private final List<DataRange> dataRanges;

	private final ExcelWorkbookResource reflectedResource;

	public XLSVirtualModelInstanceBuilder(XLSVirtualModelInstanceModelFactory factory, VirtualModel reflectedVM) {
		this.reflectedVM = reflectedVM;
		this.factory = factory;

		reflectedResource = factory.getReflectedResource();

		dataRanges = new ArrayList<>();

		reflectedVM.accept(new FlexoConceptVisitor() {

			@Override
			public void visitVirtualModel(VirtualModel virtualModel) {
				// System.out.println("visitVirtualModel: " + virtualModel);
				registerConcept(virtualModel);
			}

			@Override
			public void visitFlexoConcept(FlexoConcept flexoConcept) {
				// System.out.println("visitFlexoConcept: " + flexoConcept);
				registerConcept(flexoConcept);
			}
		});

	}

	public VirtualModel getReflectedVirtualModel() {
		return reflectedVM;
	}

	public ExcelWorkbookResource getReflectedResource() {
		return reflectedResource;
	}

	/**
	 * Return the list of {@link DataRange} which were found in reflected {@link VirtualModel}
	 */
	public List<DataRange> getDataRanges() {
		return Collections.unmodifiableList(dataRanges);
	}

	/**
	 * Register supplied {@link FlexoConcept} when this concept declares a <code>@DataRange</code> meta-data
	 */
	private void registerConcept(FlexoConcept concept) {
		FMLMetaData md = concept.getMetaData(DATA_RANGE);
		if (md instanceof MultiValuedMetaData) {
			MultiValuedMetaData metaData = (MultiValuedMetaData) md;
			String sheetName = metaData.getValue(SHEET, String.class);
			String rows = metaData.getValue(ROWS, String.class);
			String cols = metaData.getValue(COLS, String.class);
			if (StringUtils.isEmpty(sheetName)) {
				logger.warning("Ignoring @" + DATA_RANGE + " for " + concept + " : no " + SHEET + " declared");
				return;
			}
			try {
				dataRanges.add(new DataRange(concept, sheetName, rows, cols));
			} catch (XLSMappingException e) {
				logger.warning("Ignoring @" + DATA_RANGE + " for " + concept + " : " + e.getMessage());
			}
		}
		else if (md != null) {
			logger.warning("Unexpected @" + DATA_RANGE + " meta-data " + md + " for " + concept);
		}
	}

	/**
	 * Build supplied {@link XLSVirtualModelInstance} by reflecting the excel workbook: a {@link XLSFlexoConceptInstance} is created for each
	 * row of each {@link DataRange} declared in reflected {@link VirtualModel}
	 *
	 * @param vmi
	 *            the {@link XLSVirtualModelInstance} to build
	 * @return supplied {@link XLSVirtualModelInstance}
	 */
	public XLSVirtualModelInstance buildVirtualModelInstance(XLSVirtualModelInstance vmi) {

		vmi.setVirtualModel(reflectedVM);
		vmi.setReflectedResource(reflectedResource);

		ExcelWorkbook workbook = (reflectedResource != null ? reflectedResource.getExcelWorkbook() : null);
		if (workbook == null) {
			logger.warning("Could not access excel workbook for " + reflectedResource);
			return vmi;
		}

		for (DataRange dataRange : dataRanges) {
			dataRange.buildFlexoConceptInstances(vmi, workbook);
		}

		return vmi;
	}

	/**
	 * Internal representation of a <code>@DataRange(sheet=...,rows=...,cols=...)</code> meta-data declared for a {@link FlexoConcept}
	 */
	public class DataRange {

		private final FlexoConcept concept;
		private final String sheetName;
		private final int startRowIndex;
		// -1 when unbounded : end of area is then computed from workbook contents
		private final int endRowIndex;
		private final int startColumnIndex;
		private final int endColumnIndex;

		DataRange(FlexoConcept concept, String sheetName, String rows, String cols) throws XLSMappingException {
			this.concept = concept;
			this.sheetName = sheetName;
			this.startRowIndex = parseBound(rows, 0, 0);
			this.endRowIndex = parseBound(rows, 1, -1);
			this.startColumnIndex = parseBound(cols, 0, 0);
			this.endColumnIndex = parseBound(cols, 1, -1);
		}

		public FlexoConcept getFlexoConcept() {
			return concept;
		}

		public String getSheetName() {
			return sheetName;
		}

		public int getStartRowIndex() {
			return startRowIndex;
		}

		/**
		 * Return index of last row of this area, or -1 when this area is unbounded
		 */
		public int getEndRowIndex() {
			return endRowIndex;
		}

		public int getStartColumnIndex() {
			return startColumnIndex;
		}

		/**
		 * Return index of last column of this area, or -1 when this area is unbounded
		 */
		public int getEndColumnIndex() {
			return endColumnIndex;
		}

		/**
		 * Instantiate a {@link XLSFlexoConceptInstance} for each significative row of this area
		 */
		private void buildFlexoConceptInstances(XLSVirtualModelInstance vmi, ExcelWorkbook workbook) {

			ExcelSheet sheet = workbook.getExcelSheetByName(sheetName);
			if (sheet == null) {
				logger.warning("Could not find sheet " + sheetName + " in " + workbook + " while reflecting " + concept);
				return;
			}

			for (int rowIndex = startRowIndex; isSignificative(rowIndex, sheet); rowIndex++) {
				ExcelRow excelRow = sheet.getRowAt(rowIndex);
				try {
					factory.makeNewFlexoConceptInstance(concept, excelRow.getRow(), vmi, vmi, null, null);
					// System.out.println("Built " + concept + " for row " + rowIndex);
				} catch (FMLExecutionException e) {
					logger.warning("Could not instantiate " + concept + " for row " + rowIndex + " : " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		/**
		 * Return true when row identified by supplied index belongs to this area, and contains at least one non-empty cell in the columns of
		 * this area
		 */
		private boolean isSignificative(int rowIndex, ExcelSheet sheet) {

			if (endRowIndex >= 0 && rowIndex > endRowIndex) {
				return false;
			}
			if (rowIndex >= sheet.getExcelRows().size()) {
				// This row does not exist, do not create it
				return false;
			}

			ExcelRow row = sheet.getRowAt(rowIndex);
			if (row == null) {
				return false;
			}

			int lastColumnIndex = (endColumnIndex >= 0 ? endColumnIndex : row.getExcelCells().size() - 1);
			for (int columnIndex = startColumnIndex; columnIndex <= lastColumnIndex; columnIndex++) {
				ExcelCell cell = row.getExcelCellAt(columnIndex);
				if (cell != null && StringUtils.isNotEmpty(cell.getCellValueAsString())) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "@" + DATA_RANGE + "(" + SHEET + "=\"" + sheetName + "\"," + ROWS + "=\"" + startRowIndex + ","
					+ (endRowIndex >= 0 ? endRowIndex : UNBOUNDED) + "\"," + COLS + "=\"" + startColumnIndex + ","
					+ (endColumnIndex >= 0 ? endColumnIndex : UNBOUNDED) + "\") for " + concept;
		}
	}

	/**
	 * Parse bound at supplied position in a <code>"start,end"</code> meta-data value
	 *
	 * @param value
	 *            value to parse (eg <code>"1,*"</code>)
	 * @param position
	 *            0 for start bound, 1 for end bound
	 * @param defaultValue
	 *            value to be returned when bound is not declared, or declared as unbounded (<code>*</code>)
	 */
	private static int parseBound(String value, int position, int defaultValue) throws XLSMappingException {

		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}

		String[] bounds = value.split(",");
		if (position >= bounds.length) {
			return defaultValue;
		}

		String bound = bounds[position].trim();
		if (bound.length() == 0 || UNBOUNDED.equals(bound)) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(bound);
		} catch (NumberFormatException e) {
			throw new XLSMappingException("Cannot parse bound '" + bound + "' in '" + value + "'");
		}
	}

	/**
	 * Return the name of the sheet supporting supplied {@link FlexoConcept}, as it is declared using its
	 * <code>@DataRange(sheet="...")</code> meta-data, or null when this concept is not bound to a sheet.<br>
	 * Used to locate the target sheet when explicitly adding a new reflected instance (a new row).
	 */
	public static String getSheetName(FlexoConcept concept) {
		if (concept == null) {
			return null;
		}
		FMLMetaData md = concept.getMetaData(DATA_RANGE);
		if (md instanceof MultiValuedMetaData) {
			return ((MultiValuedMetaData) md).getValue(SHEET, String.class);
		}
		return null;
	}

	/**
	 * Return column index to be used to access value of supplied {@link FlexoProperty}, as it is declared using a
	 * <code>@Property(col="2")</code> meta-data, or null when this property is not bound to a column
	 */
	public static Integer getColumnIndex(FlexoProperty<?> property) {

		if (property == null) {
			return null;
		}

		FMLMetaData md = property.getMetaData(PROPERTY);
		String col = null;
		if (md instanceof MultiValuedMetaData) {
			col = ((MultiValuedMetaData) md).getValue(COL, String.class);
		}
		else if (md instanceof SingleMetaData) {
			col = ((SingleMetaData<String>) md).getValue(String.class);
		}

		if (StringUtils.isEmpty(col)) {
			return null;
		}

		try {
			return Integer.parseInt(col.trim());
		} catch (NumberFormatException e) {
			logger.warning("Cannot parse column index '" + col + "' declared for " + property);
			return null;
		}
	}

}
