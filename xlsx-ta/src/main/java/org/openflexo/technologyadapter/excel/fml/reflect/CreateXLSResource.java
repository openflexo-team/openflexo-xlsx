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

package org.openflexo.technologyadapter.excel.fml.reflect;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.AbstractCreateResource;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.rt.action.CreationSchemeAction;
import org.openflexo.foundation.fml.rt.action.FlexoBehaviourAction;
import org.openflexo.foundation.fml.validation.BindingIsRequiredAndMustBeValid;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.validation.FixProposal;
import org.openflexo.pamela.validation.ValidationError;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationRule;
import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;
import org.openflexo.technologyadapter.excel.FMLExcelModelSlot;
import org.openflexo.technologyadapter.excel.fml.reflect.rm.XLSVirtualModelInstanceResource;
import org.openflexo.technologyadapter.excel.fml.reflect.rm.XLSVirtualModelInstanceResourceFactory;
import org.openflexo.technologyadapter.excel.fml.reflect.rt.XLSMappingException;
import org.openflexo.technologyadapter.excel.fml.reflect.rt.XLSVirtualModelInstance;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

/**
 * {@link EditionAction} used to create an empty {@link XLSVirtualModelInstance} resource
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(CreateXLSResource.CreateXLSResourceImpl.class)
@XMLElement
@FML("CreateXLSResource")
public interface CreateXLSResource extends AbstractCreateResource<FMLExcelModelSlot, XLSVirtualModelInstance, ExcelTechnologyAdapter> {

	@PropertyIdentifier(type = String.class)
	public static final String CREATION_SCHEME_URI_KEY = "creationSchemeURI";
	@PropertyIdentifier(type = CreationScheme.class)
	public static final String CREATION_SCHEME_KEY = "creationScheme";
	@PropertyIdentifier(type = VirtualModel.class)
	public static final String VIRTUAL_MODEL_KEY = "virtualModel";
	@PropertyIdentifier(type = CreateXLSResourceParameter.class, cardinality = Cardinality.LIST)
	public static final String PARAMETERS_KEY = "parameters";

	@PropertyIdentifier(type = DataBinding.class)
	String EXCEL_WORKBOOK = "excelWorkbook";

	public CompilationUnitResource getVirtualModelResource();

	public void setVirtualModelResource(CompilationUnitResource virtualModelResource);

	public VirtualModel getVirtualModel();

	public void setVirtualModel(VirtualModel virtualModel);

	@Getter(EXCEL_WORKBOOK)
	@XMLAttribute
	DataBinding<ExcelWorkbook> getExcelWorkbook();

	@Setter(EXCEL_WORKBOOK)
	void setExcelWorkbook(DataBinding<ExcelWorkbook> aConnection);

	@Getter(value = CREATION_SCHEME_URI_KEY)
	@XMLAttribute
	public String _getCreationSchemeURI();

	@Setter(CREATION_SCHEME_URI_KEY)
	public void _setCreationSchemeURI(String creationSchemeURI);

	public CreationScheme getCreationScheme();

	public void setCreationScheme(CreationScheme creationScheme);

	public List<CreationScheme> getAvailableCreationSchemes();

	@Getter(value = PARAMETERS_KEY, cardinality = Cardinality.LIST, inverse = CreateXLSResourceParameter.OWNER_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public List<CreateXLSResourceParameter> getParameters();

	@Setter(PARAMETERS_KEY)
	public void setParameters(List<CreateXLSResourceParameter> parameters);

	@Adder(PARAMETERS_KEY)
	public void addToParameters(CreateXLSResourceParameter aParameter);

	@Remover(PARAMETERS_KEY)
	public void removeFromParameters(CreateXLSResourceParameter aParameter);

	public abstract Class<XLSVirtualModelInstanceResourceFactory> getResourceFactoryClass();

	public abstract String getSuffix();

	abstract class CreateXLSResourceImpl extends
			AbstractCreateResourceImpl<FMLExcelModelSlot, XLSVirtualModelInstance, ExcelTechnologyAdapter> implements CreateXLSResource {

		private DataBinding<ExcelWorkbook> excelWorkbook;

		private VirtualModel virtualModel;
		private CompilationUnitResource virtualModelResource;

		private CreationScheme creationScheme;
		private String _creationSchemeURI;
		private List<CreateXLSResourceParameter> parameters = null;

		@Override
		public Type getAssignableType() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getInstanceType();
			}
			return XLSVirtualModelInstance.class;
		}

		@Override
		public CompilationUnitResource getVirtualModelResource() {
			if (virtualModelResource != null) {
				return virtualModelResource;
			}
			if (getVirtualModel() != null) {
				return getVirtualModel().getCompilationUnitResource();
			}
			return virtualModelResource;
		}

		@Override
		public void setVirtualModelResource(CompilationUnitResource virtualModelResource) {
			if ((virtualModelResource == null && getVirtualModelResource() != null)
					|| (virtualModelResource != null && !virtualModelResource.equals(getVirtualModelResource()))) {
				CompilationUnitResource oldValue = getVirtualModelResource();
				this.virtualModelResource = virtualModelResource;
				setVirtualModel(virtualModelResource != null ? virtualModelResource.getCompilationUnit().getVirtualModel() : null);
				getPropertyChangeSupport().firePropertyChange("virtualModelResource", oldValue, virtualModelResource);
			}
		}

		@Override
		public VirtualModel getVirtualModel() {
			if (getCreationScheme() != null) {
				return (VirtualModel) getCreationScheme().getFlexoConcept();
			}
			if (getAssignedFlexoProperty() instanceof FMLExcelModelSlot) {
				return ((FMLExcelModelSlot) getAssignedFlexoProperty()).getAccessedVirtualModel();
			}
			if (virtualModelResource != null) {
				return virtualModelResource.getCompilationUnit().getVirtualModel();
			}
			return virtualModel;
		}

		@Override
		public void setVirtualModel(VirtualModel aVirtualModel) {
			if (this.virtualModel != aVirtualModel) {
				VirtualModel oldValue = this.virtualModel;
				this.virtualModel = aVirtualModel;
				if (aVirtualModel != null) {
					this.virtualModelResource = aVirtualModel.getCompilationUnitResource();
					getPropertyChangeSupport().firePropertyChange("availableCreationSchemes", null, aVirtualModel.getCreationSchemes());
				}
				if (creationScheme == null || creationScheme.getFlexoConcept() != aVirtualModel) {
					if (aVirtualModel.getCreationSchemes().size() > 0) {
						setCreationScheme(aVirtualModel.getCreationSchemes().get(0));
					}
					else {
						setCreationScheme(null);
					}
				}
				getPropertyChangeSupport().firePropertyChange(CreateXLSResource.VIRTUAL_MODEL_KEY, oldValue, aVirtualModel);
				getPropertyChangeSupport().firePropertyChange("availableCreationSchemes", null, getAvailableCreationSchemes());
			}
		}

		@Override
		public String _getCreationSchemeURI() {
			if (getCreationScheme() != null) {
				return getCreationScheme().getURI();
			}
			return _creationSchemeURI;
		}

		@Override
		public void _setCreationSchemeURI(String uri) {
			if (getVirtualModelLibrary() != null) {
				creationScheme = (CreationScheme) getVirtualModelLibrary().getFlexoBehaviour(uri, true);
			}
			_creationSchemeURI = uri;
		}

		@Override
		public CreationScheme getCreationScheme() {

			if (creationScheme == null && _creationSchemeURI != null && getVirtualModelLibrary() != null) {
				creationScheme = (CreationScheme) getVirtualModelLibrary().getFlexoBehaviour(_creationSchemeURI, true);
				updateParameters();
			}
			if (creationScheme == null && ((FlexoProperty<?>) getAssignedFlexoProperty()) instanceof FlexoConceptInstanceRole) {
				creationScheme = ((FlexoConceptInstanceRole) (FlexoProperty<?>) getAssignedFlexoProperty()).getCreationScheme();
				updateParameters();
			}
			return creationScheme;
		}

		@Override
		public void setCreationScheme(CreationScheme creationScheme) {
			if (this.creationScheme != creationScheme) {
				CreationScheme oldValue = this.creationScheme;
				this.creationScheme = creationScheme;
				if (creationScheme != null) {
					_creationSchemeURI = creationScheme.getURI();
				}
				else {
					_creationSchemeURI = null;
				}
				updateParameters();
				getPropertyChangeSupport().firePropertyChange(CREATION_SCHEME_KEY, oldValue, creationScheme);
				getPropertyChangeSupport().firePropertyChange(VIRTUAL_MODEL_KEY, null, getVirtualModel());
			}
		}

		@Override
		public List<CreationScheme> getAvailableCreationSchemes() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getCreationSchemes();
			}
			return null;
		}

		@Override
		public List<CreateXLSResourceParameter> getParameters() {
			// Comment this because of an infinite loop with updateParameters() method
			if (parameters == null) {
				parameters = new ArrayList<>();
				updateParameters();
			}
			return parameters;
		}

		@Override
		public void setParameters(List<CreateXLSResourceParameter> parameters) {
			this.parameters = parameters;
		}

		@Override
		public void addToParameters(CreateXLSResourceParameter parameter) {
			parameter.setOwner(this);
			if (parameters == null) {
				parameters = new ArrayList<>();
			}
			parameters.add(parameter);
		}

		@Override
		public void removeFromParameters(CreateXLSResourceParameter parameter) {
			parameter.setOwner(null);
			if (parameters == null) {
				parameters = new ArrayList<>();
			}
			parameters.remove(parameter);
		}

		public CreateXLSResourceParameter getParameter(FlexoBehaviourParameter p) {
			for (CreateXLSResourceParameter addEPParam : getParameters()) {
				if (addEPParam.getParam() == p) {
					return addEPParam;
				}
			}
			return null;
		}

		private void updateParameters() {
			if (parameters == null) {
				parameters = new ArrayList<>();
			}
			List<CreateXLSResourceParameter> oldValue = new ArrayList<>(parameters);
			List<CreateXLSResourceParameter> parametersToRemove = new ArrayList<>(parameters);
			if (creationScheme != null) {
				for (FlexoBehaviourParameter p : creationScheme.getParameters()) {
					CreateXLSResourceParameter existingParam = getParameter(p);
					if (existingParam != null) {
						parametersToRemove.remove(existingParam);
					}
					else {
						if (getFMLModelFactory() != null) {
							CreateXLSResourceParameter newParam = getFMLModelFactory().newInstance(CreateXLSResourceParameter.class);
							newParam.setParam(p);
							addToParameters(newParam);
						}
					}
				}
			}
			for (CreateXLSResourceParameter removeThis : parametersToRemove) {
				removeFromParameters(removeThis);
			}
			getPropertyChangeSupport().firePropertyChange(PARAMETERS_KEY, oldValue, parameters);
		}

		@Override
		public XLSVirtualModelInstance execute(RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {

			if (getCreationScheme() == null) {
				throw new FMLExecutionException("No creation scheme defined");
			}

			try {
				String resourceName = getResourceName(evaluationContext);
				String resourceURI = getResourceURI(evaluationContext);
				FlexoResourceCenter<?> rc = getResourceCenter(evaluationContext);

				System.out.println("Creating XLSVirtualModelInstanceResource");

				XLSVirtualModelInstanceResource newResource = createResource(
						getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(ExcelTechnologyAdapter.class),
						getResourceFactoryClass(), evaluationContext, getSuffix(), true);
				XLSVirtualModelInstance data = newResource.getResourceData();
				data.setVirtualModel(getVirtualModel());

				FlexoProperty<XLSVirtualModelInstance> flexoProperty = getAssignedFlexoProperty();
				if (flexoProperty instanceof FMLExcelModelSlot) {
					// Unused FMLExcelModelSlot seModelSlot = (FMLExcelModelSlot) flexoProperty;
					try {
						if (getExcelWorkbook().isValid()) {
							ExcelWorkbook excelWorkbook = getExcelWorkbook().getBindingValue(evaluationContext);
							data.setExcelWorkbookResource(excelWorkbook.getResource());
							System.out.println("Setting excelWorkbook: " + excelWorkbook);
						}
						else {
							throw new FMLExecutionException("No valid connection while creating new SEResource");
						}

					} catch (TypeMismatchException | NullReferenceException | ReflectiveOperationException e) {
						e.printStackTrace();
					}

					// Now we should execute CreationScheme
					System.out.println("Executing FML: " + getCreationScheme().getFMLPrettyPrint());
					CreationSchemeAction creationSchemeAction = new CreationSchemeAction(getCreationScheme(), null, null,
							(FlexoBehaviourAction<?, ?, ?>) evaluationContext);
					creationSchemeAction.assignNewFlexoConceptInstance(data);
					for (CreateXLSResourceParameter p : getParameters()) {
						// Unused FlexoBehaviourParameter param = p.getParam();
						Object value = p.evaluateParameterValue((FlexoBehaviourAction<?, ?, ?>) evaluationContext);
						// System.out.println("For parameter " + param + " value is " + value);
						if (value != null) {
							creationSchemeAction.setParameterValue(p.getParam(),
									p.evaluateParameterValue((FlexoBehaviourAction<?, ?, ?>) evaluationContext));
						}
					}

					creationSchemeAction.doAction();

					data.updateData();

					if (data.getVirtualModel().getFlexoBehaviours(XLSInitializer.class).size() > 0) {
						XLSInitializer initializer = data.getVirtualModel().getFlexoBehaviours(XLSInitializer.class).get(0);
						XLSInitializerAction action = new XLSInitializerAction(initializer, data, null,
								(FlexoBehaviourAction<?, ?, ?>) evaluationContext);
						action.doAction();
					}

					// newResource.getFactory().initializeModel(data, getCreationScheme(), getParameters(), evaluationContext);

					/*data.setModelSlot(httpModelSlot);
					data.setOwnerInstance(evaluationContext.getVirtualModelInstance());
					newResource.getFactory().initializeModel(data, httpModelSlot.getCreationScheme(), httpModelSlot.getParameters(),
							evaluationContext);*/
				}
				else {
					throw new FMLExecutionException("SEResource creation must be affected to a SEModelSlot");
				}

				return data;
			} catch (ModelDefinitionException | FileNotFoundException | ResourceLoadingCancelledException e) {
				throw new FMLExecutionException(e);
			} catch (SaveResourceException e) {
				throw new FMLExecutionException(e);
			} catch (XLSMappingException e) {
				throw new FMLExecutionException(e);
			} catch (FlexoException e) {
				throw new FMLExecutionException(e);
			}

		}

		@Override
		public DataBinding<ExcelWorkbook> getExcelWorkbook() {
			if (excelWorkbook == null) {
				excelWorkbook = new DataBinding<>(this, ExcelWorkbook.class, DataBinding.BindingDefinitionType.GET);
				excelWorkbook.setBindingName("excelWorkbook");
			}
			return excelWorkbook;
		}

		@Override
		public void setExcelWorkbook(DataBinding<ExcelWorkbook> aWorkbook) {
			if (aWorkbook != null) {
				aWorkbook.setOwner(this);
				aWorkbook.setDeclaredType(ExcelWorkbook.class);
				aWorkbook.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				aWorkbook.setBindingName("excelWorkbook");
			}
			this.excelWorkbook = aWorkbook;
		}

		@Override
		public Class<XLSVirtualModelInstanceResourceFactory> getResourceFactoryClass() {
			return XLSVirtualModelInstanceResourceFactory.class;
		}

		@Override
		public String getSuffix() {
			return XLSVirtualModelInstanceResourceFactory.EXCEL_SE_SUFFIX;
		}

	}

	@DefineValidationRule
	public static class ExcelWorkbookIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<CreateXLSResource> {
		public ExcelWorkbookIsRequiredAndMustBeValid() {
			super("'excel_workbook'_binding_is_required_and_must_be_valid", CreateXLSResource.class);
		}

		@Override
		public DataBinding<ExcelWorkbook> getBinding(CreateXLSResource object) {
			return object.getExcelWorkbook();
		}

	}

	@DefineValidationRule
	public static class CreateSEResourceMustAddressAValidCreationScheme
			extends ValidationRule<CreateSEResourceMustAddressAValidCreationScheme, CreateXLSResource> {
		public CreateSEResourceMustAddressAValidCreationScheme() {
			super(CreateXLSResource.class, "create_semantics_excel_resource_must_address_a_valid_creation_scheme");
		}

		@Override
		public ValidationIssue<CreateSEResourceMustAddressAValidCreationScheme, CreateXLSResource> applyValidation(
				CreateXLSResource action) {
			if (action.getCreationScheme() == null) {
				Vector<FixProposal<CreateSEResourceMustAddressAValidCreationScheme, CreateXLSResource>> v = new Vector<>();
				if (action.getVirtualModel() != null) {
					for (CreationScheme cs : action.getVirtualModel().getCreationSchemes()) {
						v.add(new SetsCreationScheme(cs));
					}
				}
				return new ValidationError<>(this, action, "create_semantics_excel_resource_does_not_address_a_valid_creation_scheme", v);
			}
			return null;
		}

		protected static class SetsCreationScheme extends FixProposal<CreateSEResourceMustAddressAValidCreationScheme, CreateXLSResource> {

			private final CreationScheme creationScheme;

			public SetsCreationScheme(CreationScheme creationScheme) {
				super("sets_creation_scheme_to_($creationScheme.name)");
				this.creationScheme = creationScheme;
			}

			public CreationScheme getCreationScheme() {
				return creationScheme;
			}

			@Override
			protected void fixAction() {
				CreateXLSResource action = getValidable();
				action.setCreationScheme(getCreationScheme());
			}

		}
	}

}
