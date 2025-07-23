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

import java.util.Date;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.reflect.ReflectedFlexoConceptInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.FMLExcelModelSlot;
import org.openflexo.technologyadapter.excel.fml.reflect.XLSColumnRole;

/**
 * A Excel-specific {@link FlexoConceptInstance} reflecting a distant object (represented by a row in a workbook) accessible in an
 * {@link XLSVirtualModelInstance} through a {@link FMLExcelModelSlot}<br>
 * 
 */
@ModelEntity
@ImplementationClass(XLSFlexoConceptInstance.XLSFlexoConceptInstanceImpl.class)
@XMLElement
public interface XLSFlexoConceptInstance extends ReflectedFlexoConceptInstance<Row> {

	@Initializer
	void initialize(FlexoConcept concept);

	/**
	 * Default implementation for {@link XLSFlexoConceptInstance}
	 * 
	 * @author sylvain
	 *
	 */
	abstract class XLSFlexoConceptInstanceImpl extends FlexoConceptInstanceImpl implements XLSFlexoConceptInstance {

		private static final Logger logger = FlexoLogger.getLogger(XLSFlexoConceptInstance.class.getPackage().toString());

		@Override
		public XLSVirtualModelInstance getVirtualModelInstance() {
			return (XLSVirtualModelInstance) super.getVirtualModelInstance();
		}

		@Override
		public <T> T getFlexoActor(FlexoRole<T> flexoRole) {
			if (flexoRole instanceof XLSColumnRole) {
				XLSColumnRole<T> columnRole = (XLSColumnRole<T>) flexoRole;
				Cell cell = getSupportObject().getCell(columnRole.getColumnIndex());
				// System.out.println("cell: " + cell);
				switch (columnRole.getPrimitiveType()) {
					case String:
						if (cell != null) {
							return (T) cell.getStringCellValue();
						}
						return null;
					case Long:
					case Integer:
					case Double:
					case Float:
						if (cell != null) {
							return (T) TypeUtils.castTo(cell.getNumericCellValue(), columnRole.getPrimitiveType().getType());
						}
						else {
							return (T) TypeUtils.castTo(0, columnRole.getPrimitiveType().getType());
						}
					case Date:
						if (cell != null) {
							return (T) cell.getDateCellValue();
						}
					case Boolean:
						if (cell != null) {
							return (T) (Boolean) cell.getBooleanCellValue();
						}
						return (T) Boolean.FALSE;
					default:
						logger.warning("Unexpected primitive type: " + columnRole.getPrimitiveType());
						return null;
				}
			}
			return super.getFlexoActor(flexoRole);
		}

		@Override
		public <T> void setFlexoActor(T object, FlexoRole<T> flexoRole) {
			if (flexoRole instanceof XLSColumnRole) {
				XLSColumnRole<T> columnRole = (XLSColumnRole<T>) flexoRole;
				Cell cell = getSupportObject().getCell(columnRole.getColumnIndex());
				// System.out.println("cell: " + cell);
				switch (columnRole.getPrimitiveType()) {
					case String:
						cell.setCellValue((String) object);
						break;
					case Long:
						cell.setCellValue((Long) object);
						break;
					case Integer:
						cell.setCellValue((Integer) object);
						break;
					case Double:
						cell.setCellValue((Double) object);
						break;
					case Float:
						cell.setCellValue((Float) object);
						break;
					case Date:
						cell.setCellValue((Date) object);
						break;
					case Boolean:
						cell.setCellValue((Boolean) object);
						break;
					default:
						logger.warning("Unexpected primitive type: " + columnRole.getPrimitiveType());
						break;
				}
			}
			else {
				super.setFlexoActor(object, flexoRole);
			}
		}

		@Override
		public XLSObjectActorReference makeActorReference(FlexoConceptInstanceRole role, FlexoConceptInstance fci) {
			AbstractVirtualModelInstanceModelFactory<?> factory = getFactory();
			XLSObjectActorReference returned = factory.newInstance(XLSObjectActorReference.class);
			returned.setFlexoRole(role);
			returned.setFlexoConceptInstance(fci);
			returned.setModellingElement(this);
			return returned;
		}

	}
}
