package org.openlmis.referencedata.domain;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.openlmis.referencedata.util.View;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "facilities", schema = "referencedata")
@NoArgsConstructor
public class Facility extends BaseEntity {

  public static final String TEXT = "text";

  @JsonView(View.BasicInformation.class)
  @Column(nullable = false, unique = true, columnDefinition = TEXT)
  @Getter
  @Setter
  private String code;

  @Column(columnDefinition = TEXT)
  @Getter
  @Setter
  private String name;

  @Column(columnDefinition = TEXT)
  @Getter
  @Setter
  private String description;

  @ManyToOne
  @JoinColumn(name = "geographiczoneid", nullable = false)
  @Getter
  @Setter
  private GeographicZone geographicZone;

  @ManyToOne
  @JoinColumn(name = "typeid", nullable = false)
  @Getter
  @Setter
  private FacilityType type;

  @ManyToOne
  @JoinColumn(name = "operatedbyid")
  @Getter
  @Setter
  private FacilityOperator operator;

  @Column(nullable = false)
  @Getter
  @Setter
  private Boolean active;



  @OneToMany
  @JoinColumn(name = "programId")
  @Getter
  @Setter
  private List<Program> supportedPrograms;
}
