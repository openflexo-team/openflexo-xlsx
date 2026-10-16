/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.technologyadapter.excel.fml.reflect.rt;

import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Row;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.ModelSlotInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * Implements {@link ActorReference} for {@link XLSFlexoConceptInstance}<br>
 * 
 * @author sylvain
 * 
 * @param <T>
 */
@ModelEntity
@ImplementationClass(XLSObjectActorReference.XLSObjectActorReferenceImpl.class)
@XMLElement
public interface XLSObjectActorReference extends ActorReference<XLSFlexoConceptInstance> {

	@PropertyIdentifier(type = String.class)
	String RESOURCE_URI_KEY = "resourceURI";
	@PropertyIdentifier(type = String.class)
	String FLEXO_CONCEPT_URI_KEY = "flexoConceptURI";
	@PropertyIdentifier(type = String.class)
	String KEY_KEY = "key";

	@Getter(RESOURCE_URI_KEY)
	@XMLAttribute
	String getResourceURI();

	@Setter(RESOURCE_URI_KEY)
	void setResourceURI(String resourceURI);

	@Getter(FLEXO_CONCEPT_URI_KEY)
	@XMLAttribute
	String getFlexoConceptURI();

	@Setter(FLEXO_CONCEPT_URI_KEY)
	void setFlexoConceptURI(String conceptURI);

	@Getter(KEY_KEY)
	@XMLAttribute
	String getKey();

	@Setter(KEY_KEY)
	void setKey(String key);

	abstract class XLSObjectActorReferenceImpl extends ActorReferenceImpl<XLSFlexoConceptInstance> implements XLSObjectActorReference {

		private static final Logger logger = FlexoLogger.getLogger(XLSObjectActorReference.class.getPackage().toString());

		// private boolean isLoading = false;

		private XLSFlexoConceptInstance modellingElement;

		@Override
		public void setModellingElement(XLSFlexoConceptInstance object) {
			this.modellingElement = object;
			if (object != null) {
				setResourceURI(object.getVirtualModelInstance().getURI());
				setFlexoConceptURI(object.getFlexoConcept().getURI());
				setKey("" + object.getSupportObject().getRowNum());
			}
			else {
				setResourceURI(null);
				setKey(null);
			}
		}

		@Override
		public XLSFlexoConceptInstance getModellingElement(boolean forceLoading) {
			// TODO: instantiate cache when retrieving fails and return null value
			// Otherwise, this will continuously loop
			if (modellingElement == null && forceLoading) {
				modellingElement = retrieveModellingElement();
			}
			return modellingElement;
		}

		/**
		 * Retrieve the referenced {@link XLSFlexoConceptInstance} from what was serialized: the concept URI and the key, which is the
		 * 0-based row number of the workbook row backing the instance (see {@link #setModellingElement(XLSFlexoConceptInstance)}).
		 *
		 * The reflected instances are rebuilt from the workbook every time the reflected VirtualModelInstance is (re)built, so they are
		 * NOT the same objects as before serialization - only the row they sit on is stable, and that is what identifies them here.
		 */
		protected XLSFlexoConceptInstance retrieveModellingElement() {

			if (getFlexoConceptURI() == null || getKey() == null) {
				return null;
			}

			XLSVirtualModelInstance reflectedVMI = retrieveReflectedVirtualModelInstance();
			if (reflectedVMI == null) {
				logger.warning("Could not find the reflected VirtualModelInstance holding " + getFlexoConceptURI());
				return null;
			}

			int rowNum;
			try {
				rowNum = Integer.parseInt(getKey());
			} catch (NumberFormatException e) {
				logger.warning("Unexpected non-numeric key '" + getKey() + "' for " + getFlexoConceptURI());
				return null;
			}

			for (FlexoConceptInstance fci : reflectedVMI.getFlexoConceptInstances(getFlexoConceptURI())) {
				if (fci instanceof XLSFlexoConceptInstance) {
					Row row = ((XLSFlexoConceptInstance) fci).getSupportObject();
					if (row != null && row.getRowNum() == rowNum) {
						return (XLSFlexoConceptInstance) fci;
					}
				}
			}

			logger.warning("Could not retrieve " + getFlexoConceptURI() + " backed by row " + rowNum + " in " + reflectedVMI);
			return null;
		}

		/**
		 * Locate the reflected {@link XLSVirtualModelInstance} the referenced instance lives in.
		 *
		 * The serialized resourceURI cannot be used: a reflected VirtualModelInstance is a view built on the fly over the workbook and has
		 * no resource of its own, so its URI is null when the reference is written. The instance is therefore reached through the model
		 * slot that reflects the workbook - directly when the role declares one, otherwise by looking, among the model slots of the owning
		 * VirtualModelInstance, for the reflected contract that declares the referenced concept.
		 */
		private XLSVirtualModelInstance retrieveReflectedVirtualModelInstance() {

			ModelSlotInstance<?, ?> modelSlotInstance = getModelSlotInstance();
			if (modelSlotInstance != null && modelSlotInstance.getAccessedResourceData() instanceof XLSVirtualModelInstance) {
				return (XLSVirtualModelInstance) modelSlotInstance.getAccessedResourceData();
			}

			// The role declares no explicit container (no "virtualModelInstance=" on the ConceptInstance role),
			// so getModelSlotInstance() cannot infer the model slot: find it by the concept it declares.
			FlexoConceptInstance owner = getFlexoConceptInstance();
			VirtualModelInstance<?, ?> container = (owner != null ? owner.getVirtualModelInstance() : null);
			if (container == null) {
				return null;
			}
			for (ModelSlotInstance<?, ?> msInstance : container.getModelSlotInstances()) {
				if (msInstance.getAccessedResourceData() instanceof XLSVirtualModelInstance) {
					XLSVirtualModelInstance candidate = (XLSVirtualModelInstance) msInstance.getAccessedResourceData();
					VirtualModel contract = candidate.getVirtualModel();
					if (contract != null && contract.getFlexoConcept(getFlexoConceptURI()) != null) {
						return candidate;
					}
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return "XLSObjectActorReference [" + getRoleName() + "] " + Integer.toHexString(hashCode()) + " references "
					+ getModellingElement() + "[resource: " + getResourceURI() + " key:" + getKey() + "]";
		}
	}
}
