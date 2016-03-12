package za.dsc.grp.lib.common.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class MessageBundleUtil
{
	private static ClassLoader getCurrentClassLoader(Object defaultObject) {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = defaultObject.getClass().getClassLoader();
		}
		return loader;
	}

	public static ResourceBundle getBundle(String bundleName) {

		Locale locale = Locale.getDefault();
		return ResourceBundle.getBundle(bundleName, locale, getCurrentClassLoader(bundleName));
	}

	public static String getMessage(String bundleName, String messageId) {

		ResourceBundle bundle = getBundle(bundleName);
		try {
			return bundle.getString(messageId);
		} catch (MissingResourceException e) {}

		return "!! " + messageId + " !!";

	}

	public static String getMessage(String bundleName, String messageId, Object[] params) {

		ResourceBundle bundle = getBundle(bundleName);
		return getMessage(bundle, messageId, params);
	}

	public static String getMessage(ResourceBundle bundle, String messageId, Object[] params) {

		String label = null;
		Locale locale = Locale.getDefault();
		try {
			label = bundle.getString(messageId);
			if (label != null && params != null) {
				MessageFormat mf = new MessageFormat(label, locale);
				label = mf.format(params, new StringBuffer(), null).toString();
			}
			return label;
		} catch (MissingResourceException e) {}

		return "!! " + messageId + " !!";
	}
}
