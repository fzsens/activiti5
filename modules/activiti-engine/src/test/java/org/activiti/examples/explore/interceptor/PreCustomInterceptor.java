package org.activiti.examples.explore.interceptor;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;

public class PreCustomInterceptor extends AbstractCommandInterceptor {
    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        //assert Context.getCommandContext() == null;
        next.execute(config,command);
        return null;
    }
}
