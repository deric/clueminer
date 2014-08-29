/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.validation.api.ui;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ValidatorUtils;
import org.openide.util.Lookup;

/**
 * Factory for listeners on a particular type of component with a particular
 * type of model.  Creates listeners which can be attached to <i>one</i>
 * component and run validators for the specified model type.
 * For example, a validator for the Document of a JTextArea would most likely
 * be a Validator&lt;JTextComponent, Document&gt;.
 * <p/>
 * Instances of ValidationListenerFactory for specific component types may
 * be declaratively registered in META-INF/services in your JAR (i.e.
 * findable via JDK 6's ServiceLoader or NetBeans default Lookup).
 * <p/>
 * The library contains built-in listeners for Swing components, including
 * JList, JTextComponent, JComboBox and arrays of AbstractButton.  These
 * will be found in preference to any registered factories.
 *
 * @author Tim Boudreau
 */
public abstract class ValidationListenerFactory<CompType, ModelType> {
    private final Class<CompType> componentType;
    private final Class<ModelType> modelType;
    protected ValidationListenerFactory(Class<CompType> componentType, Class<ModelType> modelType) {
        this.componentType = componentType;
        this.modelType = modelType;
    }

    final Class<CompType> componentType() {
        return componentType;
    }

    final Class<ModelType> modelType() {
        return modelType;
    }

