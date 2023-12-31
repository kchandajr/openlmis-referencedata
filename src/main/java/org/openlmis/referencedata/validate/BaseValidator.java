/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.referencedata.validate;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import org.openlmis.referencedata.util.Message;
import org.openlmis.referencedata.util.messagekeys.ValidationMessageKeys;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public interface BaseValidator extends Validator {

  /**
   * Rejects if newData and oldData are different.
   * @param errors Errors
   * @param oldData Old data
   * @param newData New data
   * @param field Field
   * @param message Message
   */
  default void rejectIfNotEqual(Errors errors, Object oldData, Object newData, String field,
      String message) {
    if (!Objects.equals(oldData, newData)) {
      rejectValue(errors, field, message);
    }
  }

  /**
   * Reject if value for the given field is empty.
   * @param errors Errors
   * @param field Field
   * @param message Message
   */
  default void rejectIfEmpty(Errors errors, String field, String message) {
    ValidationUtils.rejectIfEmpty(errors, field, message, message);
  }

  /**
   * Reject if value for the given field is empty or whitespace.
   * @param errors Errors
   * @param field Field
   * @param message Message
   */
  default void rejectIfEmptyOrWhitespace(Errors errors, String field, String message) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, message, message);
  }

  default void rejectValue(Errors errors, String field, String message) {
    errors.rejectValue(field, message, message);
  }

  default void rejectValue(Errors errors, String field, String message, String... parameters) {
    errors.rejectValue(field, message, parameters, message);
  }

  /**
   * Reject if value for the given field is null.
   * @param errors Errors
   * @param field Field
   * @param message Message
   */
  default void rejectIfNull(Errors errors, String field, String message) {
    if (errors.getFieldValue(field) == null) {
      errors.rejectValue(field, message, message);
    }
  }

  /**
   * Checks if arguments are not null.
   * @param target Target
   * @param errors Errors
   * @param errorNull errorNull Message if target param is null
   */
  default void verifyArguments(Object target, Errors errors, String errorNull) {
    Message targetMessage = new Message(errorNull);
    checkNotNull(target, targetMessage.toString());
    Message errorsMessage = new Message(ValidationMessageKeys.ERROR_CONTEXTUAL_STATE_NULL);
    checkNotNull(errors, errorsMessage.toString());
  }

}
