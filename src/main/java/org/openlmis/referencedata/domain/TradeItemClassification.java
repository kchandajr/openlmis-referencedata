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

package org.openlmis.referencedata.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Represents a trade item identification using a classification system.
 */
@Entity
@Table(name = "trade_item_classifications", schema = "referencedata",
    uniqueConstraints = @UniqueConstraint(
        name = "unq_trade_item_classifications_system",
        columnNames = {"tradeitemid", "classificationsystem"}))
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"tradeItem"})
public class TradeItemClassification extends BaseEntity {

  @ManyToOne
  private TradeItem tradeItem;

  @Getter
  @Setter
  private String classificationSystem;

  @Getter
  @Setter
  private String classificationId;

  /**
   * Constructs a classification for a trade item.
   * @param tradeItem the trade item this classification belongs to
   * @param classificationSystem the classification system
   * @param classificationId the id of the classification system
   */
  TradeItemClassification(TradeItem tradeItem, String classificationSystem,
                                 String classificationId) {
    this.tradeItem = tradeItem;
    this.classificationSystem = classificationSystem;
    this.classificationId = classificationId;
  }
}
