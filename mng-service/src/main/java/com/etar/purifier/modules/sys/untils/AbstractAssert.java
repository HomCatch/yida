
package com.etar.purifier.modules.sys.untils;

import com.etar.purifier.common.validation.XException;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据校验
 */
public abstract class AbstractAssert {

    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new XException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new XException(message);
        }
    }
}
