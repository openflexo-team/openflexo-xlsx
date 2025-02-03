/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

import org.openflexo.connie.type.CustomTypeFactory;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
import org.openflexo.foundation.fml.TechnologyAdapterTypeFactory;
import org.openflexo.technologyadapter.excel.fml.reflect.rt.XLSFlexoConceptInstance;

/**
 * Represent the type of an instance of a {@link XLSFlexoConcept}
 * 
 * @author sylvain
 * 
 */
//TODO : refactor !
@Deprecated // We should use "classical" FlexoConcept with specific annotations
public class XLSFlexoConceptInstanceType extends FlexoConceptInstanceType {

	public static XLSFlexoConceptInstanceType UNDEFINED_XLS_FLEXO_CONCEPT_INSTANCE_TYPE = new XLSFlexoConceptInstanceType(
			(XLSFlexoConcept) null);

	public XLSFlexoConceptInstanceType(XLSFlexoConcept aFlexoConcept) {
		super(aFlexoConcept);
	}

	public XLSFlexoConceptInstanceType(String conceptURI, CustomTypeFactory<?> factory) {
		super(conceptURI, factory);
	}

	@Override
	public Class<?> getBaseClass() {
		return XLSFlexoConceptInstance.class;
	}

	@Override
	public XLSFlexoConcept getFlexoConcept() {
		return (XLSFlexoConcept) super.getFlexoConcept();
	}

	public static XLSFlexoConceptInstanceType getFlexoConceptInstanceType(XLSFlexoConcept aSEFlexoConcept) {
		if (aSEFlexoConcept != null) {
			return aSEFlexoConcept.getInstanceType();
		}
		else {
			return UNDEFINED_XLS_FLEXO_CONCEPT_INSTANCE_TYPE;
		}
	}

	@Override
	public void resolve(CustomTypeFactory<?> factory) {
		if (factory instanceof SEFlexoConceptInstanceTypeFactory) {
			FlexoConcept concept = ((SEFlexoConceptInstanceTypeFactory) factory).getTechnologyAdapter().getTechnologyAdapterService()
					.getServiceManager().getVirtualModelLibrary().getFlexoConcept(conceptURI);
			if (concept != null) {
				flexoConcept = concept;
				this.customTypeFactory = null;
			}
			else {
				this.customTypeFactory = factory;
			}
		}
	}

	public static class SEFlexoConceptInstanceTypeFactory
			extends TechnologyAdapterTypeFactory<XLSFlexoConceptInstanceType, FMLTechnologyAdapter> {

		@Override
		public Class<XLSFlexoConceptInstanceType> getCustomType() {
			return XLSFlexoConceptInstanceType.class;
		}

		public SEFlexoConceptInstanceTypeFactory(FMLTechnologyAdapter technologyAdapter) {
			super(technologyAdapter);
		}

		@Override
		public XLSFlexoConceptInstanceType makeCustomType(String configuration) {

			XLSFlexoConcept concept = null;

			if (configuration != null) {
				concept = (XLSFlexoConcept) getTechnologyAdapter().getTechnologyAdapterService().getServiceManager().getVirtualModelLibrary()
						.getFlexoConcept(configuration, false);
				// Do not load virtual models for that reason, resolving will be performed later

			}
			else {
				concept = getFlexoConceptType();
			}

			if (concept != null) {
				return getFlexoConceptInstanceType(concept);
			}
			else {
				// We don't return UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE because we want here a mutable type
				// if FlexoConcept might be resolved later
				return new XLSFlexoConceptInstanceType(configuration, this);
			}
		}

		private XLSFlexoConcept flexoConceptType;

		public XLSFlexoConcept getFlexoConceptType() {
			return flexoConceptType;
		}

		public void setFlexoConceptType(XLSFlexoConcept flexoConceptType) {
			if (flexoConceptType != this.flexoConceptType) {
				FlexoConcept oldFlexoConceptType = this.flexoConceptType;
				this.flexoConceptType = flexoConceptType;
				getPropertyChangeSupport().firePropertyChange("flexoConceptType", oldFlexoConceptType, flexoConceptType);
			}
		}

		@Override
		public String toString() {
			return "Instance of XLSFlexoConcept";
		}

		@Override
		public void configureFactory(XLSFlexoConceptInstanceType type) {
			if (type != null) {
				setFlexoConceptType(type.getFlexoConcept());
			}
		}
	}

}
