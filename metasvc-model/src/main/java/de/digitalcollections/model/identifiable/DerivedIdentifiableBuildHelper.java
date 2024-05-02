package de.digitalcollections.model.identifiable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DerivedIdentifiableBuildHelper {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DerivedIdentifiableBuildHelper.class);

  private DerivedIdentifiableBuildHelper() {}

  /**
   * Build an instance of a class derived from {@code Identifiable} and set the properties
   * accordingly.
   *
   * <p>The properties are <b>not</b> deeply copied. Instead only references are assigned.
   *
   * @param <D> {@link Identifiable} extending class
   * @param identifiable original object
   * @param derivedClazz {@link Class} that will be instantiated and returned
   * @return an instance of {@code derivedClazz} with its properties set to {@code identifiable}
   */
  public static <D extends Identifiable> D build(Identifiable identifiable, Class<D> derivedClazz) {
    if (identifiable == null || derivedClazz == null) return null;
    try {
      D derivedInst = derivedClazz.getConstructor().newInstance();
      // collect all the public setters of the new instance
      List<Method> derivedInstSetters =
          Stream.of(derivedInst.getClass().getMethods())
              .filter(m -> m.getName().startsWith("set"))
              .collect(Collectors.toList());
      // go through all the public getters of the passed Identifiable...
      for (Method identifiableGetter : identifiable.getClass().getMethods()) {
        if (!identifiableGetter.getName().startsWith("get")) continue;
        Type returnType = identifiableGetter.getGenericReturnType();
        // ...find the corresponding setter of the new object...
        Method[] setters =
            derivedInstSetters.stream()
                .filter(
                    derivSetter ->
                        derivSetter
                                .getName()
                                .equals(identifiableGetter.getName().replaceFirst("^get", "set"))
                            && derivSetter.getParameterCount() == 1
                            && derivSetter
                                .getParameters()[0]
                                .getParameterizedType()
                                .equals(returnType))
                .toArray(i -> new Method[i]);
        if (setters.length != 1) continue;
        // ...and invoke this setter with the getter's returned value
        setters[0].invoke(derivedInst, identifiableGetter.invoke(identifiable));
      }
      return derivedInst;
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException e) {
      LOGGER.error(
          "Error while building the derived Identifiable instance, reflection threw an exception",
          e);
      return null;
    }
  }
}
