package org.elasticsearch.module.extended.analyze;

import org.elasticsearch.action.GenericAction;
import org.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeAction;
import org.elasticsearch.action.admin.indices.extended.analyze.TransportExtendedAnalyzeAction;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.multibindings.MapBinder;

/**
 * Created with IntelliJ IDEA.
 * User: johtani
 * Date: 2013/10/25
 * Time: 0:46
 * To change this template use File | Settings | File Templates.
 */
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
