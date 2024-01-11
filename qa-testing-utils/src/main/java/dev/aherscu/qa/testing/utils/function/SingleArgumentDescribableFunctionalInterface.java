package dev.aherscu.qa.testing.utils.function;

import java.lang.invoke.*;

interface SingleArgumentDescribableFunctionalInterface extends
    SerializedLambdaResolvable {

    default String getArgumentDescription() {
        MethodType methodType = MethodType.fromMethodDescriptorString(
            asSerializedLambda().getInstantiatedMethodType(),
            getClass().getClassLoader());
        return methodType.parameterType(0).getSimpleName();
    }

}
