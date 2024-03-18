package ar.mikellbobadilla.utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ObjectMapper {

    private <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>[] typesArguments) {
        try {
            return clazz.getConstructor(typesArguments);
        } catch (NoSuchMethodException exc) {
            System.err.println("Error getting constructor: " + exc.getMessage());
            throw new RuntimeException(exc);
        }
    }

    private <T> T createInstance(Constructor<T> constructor, Object[] arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exc) {
            if (exc instanceof InstantiationException) {
                System.err.println("Can't create a new instance: " + exc.getMessage());
            } else if (exc instanceof IllegalAccessException) {
                System.err.println("Application tries to reflectively create an instance: " + exc.getMessage());
            } else {
                System.err.println("Can't invoque constructor: " + exc.getMessage());
            }
            throw new RuntimeException(exc);
        }
    }

    public <T, S> T mapData(Class<T> target, S source) {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Object[] arguments = new Object[target.getDeclaredFields().length];
        Class<?>[] types = new Class[target.getDeclaredFields().length];

        if (target.isRecord()) {
            int count = 0;
            for (Field sourceField : sourceFields) {
                Object value = null;
                try {
                    Field targetField = target.getDeclaredField(sourceField.getName());
                    sourceField.setAccessible(true);
                    targetField.setAccessible(true);

                    /* Add types to arguments */
                    types[count] = targetField.getType();

                    if (isTypeEquals(targetField, sourceField)) {
                        value = sourceField.get(source);
                        if (value != null) {
                            arguments[count] = value;
                        }
                    } else if (isMatchDate(targetField) && isMatchString(sourceField)) {
                        value = sourceField.get(source);

                        if (value != null) {
                            value = parseStringToDate((String) value);
                        }

                    } else if (isMatchString(targetField) && isMatchDate(sourceField)) {

                        value = sourceField.get(source);
                        if (value != null) {
                            value = parseDateToString((Date) value);
                        }

                    }
                    arguments[count] = value;
                    /* Add types to arguments */
                    types[count] = targetField.getType();
                    targetField.setAccessible(false);
                    sourceField.setAccessible(false);

                } catch (NoSuchFieldException | IllegalAccessException exc) {
                    if (exc instanceof NoSuchFieldException) {
                        continue;
                    } else
                        throw new RuntimeException(exc);
                }
                count++;
            }

            Constructor<T> targetConstructor = getConstructor(target, types);
            /* Return newInstance */
            return createInstance(targetConstructor, arguments);

        } else {
            try {

                Constructor<T> targetConstructor = target.getConstructor();
                T newInstance = targetConstructor.newInstance();
                newInstance = mapData(newInstance, source);
                return newInstance;

            } catch (NoSuchMethodException exc) {
                System.err.println("If is Class please add void constructor " + exc.getMessage());
                throw new RuntimeException(exc);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    /**
     * Update the data from the source object to the target object.
     *
     * @param target The target object
     * @param source The source object
     * @throws IllegalAccessException If the field is not accessible
     */
    public <T, S> T mapData(T target, S source) throws IllegalAccessException {
        Field[] sourceFields = getFields(source.getClass());

        for (Field sourceField : sourceFields) {
            try {
                Field targetField = target.getClass().getDeclaredField(sourceField.getName());
                sourceField.setAccessible(true);
                targetField.setAccessible(true);

                if (targetField.getType().equals(sourceField.getType())) {

                    Object value = sourceField.get(source);
                    if (value != null) {
                        targetField.set(target, value);
                    }

                } else if (isMatchDate(targetField) && isMatchString(sourceField)) {

                    Object value = sourceField.get(source);
                    if (value != null) {
                        targetField.set(target, parseStringToDate((String) value));
                    }

                } else if (isMatchString(targetField) && isMatchDate(sourceField)) {
                    Object value = sourceField.get(source);

                    if (value != null) {
                        targetField.set(target, parseDateToString((Date) value));
                    }

                } else if (isMatchString(targetField) && isMatchBigDecimal(sourceField)) {
                    Object value = sourceField.get(source);
                    if (value != null) {
                        targetField.set(target, parseBigDecimalToString((BigDecimal) value));
                    }
                } else if (isMatchBigDecimal(targetField) && isMatchString(sourceField)) {
                    Object value = sourceField.get(source);
                    if (value != null) {
                        targetField.set(target, parseStringToBigDecimal((String) value));
                    }
                }
                targetField.setAccessible(false);
                sourceField.setAccessible(false);
            } catch (NoSuchFieldException e) {
                continue;
            }
        }
        return target;
    }

    private Field[] getFields(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    private boolean isMatchDate(Field field) {
        return field.getType().equals(Date.class);
    }

    private boolean isMatchBigDecimal(Field field) {
        return field.getType().equals(BigDecimal.class);
    }

    private boolean isMatchString(Field field) {
        return field.getType().equals(String.class);
    }

    private boolean isTypeEquals(Field field, Field field2) {
        return field.getType().equals(field2.getType());
    }

    private String parseDateToString(Date value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(value);
    }

    private Date parseStringToDate(String value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(value);
        } catch (ParseException exc) {
            /* Todo: Create DateFormat Exception to response client */
            throw new RuntimeException(exc);
        }
    }

    private BigDecimal parseStringToBigDecimal(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    private String parseBigDecimalToString(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
