package org.activiti.examples.explore.interceptor;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandContext;

public class PostCustomInterceptor extends AbstractCommandInterceptor {
    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {

        CommandContext commandContext = Context.getCommandContext();

        assert commandContext != null;
        next.execute(config,command);
        return null;
    }
}
