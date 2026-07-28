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
import org.openflexo.connie.type.PrimitiveType;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.PrimitiveRole;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.reflect.ReflectedFlexoConceptInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.FMLExcelModelSlot;

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
	void initialize(FlexoConcept concept, Row supportObject);

	/**
	 * Default implementation for {@link XLSFlexoConceptInstance}
	 * 
	 * @author sylvain
	 *
	 */
	abstract class XLSFlexoConceptInstanceImpl extends FlexoConceptInstanceImpl implements XLSFlexoConceptInstance {

		private static final Logger logger = FlexoLogger.getLogger(XLSFlexoConceptInstance.class.getPackage().toString());

		/**
		 * Initialize this {@link XLSFlexoConceptInstance} with supplied row support object, and explicit concept (type)
		 */
		@Override
		public void initialize(FlexoConcept concept, Row supportObject) {
			setFlexoConcept(concept);
			setSupportObject(supportObject);
		}

		@Override
		public XLSVirtualModelInstance getVirtualModelInstance() {
			return (XLSVirtualModelInstance) super.getVirtualModelInstance();
		}

		@Override
		public AbstractVirtualModelInstanceModelFactory getFactory() {
			if (getVirtualModelInstance() != null) {
				return getVirtualModelInstance().getFactory();
			}
			return super.getFactory();
		}

		/**
		 * Return index of column supporting values of supplied {@link FlexoRole}, or null when this role is not bound to a column<br>
		 *
		 * Binding to a column is either expressed by a {@link XLSColumnRole}, or by a <code>@Property(col="2")</code> meta-data declared on
		 * a {@link PrimitiveRole} (reflected {@link VirtualModel})
		 */
		private Integer getColumnIndex(FlexoRole<?> flexoRole) {
			if (flexoRole instanceof PrimitiveRole) {
				return XLSVirtualModelInstanceBuilder.getColumnIndex(flexoRole);
			}
			return null;
		}

		private PrimitiveType getPrimitiveType(FlexoRole<?> flexoRole) {
			if (flexoRole instanceof PrimitiveRole) {
				return ((PrimitiveRole<?>) flexoRole).getPrimitiveType();
			}
			return null;
		}

		@Override
		public <T> T getFlexoActor(FlexoRole<T> flexoRole) {
			Integer columnIndex = getColumnIndex(flexoRole);
			PrimitiveType primitiveType = getPrimitiveType(flexoRole);
			if (columnIndex != null && primitiveType != null && getSupportObject() != null) {
				Cell cell = getSupportObject().getCell(columnIndex);
				// System.out.println("cell: " + cell);
				switch (primitiveType) {
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
							return (T) TypeUtils.castTo(cell.getNumericCellValue(), primitiveType.getType());
						}
						else {
							return (T) TypeUtils.castTo(0, primitiveType.getType());
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
						logger.warning("Unexpected primitive type: " + primitiveType);
						return null;
				}
			}
			return super.getFlexoActor(flexoRole);
		}

		@Override
		public <T> void setFlexoActor(T object, FlexoRole<T> flexoRole) {
			Integer columnIndex = getColumnIndex(flexoRole);
			PrimitiveType primitiveType = getPrimitiveType(flexoRole);
			if (columnIndex != null && primitiveType != null && getSupportObject() != null) {
				Cell cell = getSupportObject().getCell(columnIndex);
				// System.out.println("cell: " + cell);
				switch (primitiveType) {
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
						logger.warning("Unexpected primitive type: " + primitiveType);
						break;
				}
			}
			else {
				super.setFlexoActor(object, flexoRole);
			}
		}

		@Override
		public XLSObjectActorReference makeActorReference(FlexoConceptInstanceRole role, FlexoConceptInstance fci) {
			AbstractVirtualModelInstanceModelFactory factory = getFactory();
			XLSObjectActorReference returned = factory.newInstance(XLSObjectActorReference.class);
			returned.setFlexoRole(role);
			returned.setFlexoConceptInstance(fci);
			returned.setModellingElement(this);
			return returned;
		}

	}
}
