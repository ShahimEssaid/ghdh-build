package com.essaid.google.harmonization.extensions.runtime;

import com.google.cloud.verticals.foundations.dataharmonization.function.CallableFunction;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContext;
import com.google.cloud.verticals.foundations.dataharmonization.init.Engine;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Utils {

  private static final Constructor<Engine> constructor;

  static {
    try {
      constructor = Engine.class.getDeclaredConstructor(
          RuntimeContext.class, CallableFunction.class);
      constructor.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }


  public static Engine newEngine(RuntimeContext context, CallableFunction mainFunction) {
    try {
      Engine engine = constructor.newInstance(context, mainFunction);
      return engine;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
