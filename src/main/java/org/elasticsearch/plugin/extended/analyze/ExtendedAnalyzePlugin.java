package org.elasticsearch.plugin.extended.analyze;

import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.module.extended.analyze.ExtendedAnalyzeModule;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.rest.RestModule;
import org.elasticsearch.rest.action.admin.indices.analyze.RestExtendedAnalyzeAction;

import java.util.Collection;

/**
 * Extended _analyze API Plugin
 */
public class ExtendedAnalyzePlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "extended-analyze";
    }

    @Override
    public String description() {
        return "Extended _analyze API support";
    }

    public void onModule(RestModule restModule) {
        restModule.addRestAction(RestExtendedAnalyzeAction.class);
    }


    @Override
    public Collection<Class<? extends Module>> modules() {
        return ImmutableList.<Class<? extends Module>>of(ExtendedAnalyzeModule.class);
    }
}
