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

package org.openlmis.referencedata.web.csv.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.referencedata.domain.Code;
import org.openlmis.referencedata.domain.Orderable;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class FormatOrderableTest {

  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private CsvContext csvContext;

  private FormatOrderable formatOrderable;

  @Before
  public void beforeEach() {
    formatOrderable = new FormatOrderable();
  }

  @Test
  public void shouldFormatValidOrderable() {
    Orderable orderable = new Orderable();
    orderable.setProductCode(Code.code("orderable-product-code"));

    String result = formatOrderable.execute(orderable, csvContext);

    assertEquals("orderable-product-code", result);
  }

  @Test
  public void shouldThrownExceptionWhenProductCodeIsNull() {
    Orderable orderable = new Orderable();
    orderable.setProductCode(null);

    expectedEx.expect(SuperCsvCellProcessorException.class);
    expectedEx.expectMessage(String.format("Cannot get product code from '%s'.",
            orderable.toString()));

    formatOrderable.execute(orderable, csvContext);
  }

  @Test
  public void shouldThrownExceptionWhenValueIsNotOrderableType() {
    String invalid = "invalid-type";

    expectedEx.expect(SuperCsvCellProcessorException.class);
    expectedEx.expectMessage(String.format("Cannot get product code from '%s'.", invalid));

    formatOrderable.execute(invalid, csvContext);
  }

}