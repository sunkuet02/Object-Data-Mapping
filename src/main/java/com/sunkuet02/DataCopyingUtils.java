package com.sunkuet02;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sun on 4/4/17.
 */
public class DataCopyingUtils {

    /**
     * @param source      The object needs to copy to destination. Data are copied to destination with repect to the
     *                    variable name, or Annotation mapping
     * @param destination The object where source data needs to copy
     * @return The modified destination object
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public static Object copyDataToObject(Object source, Object destination) throws IntrospectionException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class<? extends Object> sourceClass = source.getClass();
        Class<? extends Object> destinationClass = destination.getClass();

        List<Map<String, String>> annotationMapList = getAnnotationMapList(destinationClass);
        destination = assignValuesToDestinationAsGetterMethod(source, destination);
        destination = assignValuesToDestinationAsAnnotationList(source, destination, annotationMapList);

        return destination;
    }

    public static Object assignValuesToDestinationAsAnnotationList(Object source, Object destination,
                                                                   List<Map<String, String>> annotationMapList) throws InvocationTargetException, IllegalAccessException {
        for (Map<String, String> annotationMap : annotationMapList) {
            String name = null;
            String replacedBy = null;
            for (String key : annotationMap.keySet()) {
                String value = annotationMap.get(key);
                value = capitalize(value);

                if (key == "name") {
                    name = value;
                } else if (key == "replacedBy") {
                    replacedBy = value;
                }

            }
            Method getterMethodOfSource = getNonParameterizedMethodWithName(source, "get" + replacedBy);

            Method setterMethodOfDestination = getParameterizedMethodWithName(destination,
                "set" + name, getterMethodOfSource.getReturnType());

            if (getterMethodOfSource == null || setterMethodOfDestination == null) continue;
            ;

            Object property = getterMethodOfSource.invoke(source);

            setterMethodOfDestination.invoke(destination, property);
        }

        return destination;
    }

    public static String capitalize(final String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    /**
     *
     * @param source The source object from which data to be copied to destination object
     * @param destination The destination object to which data to be copied from source object
     * @return The desired object with data copy from source to destination with the same variable name or
     *          same getter method
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     */
    public static Object assignValuesToDestinationAsGetterMethod(Object source, Object destination) throws InvocationTargetException, IllegalAccessException, IntrospectionException {
        Class<? extends Object> destinationClass = destination.getClass();

        for (PropertyDescriptor propertyDescriptor : Introspector
            .getBeanInfo(destinationClass, Object.class).getPropertyDescriptors()) {

            // Get the name of the destinator class setter method
            Method destinationSetterMethod = propertyDescriptor.getWriteMethod();
            String destinationSetterMethodName = destinationSetterMethod.getName();

            //Get the getter method of source class
            Method sourceGetterMethod = getNonParameterizedMethodWithName(source,
                "get" + destinationSetterMethodName.substring(3));

            //if sourceGetterMethod is null then no value to get or set
            if (sourceGetterMethod == null) continue;

            // getting the property value of source method
            Object property = sourceGetterMethod.invoke(source);
            if (destinationSetterMethod != null) {
                // Assign value to destination object
                destinationSetterMethod.invoke(destination, property);
            }
        }
        return destination;
    }

    /**
     *
     * @param objectClass The class from which annotation to be extracted.
     * @return A List of Map<String,String> which contains the mapping of CopyObjectData annotation values
     * applied on the source class.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static List<Map<String, String>> getAnnotationMapList(Class<? extends Object> objectClass) throws InvocationTargetException, IllegalAccessException {
        List<Map<String, String>> annotationMapList = new ArrayList<Map<String, String>>();

        for (Field field : objectClass.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                Map<String, String> annotationMap = new HashMap<String, String>();
                if (annotation.annotationType() == CopyObjectData.class) {
                    for (Method method : CopyObjectData.class.getDeclaredMethods()) {
                        Object value = method.invoke(annotation, (Object[]) null);
                        annotationMap.put(method.getName(), (String) value);
                    }
                }
                annotationMapList.add(annotationMap);
            }
        }
        return annotationMapList;
    }

    /**
     * @param object The object we want to extract to find the method name
     * @param name   Name of the method we want to find
     * @return the expected method, if method not found then return null
     */
    public static Method getNonParameterizedMethodWithName(Object object, String name) {
        Method methodToFind = null;
        try {
            methodToFind = object.getClass().getMethod(name);
        } catch (NoSuchMethodException e) {
        }
        return methodToFind;
    }

    /**
     * @param object         The object we want to extract to find the method name
     * @param name           Name of the method we want to find
     * @param parameterTypes the parameter types of the expected method
     * @return the expected method, if method not found then return null
     */
    public static Method getParameterizedMethodWithName(Object object, String name, Class<?> parameterTypes) {
        Method methodToFind = null;
        try {
            methodToFind = object.getClass().getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            System.out.println("No method found on " + object.getClass() + " with name : *" + name + "*");
        }
        return methodToFind;
    }
}
