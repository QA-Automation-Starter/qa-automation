package dev.aherscu.qa.testing.utils.function;

import static dev.aherscu.qa.testing.utils.internal.DescriptionUtils.withPrefixedArticle;

import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.util.function.Function;

@FunctionalInterface
public interface DescribableFunction<T, R> extends Function<T, R>,
    SerializedLambdaResolvable, SingleArgumentDescribableFunctionalInterface {

    default String getResultDescription() {
        SerializedLambda lambda = asSerializedLambda();
        MethodType lambdaMethodType = MethodType.fromMethodDescriptorString(
            lambda.getImplMethodSignature(), getClass().getClassLoader());
        String resultType = lambdaMethodType.returnType().getSimpleName();
        if (!lambda.getImplMethodName().startsWith("lambda$")) {
            return lambda.getImplMethodName() + " ("
                + withPrefixedArticle(resultType) + ")";
        }
        return resultType;
    }

}
