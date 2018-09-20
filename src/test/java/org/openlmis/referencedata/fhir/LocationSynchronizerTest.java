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

package org.openlmis.referencedata.fhir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICreate;
import ca.uhn.fhir.rest.gclient.ICreateTyped;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.IUntypedQuery;
import ca.uhn.fhir.rest.gclient.IUpdate;
import ca.uhn.fhir.rest.gclient.IUpdateTyped;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.referencedata.testbuilder.FacilityDataBuilder;

@RunWith(MockitoJUnitRunner.class)
public abstract class LocationSynchronizerTest<R extends IBaseResource, B extends IBaseBundle> {

  private static final String FHIR_SERVER_URL = "http://localhost/fhir";
  private static final String SERVICE_URL = "http://localhost";

  @Mock
  private FhirContext context;

  @Mock
  private IGenericClient client;

  @Mock
  private ICreate create;

  @Mock
  private IUpdate update;

  @Mock
  private IUntypedQuery search;

  @Mock
  private IQuery baseQuery;

  @Mock
  private IQuery query;

  @Mock
  private ICreateTyped createTyped;

  @Mock
  private IUpdateTyped updateTyped;

  @Mock
  private IClientInterceptor authInterceptor;

  private LocationSynchronizer<R, B> synchronizer;

  private Location olmisLocation;
  private R fhirLocation;

  private B emptyBundle;
  private B bundle;

  @Before
  public void setUp() {
    synchronizer = getSynchronizer();
    olmisLocation = Location.newInstance(SERVICE_URL, new FacilityDataBuilder().build());
    fhirLocation = getFhirLocation();

    emptyBundle = getEmptyBundle();
    bundle = getBundle(fhirLocation);

    synchronizer
        .withContext(context)
        .withFhirServerUrl(FHIR_SERVER_URL)
        .withServiceUrl(SERVICE_URL)
        .withAuthInterceptor(authInterceptor);

    when(context.newRestfulGenericClient(FHIR_SERVER_URL)).thenReturn(client);

    when(client.create()).thenReturn(create);
    when(client.update()).thenReturn(update);

    when(create.resource(any(IBaseResource.class))).thenReturn(createTyped);
    when(createTyped.prettyPrint()).thenReturn(createTyped);
    when(createTyped.encodedJson()).thenReturn(createTyped);

    when(update.resource(any(IBaseResource.class))).thenReturn(updateTyped);
    when(updateTyped.encodedJson()).thenReturn(updateTyped);

    when(client.search()).thenReturn(search);
    when(search.forResource(fhirLocation.getClass())).thenReturn(baseQuery);
    when(baseQuery.where(any(ICriterion.class))).thenReturn(baseQuery);
    when(baseQuery.returnBundle(emptyBundle.getClass())).thenReturn(query);
  }

  @Test
  public void shouldGetInstanceBasedOnFhirVersion() {
    assertThat(LocationSynchronizer.getInstance(getFhirVersion()))
        .isInstanceOf(synchronizer.getClass());
  }

  @Test
  public void shouldCreateResource() {
    when(query.execute()).thenReturn(emptyBundle);
    synchronizer.synchronize(olmisLocation, fhirLocation);

    verify(client).create();
    verify(create).resource(any(IBaseResource.class));
    verify(createTyped).prettyPrint();
    verify(createTyped).encodedJson();
    verify(createTyped).execute();

    verify(client, never()).update();
    verifyZeroInteractions(update, updateTyped);
  }

  @Test
  public void shouldUpdateResource() {
    when(query.execute()).thenReturn(bundle);
    synchronizer.synchronize(olmisLocation, fhirLocation);

    verify(client, never()).create();
    verifyZeroInteractions(create, createTyped);

    verify(client).update();
    verify(update).resource(any(IBaseResource.class));
    verify(updateTyped).encodedJson();
    verify(updateTyped).execute();
  }

  abstract LocationSynchronizer<R, B> getSynchronizer();

  abstract FhirVersionEnum getFhirVersion();

  abstract R getFhirLocation();

  abstract B getEmptyBundle();

  abstract B getBundle(R resource);

}
