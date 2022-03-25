package com.kinetise.data.application.actionmanager;

/**
 * Basic interface for all actions that are executable
 * */
public interface IFunctionCommand {
	/**
     * Contains code that action should execute
     * @param desc Descriptor on which action should be called
     * @return descriptor got from action execution or null
     * */
	Object execute(Object desc);
}
