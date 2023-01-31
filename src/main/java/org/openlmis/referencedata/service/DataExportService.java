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

package org.openlmis.referencedata.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.openlmis.referencedata.exception.ValidationMessageException;
import org.openlmis.referencedata.util.messagekeys.MessageKeys;
import org.openlmis.referencedata.web.DataExportParams;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataExportService {

  private static final String FORMATTER_SERVICE_NAME_SUFFIX = "FormatterService";
  private static final String REPOSITORY_NAME_SUFFIX = "Service";

  @Autowired
  private BeanFactory beanFactory;

  /**
   * Return zip archive with files in specific format.
   *
   * @param params query parameters.
   * @return byte data in zip format
   */
  public byte[] exportData(DataExportParams params) {
    try (ByteArrayOutputStream outputStream = toZip(params)) {

      return outputStream.toByteArray();
    } catch (IOException ex) {
      throw new ValidationMessageException(ex, MessageKeys.ERROR_IO, ex.getMessage());
    }
  }

  private ByteArrayOutputStream toZip(DataExportParams params) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ZipOutputStream zip = new ZipOutputStream(baos)) {
      for (Map.Entry<String, ByteArrayOutputStream> file : generateFiles(params).entrySet()) {
        ZipEntry entry = new ZipEntry(file.getKey() + "." + params.getFormat());
        entry.setSize(file.getValue().toByteArray().length);
        zip.putNextEntry(entry);
        zip.write(file.getValue().toByteArray());
      }
      zip.closeEntry();

      return baos;
    } catch (IOException ex) {
      throw new ValidationMessageException(ex, MessageKeys.ERROR_IO, ex.getMessage());
    }
  }

  private Map<String, ByteArrayOutputStream> generateFiles(DataExportParams params) {
    Map<String, ByteArrayOutputStream> output = new HashMap<>();
    String[] filenames = params.getData().split(",");
    for (String file : filenames) {
      output.put(file, generateFile(params.getFormat(), file));
    }
    return output;
  }

  private <T> ByteArrayOutputStream generateFile(String format, String filename) {
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      DataFormatterService formatter = beanFactory.getBean(format
              + FORMATTER_SERVICE_NAME_SUFFIX, DataFormatterService.class);

      ExportableDataService<T> service = beanFactory.getBean(filename
              + REPOSITORY_NAME_SUFFIX, ExportableDataService.class);

      List<T> data = service.findAll();

      formatter.process(output, data, service.getType());
      return output;
    } catch (IOException | BeansException ex) {
      throw new ValidationMessageException(ex, MessageKeys.ERROR_IO, ex.getMessage());
    }
  }

  public interface ExportParams {

    String getFormat();

    String getData();

  }

}


