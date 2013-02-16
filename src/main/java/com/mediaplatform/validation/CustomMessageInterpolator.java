package com.mediaplatform.validation;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.ResourceBundleLocator;
import org.hibernate.validator.util.ReflectionHelper;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: timur
 * Date: 2/16/13
 * Time: 8:18 PM
 */
public class CustomMessageInterpolator extends ResourceBundleMessageInterpolator {
    public CustomMessageInterpolator () {
        super(new MyCustomResourceBundleLocator(), false);
    }

    private static class MyCustomResourceBundleLocator implements ResourceBundleLocator {
        public MyCustomResourceBundleLocator() {
        }

        @Override
        public ResourceBundle getResourceBundle(Locale locale) {
            ClassLoader classLoader = ReflectionHelper.getClassLoaderFromContext();
            ResourceBundle.clearCache(classLoader);
            return ResourceBundle.getBundle("CustomValidationMessages");
        }
    }
}