    /**
     * Create a ValidationListener for a specific type of component, which accepts
     * Validators of a given type.  By default, the following combinations are
     * supported:
     * <ul>
     * <li><code>JList + Integer[]</code></li>
     * <li><code>JList[] + ListSelectionModel</code></li>
     * <li><code>JTextComponent + Document</code></li>
     * <li><code>JTextComponent + String</code></li>
     * <li><code>JComboBox + ComboBoxModel</code></li>
     * <li><code>JComboBox + String</code></li>
     * <li><code>AbstractButton[] + ButtonModel[]</code></li>
     * <li><code>AbstractButton[] + Integer</code></li>
     * </ul>
     * To register validation listeners for additional types, subclass
     * SwingValidationListenerFactory<YourComponentType> and register it
     * in JDK 6's ServiceLoader/NetBeans default Lookup by placing flat
     * files in META-INF/services in a JAR on the classpath.  Each such
     * registered type <b>must</b> also have an annotation of type
     * <code>ListenerFor</code>, so that it can be located correctly.
     * Example:<pre>
&#64;SwingValidationListenerFactory.ListenerFor(componentType=JColorChooser.class, modelObjectType=Color.class)
public class ColorChooserValidationListenerFactory extends SwingValidationListenerFactory<JColorChooser> {

    &#64;Override
    protected <ModelType> ValidationListener<JColorChooser> createListener(
        JColorChooser component, ValidationStrategy strategy,
        ValidationUI validationUI, Validator<ModelType> validator) {
        //This cast is safe - this method will never be called unless
        //the type has first been checked
        return new ColorChooserListener(component, validationUI,
            (Validator<Color>) validator);
    }

    static final class ColorChooserListener
                    extends AbstractValidationListener<JColorChooser, Color>
                    implements PropertyChangeListener {
        ColorChooserListener (JColorChooser comp, ValidationUI ui,
                              Validator<Color> validator) {
            super (comp, ui, color);
            comp.addPropertyChangeListener("color", this);
        }

        &#64;Override
        protected Color getModelObject(JColorChooser comp) {
            return comp.getColor();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            performValidation();
        }
    }
}</pre>
     *
     * @param <CType>  The component type.
     * @param <MType> The type of object the Validator accepts
     * @param component A component of CType or a subclass
     * @param strategy The validation strategy to use
     * @param validationUI A validation UI, typically the component decorator
     * @param validator A validator that accepts ModelType arguments
     * @return a listener
     */
    public static <CType, MType> ValidationListener<CType> createValidationListener(final CType component, final ValidationStrategy strategy, ValidationUI validationUI, final Validator<MType> validator) {
        Class<MType> modelType = validator.modelType();
        ValidationListener<CType> result = null;
        //If we're really using some other toolkit, avoid loading more Swing
        //classes than JComponent
        if (component instanceof JComponent || (component.getClass().isArray() && JComponent.class.isAssignableFrom(component.getClass().getComponentType()))) {
            result = findBuiltInValidationListener(component, strategy, validationUI, validator);
        }
        if (result == null) {
            @SuppressWarnings("rawtypes")
            Collection <? extends ValidationListenerFactory> registered = Lookup.getDefault().lookupAll(ValidationListenerFactory.class);
            if (Object.class.equals(modelType)) {
                Logger.getLogger(ValidationListenerFactory.class.getName()).log(
                        Level.WARNING,
                        "Bad form to create a Validator<Object>: {0}",
                        validator.getClass().getName());
            }
            @SuppressWarnings("unchecked") // XXX this code is probably wrong, but have not yet figured out why; probably should accept Class<CType> param
            Class<CType> compType = (Class<CType>) component.getClass();
            for (ValidationListenerFactory<?,?> f : registered) {
                if (f.componentType().isAssignableFrom(compType)) {
                    ValidationListenerFactory<CType, MType> cast = f.<CType,MType>as(compType, modelType, component);
                    if (cast != null) {
                        return cast.createListener(component, strategy, validationUI, validator);
                    }
                }
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("No registered validator for " +
                "components of type " + component.getClass().getName() +
                " and validators for objects of type " + modelType);
        }
        return result;
    }

    // XXX unchecked warnings: need cast method for ValidationListener
    @SuppressWarnings("unchecked")
    static <CType, MType> ValidationListener<CType> findBuiltInValidationListener(final CType component, final ValidationStrategy strategy, ValidationUI validationUI, final Validator<MType> validator) {
        Class<MType> modelType = validator.modelType();
        if (component instanceof JList && Integer[].class.equals(modelType)) {
            return (ValidationListener<CType>) SwingValidationListenerFactories.createJListValidationListenerConverted((JList) component, strategy, validationUI, ValidatorUtils.cast(Integer[].class, validator));
        } else if (component instanceof JList && ListSelectionModel.class.isAssignableFrom(modelType)) {
            return SwingValidationListenerFactories.createJListValidationListener(JList.class.cast(component), strategy, validationUI, (Validator<ListSelectionModel>) validator);
        } else if (component instanceof JTextComponent && String.class.equals(modelType)) {
            return SwingValidationListenerFactories.createJTextComponentValidationListener(JTextComponent.class.cast(component), strategy, validationUI, (Validator<String>) validator);
        } else if (component instanceof JTextComponent && Document.class.isAssignableFrom(modelType)) {
            return SwingValidationListenerFactories.createJTextComponentValidationListener(JTextComponent.class.cast(component), strategy, validationUI, (Validator<Document>) validator);
        } else if (component instanceof JComboBox && String.class.equals(modelType)) {
            return SwingValidationListenerFactories.createJComboBoxValidationListener(JComboBox.class.cast(component), strategy, validationUI, (Validator<String>) validator);
        } else if (component instanceof JComboBox && ComboBoxModel.class.isAssignableFrom(modelType)) {
            return SwingValidationListenerFactories.createJComboBoxValidationListener(JComboBox.class.cast(component), strategy, validationUI, (Validator<ComboBoxModel>) validator);
        } else if (ButtonModel[].class.equals(modelType) && component.getClass().isArray() && component.getClass().getComponentType().isAssignableFrom(AbstractButton.class)) {
            return SwingValidationListenerFactories.createButtonsValidationListener(AbstractButton[].class.cast(component), validationUI, (Validator<ButtonModel[]>) validator);
        } else if (Integer[].class.equals(modelType) && component.getClass().isArray() && component.getClass().getComponentType().isAssignableFrom(AbstractButton.class)) {
            return SwingValidationListenerFactories.createButtonsValidationListener(AbstractButton[].class.cast(component), validationUI, (Validator<Integer[]>) validator);
        }
        return null;
    }

    protected abstract ValidationListener<CompType> createListener (final CompType component, final ValidationStrategy strategy, ValidationUI validationUI, final Validator<ModelType> validator);

    <T, R> ValidationListenerFactory<T, R> as (Class<T> actualCompType, Class<R> actualModelType, T comp) {
        return new Cast<T, R, CompType, ModelType> (actualCompType, actualModelType, comp, this);
    }

    private static final class Cast<CompType, ModelType, T, R> extends ValidationListenerFactory<CompType, ModelType> {
        private final ValidationListenerFactory<T, R> other;
        Cast(Class<CompType> compType, Class<ModelType> model, CompType comp, ValidationListenerFactory<T, R> other) {
            super (compType, model);
            this.other = other;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected ValidationListener<CompType> createListener(CompType component, ValidationStrategy strategy, ValidationUI validationUI, Validator<ModelType> validator) {
            T t = other.componentType.cast(component);
            Validator<R> v = ValidatorUtils.cast(other.modelType(), validator);
            ValidationListener<?> real = other.createListener(t, strategy, validationUI, v);
            return (ValidationListener<CompType>) real;
        }
    }
}
