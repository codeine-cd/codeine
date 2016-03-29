package codeine.servlets.api_servlets.angular;

import codeine.utils.StringUtils;

/**
 * Created by rezra3 on 3/29/16.
 */
public class OptionalParameter {

    public static boolean getValue(String parameterValue) {
        return StringUtils.isEmpty(parameterValue) ?
                true : Boolean.valueOf(parameterValue);
    }
}
