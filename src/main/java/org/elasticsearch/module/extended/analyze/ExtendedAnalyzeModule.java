/*
 * Copyright 2013 Jun Ohtani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.elasticsearch.module.extended.analyze;

import org.elasticsearch.action.GenericAction;
import org.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeAction;
import org.elasticsearch.action.admin.indices.extended.analyze.TransportExtendedAnalyzeAction;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.multibindings.MapBinder;

public class ExtendedAnalyzeModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TransportExtendedAnalyzeAction.class).asEagerSingleton();

        MapBinder<GenericAction, TransportAction> transportActionsBinder =
            MapBinder.newMapBinder(binder(), GenericAction.class, TransportAction.class);

        transportActionsBinder.addBinding(ExtendedAnalyzeAction.INSTANCE).to(TransportExtendedAnalyzeAction.class).asEagerSingleton();

        MapBinder<String, GenericAction> actionsBinder = MapBinder.newMapBinder(binder(), String.class, GenericAction.class);
        actionsBinder.addBinding(ExtendedAnalyzeAction.NAME).toInstance(ExtendedAnalyzeAction.INSTANCE);

    }
}
