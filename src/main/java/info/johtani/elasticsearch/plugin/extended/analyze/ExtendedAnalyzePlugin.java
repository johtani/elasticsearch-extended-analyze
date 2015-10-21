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
package info.johtani.elasticsearch.plugin.extended.analyze;

import org.elasticsearch.common.inject.Module;
import info.johtani.elasticsearch.module.extended.analyze.ExtendedAnalyzeModule;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestModule;
import info.johtani.elasticsearch.rest.action.admin.indices.analyze.RestExtendedAnalyzeAction;

import java.util.Collection;
import java.util.Collections;

/**
 * Extended _analyze API Plugin
 */
public class ExtendedAnalyzePlugin extends Plugin {

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
    public Collection<Module> nodeModules() {
        return Collections.<Module>singletonList(new ExtendedAnalyzeModule());
    }
}
